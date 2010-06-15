package hudson.plugins.ccm.model;

import java.io.Serializable;

/**
 * Represents a metric measured by ccm plugin.
 * 
 * See any ccm output xml.
 * 
 * @author Bruno
 *
 */
public class Metric implements Serializable {

	private int complexity;
	private String unit;
	private String classification;
	private String file;
	
	public Metric() {
	}
	
	public Metric(int complexity, String unit, String classification,
			String file) {
		super();
		this.complexity = complexity;
		this.unit = unit;
		this.classification = classification;
		this.file = file;
	}

	public int getComplexity() {
		return complexity;
	}
	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
}
