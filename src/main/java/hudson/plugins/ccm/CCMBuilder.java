/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 7 april, 2010 
 */
package hudson.plugins.ccm;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.util.CCMUtil;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


/**
 * <p>
* When the user configures the project and enables this builder,
* {@link CCMBuilderDescriptor#newInstance(StaplerRequest)} is invoked
* and a new {@link CcmBuilder} is created. The created
* instance is persisted to the project configuration XML by using
* XStream, so this allows you to use instance fields (like {@link #ccmName})
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
extends Builder 
{

	/**
	 * Identifies {@link CCM} to be used.
	 */
    private final String ccmName;
    /**
     * The source folder CCM must scan.
     */
    private final String srcFolder;
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
    public static final CCMBuilderDescription DESCRIPTOR = new CCMBuilderDescription();
    
    @DataBoundConstructor
    public CCMBuilder(
    		String ccmName, 
    		String srcFolder,
    		Boolean recursive, 
    		Boolean outputXml, 
    		String numMetrics) 
    {
		super();
		this.ccmName = ccmName;
		this.srcFolder = srcFolder;
		this.recursive = recursive;
		this.outputXml = outputXml;
		this.numMetrics = ((numMetrics == null || numMetrics.length()<=0) ? "30" : numMetrics);
	}
    
    public String getCcmName() 
    {
		return ccmName;
	}

	public String getSrcFolder() 
	{
		return srcFolder;
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
    			//TOTHINK Check if the executable exists.
    			foundInstallation =  installation;
    		}
    	}
    	
    	return foundInstallation;
    }
    
	/**
	 * <p>Creates the XML input file for CCM.exe. It uses data provided by 
	 * user in the project configuration page (e.g.:{@link #recursive}).</p> 
	 * @param workspace
	 * @param listener
	 * @throws IOException
	 */
    private void createXMLConfig(FilePath workspace, BuildListener listener) 
	throws IOException
	{
		// TBD: the file name is hard coded... fix it later.
		File ccmConfigFile = new File(workspace.getRemote(), "ccm.config.xml");
		
		listener.getLogger().println("Creating CCM config file " + ccmConfigFile.getAbsolutePath());
		//TBD: improve this
		ccmConfigFile.createNewFile();
		
		StringBuffer buffer = new StringBuffer();
		
		// TBD: eck! correct this. later...
		buffer.append("<ccm>\n");
		buffer.append("<exclude></exclude>\n");
		buffer.append("<analyze>\n");
		buffer.append("<folder>"+CCMUtil.getSrcFolderRelativeToWorkspace(this.getSrcFolder(), workspace)+"</folder>\n");
		buffer.append("</analyze>\n");
		buffer.append("<recursive>"+CCMUtil.yesOrNo(this.isRecursive())+"</recursive>\n");
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
	
    /**
     * <p>The CCM executable, by default, prints a header before the results in 
     * XML format.</p>
     * 
     * @param file The CCM result file.
     * @param logger Log.
     */
    private void fixResultFile(File file, PrintStream logger) 
	{
		BufferedReader reader = null;
		
		StringBuffer newFile = new StringBuffer();
		
		logger.println("Removing CCM header line...");
		try 
		{
			reader = new BufferedReader( new FileReader(file) );
			String line = null;
			
			boolean found = false;
			while((line = reader.readLine()) != null )
			{
				if ( !found && line.contains("Loading configuration from") )
				{
					found = true;
					//newFile.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
				} else {
					newFile.append(line);
					//TOTHINK CCM is Win32 only. What if it is running in Wine?
					// Perhaps we should use System.getProperty... line.separator?
					newFile.append("\n");
				}				
			}
			
		} catch (IOException e) {
			logger.println("Error removing header from CCM result file ["+file+"]: " + e.getMessage());
		} finally {
			if ( reader != null )
			{
				try {
					reader.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(newFile.toString().trim());
		} catch (IOException e) {
			logger.println("Error fix CCM result file: " + e.getMessage());
		}finally {
			if ( writer != null )
			{
				try {
					writer.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
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
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) 
	throws InterruptedException, IOException {
		
		ArgumentListBuilder args = new ArgumentListBuilder();
    	
    	CCMBuilderInstallation installation = getCCM();
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
		args.add(new File(workspace.getRemote(), "ccm.config.xml"));
		
		//According to the Ant builder source code, in order to launch a program 
        //from the command line in windows, we must wrap it into cmd.exe.  This 
        //way the return code can be used to determine whether or not the build failed.
        if(!launcher.isUnix()) {
            args.prepend("cmd.exe","/C");
            args.add(">", "ccm.result.xml");
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
            
            fixResultFile( new File(workspace.getRemote(), "ccm.result.xml"), listener.getLogger());
            
            return r==0;
        } catch (IOException e) {
            Util.displayIOException(e,listener);
            e.printStackTrace( listener.fatalError("command execution failed") );
            return false;
        }
		
	}
    
}
