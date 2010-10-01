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
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 21/09/2010
 */
package hudson.plugins.ccm.config;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.plugins.ccm.CCMBuilder;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 21/09/2010
 */
public class CCMConfigCallable 
implements FileCallable<String>
{
	
	private final String srcFolders;
	private final String excludeFiles;
	private final String excludeFolders;
	private final String excludeFunctions;
	private final Boolean isRecursive;
	private final String numMetrics;
	
	private final BuildListener listener;

	public CCMConfigCallable(
		String srcFolders, 
		String excludeFiles, 
		String excludeFolders, 
		String excludeFunctions, 
		Boolean isRecursive, 
		String numMetrics, 
		BuildListener listener
	)
	{
		this.srcFolders = srcFolders;
		this.excludeFiles = excludeFiles;
		this.excludeFolders = excludeFolders;
		this.excludeFunctions = excludeFunctions;
		this.isRecursive = isRecursive;
		this.numMetrics = numMetrics;
		this.listener = listener;
	}

	/**
	 * <p>Creates the XML input file for CCM.exe. It uses data provided by 
	 * user in the project configuration page (e.g.:{@link #recursive}).</p> 
	 * 
	 * @param ccmConfigFilePath Path to CCM config file
	 * @param listener Hudson Build Listener
	 * @throws IOException
	 */
    private String createCCMConfigFile( File workspace ) 
	throws IOException
	{
    	File ccmConfigFile = new File( workspace, CCMBuilder.CCM_CONFIG_FILE );
		listener.getLogger().println("Creating CCM config file " + ccmConfigFile.getAbsolutePath());
		//TBD: improve this
		ccmConfigFile.createNewFile();
		
		StringBuffer buffer = new StringBuffer();
		
		// TBD: eck! correct this. later...
		buffer.append("<ccm>\n");
		buffer.append("<exclude>\n");
		
		this.addExclusion( buffer, excludeFiles, "file" );
		
		this.addExclusion( buffer, excludeFolders, "folder" );
		
		this.addExclusion( buffer, excludeFunctions, "function" );
		
		buffer.append("</exclude>\n");
		buffer.append("<analyze>\n");
		String normalizedSourceFolders = srcFolders.replaceAll("[\t\r\n]+", " ");
		StringTokenizer st = new StringTokenizer(normalizedSourceFolders, " ");
		while ( st.hasMoreTokens() )
		{
			//buffer.append("<folder>"+new File(workspace, st.nextToken()).getAbsolutePath()+"</folder>\n");
			buffer.append("<folder>"+st.nextToken()+"</folder>\n");
		}
		buffer.append("</analyze>\n");
		buffer.append("<recursive>"+ (isRecursive == true ? "yes" : "no") +"</recursive>\n");
		buffer.append("<outputXML>yes</outputXML>\n");
		buffer.append("<numMetrics>"+numMetrics+"</numMetrics>\n");
		buffer.append("</ccm>\n");
		
		listener.getLogger().println("Writing CCM configuration into file");
		listener.getLogger().println(buffer.toString());
		
		FileWriter writer = new FileWriter(ccmConfigFile);
		writer.append(buffer.toString());
		writer.flush(); // TBD: do it better.
		writer.close();
		
		return ccmConfigFile.getAbsolutePath();
	}
	
	/**
	 * Adds exclusion tag to CCM config File. 
	 * 
	 * @param buffer Buffer to append the string
	 * @param excludeString String with tokens separated by spaces
	 * @param xmlTag xml tag of exclusion, like folder, function, tag...
	 */
	private void addExclusion( 
			StringBuffer buffer, 
			String excludeString,
			String xmlTag )
	{
		if ( StringUtils.isEmpty( excludeString ) )
		{
			return;
		}
		String normalizedExcludeString = excludeString.replaceAll("[\t\r\n]+", " ");
		
		StringTokenizer st = new StringTokenizer( normalizedExcludeString, " " );
		while ( st.hasMoreTokens() )
		{
			buffer.append( "<"+xmlTag+">" );
			buffer.append( st.nextToken() );
			buffer.append( "</"+xmlTag+">\n" );
		}
	}

	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public String invoke( File workspace, VirtualChannel channel ) 
	throws IOException, InterruptedException
	{
		return createCCMConfigFile( workspace );
	}
	
}
