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
 * @since 16/08/2010
 */
package hudson.plugins.ccm.model;

import hudson.FilePath;
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
 * @since 16/08/2010
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
			logger.println("Parsing ccm.result.xml file...");
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
        } catch (SAXNotRecognizedException e) {
        } catch (SAXNotSupportedException e) {
        }
        
        try {
            SAXParser parser = factory.newSAXParser();
            CCMXmlHandler handler = new CCMXmlHandler();
            
            parser.parse(in, handler);
            CCM ccm  = handler.getCCM();
            List<Metric> metrics = ccm.getMetrics();    		
    		report.setMetrics(metrics);
    		report.updateNumbers();
    		
            /*CCM ccm = (CCM)xstream.fromXML(in);
    		List<Metric> metrics = ccm.getMetrics();
    		
    		report.addMetrics(metrics);*/
            
        } catch (ParserConfigurationException e) {
            throw new IOException2("Cannot parse coverage results", e);
        } catch (SAXException e) {
            throw new IOException2("Cannot parse coverage results", e);
        }	

	}
	
	
	/*
	 * private void parse(InputStreamReader reader, CCMReport report) 
	throws IOException 
	{
		
		BufferedReader in = new BufferedReader(reader);
		
		if ( LOG_ENABLED && logger != null )
		{
			logger.println("Parsing ccm.result.xml file...");
		}
		
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("ccm", CCM.class);
		xstream.alias("metric", Metric.class);
		
		xstream.addImplicitCollection(CCM.class, "metrics");
		
		CCM ccm = (CCM)xstream.fromXML(in);
		List<Metric> metrics = ccm.getMetrics();
		
		report.addMetrics(metrics);

	}
	 */
	
	
}
