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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler used by the {@link CCMParser}, a SAX Parser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class CCMXmlHandler 
extends DefaultHandler 
{

	/**
	 * 
	 */
	private static final String METRIC = "metric";
	/**
	 * 
	 */
	private static final String FILE = "file";
	/**
	 * 
	 */
	private static final String CLASSIFICATION = "classification";
	/**
	 * 
	 */
	private static final String UNIT = "unit";
	/**
	 * 
	 */
	private static final String COMPLEXITY = "complexity";
	private CCM ccm;
	private Metric tempMetric;
	private String tempVal;
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() 
	throws SAXException 
	{
		this.ccm = new CCM();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(
			String uri, 
			String localName, 
			String qName,
			Attributes attributes) 
	throws SAXException 
	{
		if ( METRIC.equals(qName) )
		{
			tempMetric = new Metric();
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(
			char[] ch, 
			int start, 
			int length)
	throws SAXException 
	{
		tempVal = new String(ch, start, length);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException 
	{
		if ( COMPLEXITY.equals(qName) )
		{
			tempMetric.setComplexity( Integer.parseInt(tempVal) );
		} else if ( UNIT.equals(qName) )
		{
			tempMetric.setUnit(tempVal);
		} else if ( CLASSIFICATION.equals(qName) )
		{
			tempMetric.setClassification(tempVal);
		} else if ( FILE.equals(qName) )
		{
			tempMetric.setFile(tempVal);
		} else if ( METRIC.equals(qName) )
		{
			this.ccm.getMetrics().add(tempMetric);
		}
	}
	
	public CCM getCCM()
	{
		return this.ccm;
	}
	
}
