package hudson.plugins.ccm;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
  * <p>
 * When the user configures the project and enables this builder,
 * {@link CCMDescriptor#newInstance(StaplerRequest)} is invoked
 * and a new {@link CcmBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(Build, Launcher, BuildListener)} method
 * will be invoked. 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 7 april, 2010 
 */
public class CCMBuilder 
extends Builder {

	/**
	 * Identifies {@link CCM} to be used.
	 */
    private final String ccmName;
    private final String srcFolder;
    private final Boolean recursive;
    private final Boolean outputXml;
    private final String numMetrics;
    
    @Extension
    public static final CCMDescriptor DESCRIPTOR = new CCMDescriptor();
    
    @DataBoundConstructor
    public CCMBuilder(String ccmName, String srcFolder,
    		Boolean recursive, Boolean outputXml, String numMetrics) {
		super();
		this.ccmName = ccmName;
		this.srcFolder = srcFolder;
		this.recursive = recursive;
		this.outputXml = outputXml;
		this.numMetrics = ((numMetrics == null || numMetrics.length()<=0) ? "30" : numMetrics);
	}

	public String getCcmName() {
		return ccmName;
	}

	public String getSrcFolder() {
		return srcFolder;
	}

	public Boolean isRecursive() {
		return recursive;
	}
	
	public Boolean getRecursive() {
		return recursive;
	}

	public Boolean isOutputXml() {
		return outputXml;
	}

	public String getNumMetrics() {
		return numMetrics;
	}
	
	public Descriptor<Builder> getDescriptor()
	{
		return DESCRIPTOR;
	}
    
    public CCMInstallation getCCM()
    {
    	CCMInstallation foundInstallation = null;
    	
    	for ( CCMInstallation installation : DESCRIPTOR.getInstallations() )
    	{
    		if ( this.getCcmName() != null && installation.getName().equals(this.getCcmName()))
    		{
    			foundInstallation =  installation;
    		}
    	}
    	
    	return foundInstallation;
    }
    
    private String yesOrNo(boolean flag)
    {
    	if(flag)
    	{
    		return "yes";
    	}
    	return "no";
    }
    
    private String getSrcFolderRelativeToWorkspace(String srcFolder, FilePath workspace )
    {
    	return new File(workspace.getName(), srcFolder).getAbsolutePath();
    }
    
    private void createXMLConfig(FilePath workspace, BuildListener listener) 
	throws IOException
	{
		// TBD: the file name is hard coded... fix it later.
		File ccmConfigFile = new File(workspace.getName(), "ccm.config.xml");
		
		listener.getLogger().println("Creating CCM config file " + ccmConfigFile.getAbsolutePath());
		//TBD: improve this
		ccmConfigFile.createNewFile();
		
		StringBuffer buffer = new StringBuffer();
		
		// TBD: eck! correct this. later...
		buffer.append("<ccm>\n");
		buffer.append("<exclude></exclude>\n");
		buffer.append("<analyze>\n");
		buffer.append("<folder>"+this.getSrcFolderRelativeToWorkspace(this.getSrcFolder(), workspace)+"</folder>\n");
		buffer.append("</analyze>\n");
		buffer.append("<recursive>"+this.yesOrNo(this.isRecursive())+"</recursive>\n");
		buffer.append("<outputXML>yes</outputXML>\n");
		buffer.append("<numMetrics>"+this.getNumMetrics()+"</numMetrics>\n");
		buffer.append("</ccm>\n");
		
		listener.getLogger().println("Writing CCM configuration into file");
		listener.getLogger().println(buffer.toString());
		
		FileWriter writer = new FileWriter(ccmConfigFile);
		writer.append(buffer.toString());
		writer.flush(); // TBD: do it better.
		writer.close();
		
	}
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
    		BuildListener listener) throws InterruptedException, IOException {
    	
    	ArgumentListBuilder args = new ArgumentListBuilder();
    	
    	CCMInstallation installation = getCCM();
    	String execName = installation.getPathToCCM();
    	if ( installation == null )
    	{
    		listener.fatalError("Invalid CCM installation");
    		return false;
    	} else {
    		File exec = installation.getExecutable();
    		if ( ! installation.getExists() )
    		{
    			listener.fatalError(exec+" doesn't exist");
    			return false;
    		}
    		listener.getLogger().println("Path To CCM.exe: " + execName);
    		args.add(execName);
    	}
    	
    	// create project ccm config file
    	FilePath workspace = build.getWorkspace();
    	try {
			createXMLConfig(workspace, listener);
		} catch (IOException e) {
			Util.displayIOException(e,listener);
			e.printStackTrace( listener.fatalError("Error creating CCM config file") );
			return false;
		}
		args.add(new File(workspace.getName(), "ccm.config.xml"));
		
		//According to the Ant builder source code, in order to launch a program 
        //from the command line in windows, we must wrap it into cmd.exe.  This 
        //way the return code can be used to determine whether or not the build failed.
        if(!launcher.isUnix()) {
            args.prepend("cmd.exe","/C");
            args.add("&&","exit","%%ERRORLEVEL%%");
        } else {
        	listener.fatalError("CCM can be run only in Win platforms.");
        	return false;
        }
        
        // Try to execute the command
        listener.getLogger().println("Executing command: "+args.toStringWithQuote());
        try {
            Map<String,String> env = build.getEnvironment(listener);
            int r = launcher.launch().cmds(args).envs(env).stdout(listener).pwd(build.getModuleRoot()).join();
            return r==0;
        } catch (IOException e) {
            Util.displayIOException(e,listener);
            e.printStackTrace( listener.fatalError("command execution failed") );
            return false;
        }
        
    }
    
}
