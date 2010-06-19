package hudson.plugins.ccm.parser;

import hudson.model.BuildListener;
import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.model.Metric;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CCMResultParser {

	private final CCM ccmNode = new CCM();
	
	private PrintStream ps;
	
	public CCMResultParser()
	{
		this.ps = System.out;
	}
	
	public CCMResultParser(BuildListener listener)
	{
		this.ps = listener.getLogger();	
	}
	
	public CCM parse(File ccmOutputFile) 
	throws IOException
	{
	
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse( ccmOutputFile );
			
			NodeList mainNode = doc.getElementsByTagName("ccm");
			
			Element rootElement = (Element)mainNode.item(0);
			
			parseMetrics( rootElement );
			
			return this.ccmNode;
			
		} catch (SAXException e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		
	}

	private void parseMetrics(Element rootElement) {
		if ( rootElement != null )
		{
			for ( Element metricElement : XmlElementUtil.getNamedChildElements(rootElement, "metric"))
			{
				Metric metric;
				int complexity = parseComplexity( metricElement );
				String unit = parseString( metricElement, "unit" );
				String classification = parseString( metricElement, "classification" );
				String file = parseString( metricElement, "file" );
				metric = new Metric( complexity, unit, classification, file );
				
				this.ccmNode.addMetric( metric ) ;
			}
		}
	}

	private int parseComplexity(Element metricElement) {
		int complexity = -1;
		Element complexityElement = XmlElementUtil.getFirstElementByTagName(metricElement, "complexity");
		if ( complexityElement != null )
		{
			try
			{
				complexity = Integer.parseInt(complexityElement.getTextContent());
			} catch ( NumberFormatException nfe )
			{
				this.ps.println("Invalid complexity element: " + complexity);
			}
		}
		return complexity;
	}
	
	private String parseString(Element metricElement, String string) 
	{
		String value = null;
		Element element = XmlElementUtil.getFirstElementByTagName(metricElement, string);
		if ( element != null )
		{
			value = element.getTextContent();
		}
		return value;
	}
	
}
