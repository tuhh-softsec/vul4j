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
package hudson.plugins.ccm.parser;


/**
 * <p>Entity representing the Metric from CCM.exe output.</p>
 * 
 * <p>It has the {@link #complexity}, {@link #unit}, {@link #classification} 
 * and {@link #file} fields.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class Metric {
	
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
  private int startLineNumber;
  private int endLineNumber;
  
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

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	public int getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

}
