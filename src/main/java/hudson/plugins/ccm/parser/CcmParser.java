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
            digester.addBeanPropertySetter("ccm/metric/startLineNumber");
            digester.addBeanPropertySetter("ccm/metric/endLineNumber");
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
		
		for (Metric metric : collection.getMetrics()) {
			Priority priority = calculateMetricPriority(metric);

			String complexity = String.format("%s has a complexity of %d", metric.getUnit(), metric.getComplexity());

			Bug bug = new Bug(priority, complexity, metric.getClassification(),
					"Cyclomatic Complexity", metric.getStartLineNumber(), metric.getEndLineNumber());
			bug.setPackageName("-");
			bug.setModuleName(moduleName);
			bug.setFileName(metric.getFile());
			bug.setColumnPosition(0, 0);

			try {
				bug.setContextHashCode(createContextHashCode(metric.getFile() + metric.getUnit() + metric.getClassification(), 0));
			} catch (IOException exception) {
				// ignore and continue
			}

			annotations.add(bug);
		}

		return annotations;
	}

	private Priority calculateMetricPriority(Metric metric){
		Priority priority;
		if (isMetricHighPriority(metric)) {
			priority = Priority.HIGH;
		} else if (isMetricModeratePriority(metric)) {
			priority = Priority.NORMAL;
		} else {
			priority = Priority.LOW;
		}

		return priority;
	}

	private boolean isMetricHighPriority(Metric metric){
		String metricClassification = metric.getClassification();
		if (metricClassification.contains("high")){
			return true;
		}
		else if (metricClassification.contentEquals("C") || metricClassification.contentEquals("D") ||
				metricClassification.contentEquals("E") || metricClassification.contentEquals(("F"))){
			return true;
		}
		else {
			return false;
		}
	}

	private boolean isMetricModeratePriority(Metric metric){
		String metricClassification = metric.getClassification();
		if (metricClassification.contains("moderate")) {
			return true;
		}
		else if (metricClassification.contentEquals("B")) {
			return true;
		}
		else {
			return false;
		}
	}
}
