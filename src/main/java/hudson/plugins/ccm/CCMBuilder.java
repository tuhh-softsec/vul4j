/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.ccm;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.plugins.ccm.config.CCMConfigCallable;
import hudson.plugins.ccm.config.CCMResultCallable;
import hudson.plugins.ccm.parser.Ccm;
import hudson.plugins.ccm.parser.CCMParser;
import hudson.plugins.ccm.parser.CCMReport;
import hudson.plugins.ccm.util.Messages;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * <p>
* When the user configures the project and enables this builder,
* {@link CCMBuilderDescriptor} newInstance() is invoked
* and a new {@link CcmBuilder} is created. The created
* instance is persisted to the project configuration XML by using
* XStream, so this allows you to use instance fields (like {@link #ccmName})
* to remember the configuration.</p>
*  
* @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
* @since 1.0
*/
public class CCMBuilder 
extends Builder 
implements Serializable
{

	/**
	 * Identifies {@link Ccm} to be used.
	 */
    private final String ccmName;
    /**
     * The list of source folders CCM must scan.
     */
    private final String srcFolders;
    /**
     * List of files that CCM must not scan.
     */
    private final String excludeFiles;
    /**
     * List of folders that CCM must not scan.
     */
    private final String excludeFolders;
    /**
     * List of functions that CCM must not scan.
     */
    private final String excludeFunctions;
    /**
     * Whether CCM should traverse the directories recursivelly or not.
     */
    private final Boolean recursive;
    /**
     * Whether CCM should output its report in XML format or not.
     */
    private final Boolean outputXml;
    /**
     * Maximum of metrics to be shown in the report. 
     */
    private final String numMetrics;
	
    @Extension
    public static final CCMBuilderDescriptor DESCRIPTOR = new CCMBuilderDescriptor();
    
    /**
     * Name of generated config file for CCM.
     */
    public final static String CCM_CONFIG_FILE = "ccm.config.xml";
    
    /**
     * Name of generated result file of CCM.
     */
    public static final String CCM_RESULT_FILE = "ccm.result.xml";
	
    /**
     * Default number of metrics.
     */
    public static final Integer DEFAULT_NUMBER_OF_METRICS = 30;
	
    /**
     * Process success exit code.
     */
    protected static final int EXIT_SUCCESS = 0;
    
    @DataBoundConstructor
    public CCMBuilder(
    		String ccmName, 
    		String srcFolders,
    		String excludeFiles, 
    		String excludeFolders, 
    		String excludeFunctions, 
    		Boolean recursive, 
    		Boolean outputXml, 
    		String numMetrics) 
    {
		super();
		this.ccmName = ccmName;
		this.srcFolders = srcFolders;
		this.excludeFiles = excludeFiles;
		this.excludeFolders = excludeFolders;
		this.excludeFunctions = excludeFunctions;
		this.recursive = recursive;
		this.outputXml = outputXml;
		this.numMetrics = ((numMetrics == null || numMetrics.length()<=0) ? DEFAULT_NUMBER_OF_METRICS.toString() : numMetrics);
	}
    
    public String getCcmName() 
    {
		return ccmName;
	}

	public String getSrcFolders() 
	{
		return srcFolders;
	}
	
	public String getExcludeFiles()
	{
		return excludeFiles;
	}
	
	public String getExcludeFolders()
	{
		return excludeFolders;
	}
	
	public String getExcludeFunctions()
	{
		return excludeFunctions;
	}

	public Boolean isRecursive() 
	{
		return recursive;
	}
	
	public Boolean getRecursive() 
	{
		return recursive;
	}

	public Boolean isOutputXml() 
	{
		return outputXml;
	}

	public String getNumMetrics() 
	{
		return numMetrics;
	}
	
	public Descriptor<Builder> getDescriptor()
	{
		return DESCRIPTOR;
	}
	
	/**
	 * <p>Even though we may have many installations of the CCM in Hudson, CCM 
	 * Plugin grabs the first one that it finds.</p>
	 * 
	 * @return The {@link CCMBuilderInstallation} containing details of the 
	 * CCM installed.
	 */
	public CCMBuilderInstallation getCCM()
    {
		CCMBuilderInstallation foundInstallation = null;
    	
    	for ( CCMBuilderInstallation installation : DESCRIPTOR.getInstallations() )
    	{
    		if ( this.getCcmName() != null && installation.getName().equals(this.getCcmName()))
    		{
    			foundInstallation =  installation;
    		}
    	}
    	
    	return foundInstallation;
    }
    
	
	/**
	 * CCM doesn't need to continue if the build's status is ABORTED or FAILURE.
	 * 
	 * @param result
	 * @return true if build status is not ABORTED or FAILURE.
	 */
	protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }

	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#getProjectAction(hudson.model.AbstractProject)
	 */
	@Override
	public Action getProjectAction( AbstractProject<?, ?> project )
	{
		return new CCMProjectAction( project );
	}
	
	/**
	 * <p>Called when the job is executed.</p>
	 * 
	 * <p>It calls the CCM.exe executable passing the config xml file created using 
	 * the inputs provided by the user. Then it redirects the output of the 
	 * command (using >) to a new file (overwriting it if already created).</p>
	 * 
	 * <p>Later this output xml if processed by another extension point, the 
	 * {@link CCMPublisher}.</p>
	 */
	@Override
	public boolean perform(
			final AbstractBuild<?, ?> build, 
			final Launcher launcher,
			final BuildListener listener ) 
	throws InterruptedException, IOException 
	{
		// List of arguments
		final ArgumentListBuilder args = new ArgumentListBuilder();
    	
		// CCM installation
    	final CCMBuilderInstallation installation = getCCM();
    	
    	// ------------------------- 
    	// Check CCM installation    
    	// ------------------------- 
    	if ( installation == null )
    	{
    		listener.error( Messages.CCM_Builder_MissingCCMInstallation() );
    		build.setResult(Result.FAILURE);
    		return false;
    	} 
    	
    	// ------------------------- 
    	// Preparing arguments list   
    	// ------------------------- 
    	
    	// Path to CCM.exe
    	final String pathToCCM = installation.getExecutable(launcher);
    	listener.getLogger().println( Messages.CCM_Builder_PathToCCM( pathToCCM ) );
    	args.add("\"");
    	args.add(pathToCCM);		
    	
    	final FilePath workspace = build.getWorkspace();
    	// ------------------------- 
    	// Creating CCM config file   
    	// ------------------------- 
        
    	// create project ccm config file and result file
        
        CCMConfigCallable ccmConfigGenerator = new CCMConfigCallable( srcFolders, excludeFiles, excludeFolders, excludeFunctions, recursive, numMetrics, listener );
        String ccmConfigFile = workspace.act( ccmConfigGenerator );
    	args.add( ccmConfigFile );
    	
    	args.add( ">" );
    	
    	CCMResultCallable ccmResultCallable = new CCMResultCallable();
    	String ccmResultFile = workspace.act( ccmResultCallable );
    	args.add( ccmResultFile );
    	
    	//According to the Ant builder source code, in order to launch a program 
        //from the command line in windows, we must wrap it into cmd.exe.  This 
        //way the return code can be used to determine whether or not the build failed.
        if( ! launcher.isUnix() ) // maybe user is using Wine? 
        {
            args.prepend("cmd.exe","/C");
            args.add( "&&", "exit", "%%ERRORLEVEL%%" );
        } 
        args.add("\"");

		// ------------------------- 
    	// Executing CCM    
    	// ------------------------- 
        
        listener.getLogger().println( Messages.CCM_Builder_ExecutingCCMCommand(args.toStringWithQuote()) );
        
        try 
        {
            Map<String,String> env = build.getEnvironment(listener);
            int exitCode = launcher.launch().cmds(args).envs(env).stdout(listener).pwd(build.getModuleRoot()).join();
            
            // If the returned ccm status is equals to 0, call the publishing results method
            Boolean ccmExecuteSuccess = exitCode == EXIT_SUCCESS;
            
            if( ccmExecuteSuccess )
            {
            	this.publishResults(build, listener, workspace);
            }
            
            return ccmExecuteSuccess;
        }
        catch (Exception e) 
        {
            e.printStackTrace( listener.error( Messages.CCM_Builder_CCMCommandExecutionFailed()) );
            build.setResult(Result.FAILURE);
            return false;
        }
	}
	
	/**
	 * Publish of CCM results.
	 * @param build
	 * @param listener
	 * @param workspace
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected void publishResults(AbstractBuild<?, ?> build,
					   		      BuildListener listener,
					   		      FilePath workspace) 
	throws InterruptedException, IOException 
	{
		listener.getLogger().println( Messages.CCM_Publisher_PerformingPublisher() );
		
		PrintStream logger = listener.getLogger();
		
		CCMParser parser = new CCMParser(logger);
		CCMReport report = null;
		
		logger.println( Messages.CCM_Publisher_GeneratingReport() );
		
		try
		{
            report = workspace.act(parser);
            CCMResult result = new CCMResult(report, build);
            CCMBuildAction buildAction = new CCMBuildAction(build, result);
            build.addAction( buildAction );
        }
		catch(IOException ioe)
		{
            ioe.printStackTrace(logger);
        }
		catch(InterruptedException ie)
		{
            ie.printStackTrace(logger);
        }
	}
	
}
