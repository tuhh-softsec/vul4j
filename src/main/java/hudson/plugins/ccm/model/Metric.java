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

import java.io.Serializable;

/**
 * <p>Entity representing the Metric from CCM.exe output.</p>
 * 
 * <p>It has the {@link #complexity}, {@link #unit}, {@link #classification} 
 * and {@link #file} fields.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class Metric 
implements Serializable {
	
	/**
	 * Total CC of the method.
	 */
	private int complexity;
	
	/**
	 * String containing Class_Name::Method_Name
	 */
	private String unit;
	
	/**
	 * CCM outputs a String with a classification such as "complex, high risk", 
	 * "untestable, very high risk", etc. As there is no documentation on which 
	 * values are used to determine a method's CC classification CCM Plugin 
	 * only outputs this value. But does not use the information as a constraint 
	 * in any place.
	 */
	private String classification;
	
	/**
	 * The file name (e.g.:\ascx\request\open\form.ascx.cs).
	 */
	private String file;

	public Metric() {
		super();
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

	@Override
	public String toString() {
		return "Metric [classification=" + classification + ", complexity="
				+ complexity + ", file=" + file + ", unit=" + unit + "]";
	}

}
