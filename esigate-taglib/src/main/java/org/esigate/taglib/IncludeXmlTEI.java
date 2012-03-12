package org.esigate.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * This TEI validate IncludeXmlTag attributes. Checks that either xpath or
 * template attributes are defined, but not both.
 * 
 * @author Sylvain Sicard
 * 
 */
public class IncludeXmlTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		Object xpathAtt = data.getAttribute("xpath");
		Object templateAtt = data.getAttribute("template");
		if (xpathAtt != null && templateAtt != null) {
			return new ValidationMessage[] { new ValidationMessage(
					data.getId(),
					"One and only one of the attributes \"xpath\" "
							+ "or \"template\" must be defined") };
		} else {
			return null;
		}
	}

}
