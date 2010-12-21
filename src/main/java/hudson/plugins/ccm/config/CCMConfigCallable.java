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
package hudson.plugins.ccm.config;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.plugins.ccm.Messages;
import hudson.plugins.ccm.CCMBuilder;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
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

	/**
	 * @param srcFolders
	 * @param excludeFiles
	 * @param excludeFolders
	 * @param excludeFunctions
	 * @param isRecursive
	 * @param numMetrics
	 * @param listener
	 */
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
		listener.getLogger().println( Messages.CCM_Config_CreatingCCMConfigFile( ccmConfigFile ) );

		ccmConfigFile.createNewFile();
		
		StringBuffer buffer = new StringBuffer();
		
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
			buffer.append("<folder>"+st.nextToken()+"</folder>\n");
		}
		buffer.append("</analyze>\n");
		buffer.append("<recursive>"+ (isRecursive == true ? "yes" : "no") +"</recursive>\n");
		buffer.append("<outputXML>yes</outputXML>\n");
		buffer.append("<numMetrics>"+numMetrics+"</numMetrics>\n");
		buffer.append("</ccm>\n");
		
		listener.getLogger().println( Messages.CCM_Config_WritingCCMConfigurationToFile( ccmConfigFile ) );
		listener.getLogger().println(buffer.toString());
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(ccmConfigFile);
			writer.append(buffer.toString());
			writer.flush(); 
		}
		finally 
		{
			writer.close();
		}
		
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
