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
package hudson.plugins.ccm.model;

import hudson.FilePath;
import hudson.plugins.ccm.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kohsuke.stapler.framework.io.IOException2;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/** 
 * <p>Parser of the output of CCM.exe. In the first version we used XStream. 
 * But when a project has 10000 methods the DOM parser would degrade the 
 * overall performance. Then we changed to a SAX Parser. Sweet as!</p> 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class CCMParser 
implements FilePath.FileCallable<CCMReport>
{

	private static final String RESULT_FILE_NAME = "ccm.result.xml";

	private boolean LOG_ENABLED = false;
	
	private transient PrintStream logger;

	public CCMParser(PrintStream logger) {
		super();
		this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public CCMReport invoke(File workspace, VirtualChannel channel) throws IOException,
			InterruptedException {
		CCMReport report = new CCMReport();
		
		this.parse( workspace, RESULT_FILE_NAME, report);

		return report;
	}

	/**
	 * @param workspace
	 * @param channel
	 * @param report
	 */
	private void parse(File workspace, String fileName, CCMReport report)
	throws IOException{
		java.io.File file = new java.io.File(workspace, fileName);
        InputStream in = new FileInputStream(file);
        this.parse(in, report);
        in.close();
	}

	/**
	 * @param in
	 * @param report
	 */
	private void parse(InputStream in, CCMReport report) 
	throws IOException 
	{
	
		
		if ( LOG_ENABLED && logger != null )
		{
			logger.println(Messages.CCM_Parser_ParsingResults());
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } 
		catch (ParserConfigurationException e) 
        {
        	e.printStackTrace( logger );
        } 
		catch (SAXNotRecognizedException e) 
		{
			e.printStackTrace( logger );
        } 
		catch (SAXNotSupportedException e) 
		{
			e.printStackTrace( logger );
        }
        
        try {
            SAXParser parser = factory.newSAXParser();
            CCMXmlHandler handler = new CCMXmlHandler();
            
            parser.parse(in, handler);
            CCM ccm  = handler.getCCM();
            List<Metric> metrics = ccm.getMetrics();    		
    		report.setMetrics(metrics);
    		report.updateNumbers();
            
        } 
        catch (ParserConfigurationException pce) 
        {
            throw new IOException2(Messages.CCM_Parser_CouldNotParse(pce.getMessage()), pce);
        }
        catch (SAXException saxe) 
        {
            throw new IOException2(Messages.CCM_Parser_CouldNotParse(saxe.getMessage()), saxe);
        }	

	}
}
