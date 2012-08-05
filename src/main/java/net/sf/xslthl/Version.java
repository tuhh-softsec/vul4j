/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Version information for xslthl
 */
public class Version {
	/**
	 * @return the version string
	 */
	public static String getVersion() {
		return VersionInternal.versionString;
	}

	/**
	 * Internal class responsible for loading
	 */
	private static class VersionInternal {
		private static Logger LOG = Logger.getLogger("net.sf.xslthl");

		private static String versionString = "undefined";

		static {
			Properties prop = new Properties();
			InputStream is = VersionInternal.class
			        .getResourceAsStream("META-INF/maven/net.sf.xslthl/xslthl/pom.properties");
			if (is != null) {
				try {
					prop.load(is);
					versionString = prop.getProperty("version", versionString);

				} catch (Exception e) {
					LOG.log(Level.WARNING,
					        "Unable to get version information. "
					                + e.getMessage(), e);
				} finally {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
}
