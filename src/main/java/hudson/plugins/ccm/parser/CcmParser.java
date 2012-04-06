/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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
package hudson.plugins.ccm.parser;

import hudson.plugins.analysis.core.AbstractAnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

/**
 * A parser for CCM XML files.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmParser extends AbstractAnnotationParser {

	private static final long serialVersionUID = -5172155190810975806L;
	
	/** PMD priorities smaller than this value are mapped to {@link Priority#HIGH}. */
    private static final int CCM_COMPLEXITY_MAPPED_TO_HIGH_PRIORITY = 10;
    /** PMD priorities greater than this value are mapped to {@link Priority#LOW}. */
    private static final int CCM_COMPLEXITY_MAPPED_TO_LOW_PRIORITY = 4;
	
	/**
     * Creates a new instance of {@link PmdParser}.
     */
    public CcmParser() {
        super(StringUtils.EMPTY);
    }
	
	/**
	 * @param defaultEncoding
	 */
	public CcmParser(String defaultEncoding) {
		super(defaultEncoding);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AbstractAnnotationParser#parse(java.io.InputStream, java.lang.String)
	 */
	@Override
	public Collection<FileAnnotation> parse(InputStream file, String moduleName)
			throws InvocationTargetException {
		try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CcmParser.class.getClassLoader());

            String rootXPath = "ccm";
            digester.addObjectCreate(rootXPath, Ccm.class);
            digester.addSetProperties(rootXPath);

            String fileMetric = "ccm/metric";
            digester.addObjectCreate(fileMetric, Metric.class);
            digester.addSetProperties(fileMetric);
            digester.addBeanPropertySetter("ccm/metric/complexity");
            digester.addBeanPropertySetter("ccm/metric/unit");
            digester.addBeanPropertySetter("ccm/metric/classification");
            digester.addBeanPropertySetter("ccm/metric/file");
            digester.addSetNext(fileMetric, "addMetric", Metric.class.getName());

            Ccm module = (Ccm)digester.parse(file);
            if (module == null) {
                throw new SAXException("Input stream is not a CCM file.");
            }

            return convert(module, moduleName);
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
	}

	/**
	 * @param module
	 * @param moduleName
	 * @return
	 */
	private Collection<FileAnnotation> convert(Ccm collection, String moduleName) {
		ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();
		
		for (Metric warning : collection.getMetrics()) {
			Priority priority;
			if (warning.getComplexity() > CCM_COMPLEXITY_MAPPED_TO_HIGH_PRIORITY) {
				priority = Priority.HIGH;
			} else if (warning.getComplexity() < CCM_COMPLEXITY_MAPPED_TO_LOW_PRIORITY) {
				priority = Priority.LOW;
			} else {
				priority = Priority.NORMAL;
			}

			Bug bug = new Bug(priority, warning.getClassification(), warning.getClassification(), 
					"Cyclomatic Complexity", 0, 0);
			bug.setPackageName("");
			bug.setModuleName(moduleName);
			bug.setFileName(warning.getFile());
			bug.setColumnPosition(0, 0);

			try {
				bug.setContextHashCode(createContextHashCode(warning.getFile(), 0));
			} catch (IOException exception) {
				// ignore and continue
			}

			annotations.add(bug);
		}

		return annotations;
	}

}
