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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Michiel Hendriks
 * 
 */
public class VersionTest {
	/**
	 * Test method for {@link net.sf.xslthl.Version#getVersion()}.
	 */
	@Test
	public void testGetVersion() {
		assertNotNull(Version.getVersion());
	}

	/**
	 * Test method for {@link net.sf.xslthl.Version#getMajor()}.
	 */
	@Test
	public void testGetMajor() {
		assertTrue(Version.getMajor() >= 0);
	}

	/**
	 * Test method for {@link net.sf.xslthl.Version#getMinor()}.
	 */
	@Test
	public void testGetMinor() {
		assertTrue(Version.getMinor() >= 0);
	}

	/**
	 * Test method for {@link net.sf.xslthl.Version#getRevision()}.
	 */
	@Test
	public void testGetRevision() {
		assertTrue(Version.getRevision() >= 0);
	}

	/**
	 * Test method for {@link net.sf.xslthl.Version#getReleaseType()}.
	 */
	@Test
	public void testGetReleaseType() {
		Version.getReleaseType();
	}

}
