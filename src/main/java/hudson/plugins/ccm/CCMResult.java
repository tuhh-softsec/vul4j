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
 * Kinow ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
package hudson.plugins.ccm;

import hudson.model.AbstractBuild;
import hudson.plugins.ccm.model.CCMReport;

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class CCMResult 
implements Serializable {

	private CCMReport report;
	private AbstractBuild owner;
	
	public CCMResult(CCMReport report, AbstractBuild<?, ?> owner)
	{
		this.report = report;
		this.owner = owner;
	}
	
	public CCMReport getReport()
	{
		return this.report;
	}
	
	public AbstractBuild<?, ?> getOwner() 
	{
		return this.owner;
	}
	
}
