/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.codehaus.plexus.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Condition that tests the OS type.
 *
 * @author Stefan Bodewig
 * @author Magesh Umasankar
 * @author Brian Fox
 * @since 1.0
 * @version $Revision$
 */
public class Os
{
    // define the families for easier reference
    public static final String FAMILY_DOS = "dos";

    public static final String FAMILY_MAC = "mac";

    public static final String FAMILY_NETWARE = "netware";

    public static final String FAMILY_OS2 = "os/2";

    public static final String FAMILY_TANDEM = "tandem";

    public static final String FAMILY_UNIX = "unix";

    public static final String FAMILY_WINDOWS = "windows";

    public static final String FAMILY_WIN9X = "win9x";

    public static final String FAMILY_ZOS = "z/os";

    public static final String FAMILY_OS400 = "os/400";

    public static final String FAMILY_OPENVMS = "openvms";

    // store the valid families
    private static final Set validFamilies = setValidFamilies();

    // get the current info
    private static final String PATH_SEP = System.getProperty( "path.separator" );

    public static final String OS_NAME = System.getProperty( "os.name" ).toLowerCase( Locale.US );

    public static final String OS_ARCH = System.getProperty( "os.arch" ).toLowerCase( Locale.US );

    public static final String OS_VERSION = System.getProperty( "os.version" ).toLowerCase( Locale.US );

    // Make sure this method is called after static fields it depends on have been set!
    public static final String OS_FAMILY = getOsFamily();

    private String family;

    private String name;

    private String version;

    private String arch;

    /**
     * Default constructor
     */
    public Os()
    {
    }

    /**
     * Constructor that sets the family attribute
     * 
     * @param family a String value
     */
    public Os( String family )
    {
        setFamily( family );
    }

    /**
     * Initializes the set of valid families.
     */
    private static Set setValidFamilies()
    {
        Set valid = new HashSet();
        valid.add( FAMILY_DOS );
        valid.add( FAMILY_MAC );
        valid.add( FAMILY_NETWARE );
        valid.add( FAMILY_OS2 );
        valid.add( FAMILY_TANDEM );
        valid.add( FAMILY_UNIX );
        valid.add( FAMILY_WINDOWS );
        valid.add( FAMILY_WIN9X );
        valid.add( FAMILY_ZOS );
        valid.add( FAMILY_OS400 );
        valid.add( FAMILY_OPENVMS );

        return valid;
    }

    /**
     * Sets the desired OS family type
     * 
     * @param f The OS family type desired<br />
     *            Possible values:<br />
     *            <ul>
     *            <li>dos</li>
     *            <li>mac</li>
     *            <li>netware</li>
     *            <li>os/2</li>
     *            <li>tandem</li>
     *            <li>unix</li>
     *            <li>windows</li>
     *            <li>win9x</li>
     *            <li>z/os</li>
     *            <li>os/400</li>
     *            <li>openvms</li>
     *            </ul>
     */
    public void setFamily( String f )
    {
        family = f.toLowerCase( Locale.US );
    }

    /**
     * Sets the desired OS name
     * 
     * @param name The OS name
     */
    public void setName( String name )
    {
        this.name = name.toLowerCase( Locale.US );
    }

    /**
     * Sets the desired OS architecture
     * 
     * @param arch The OS architecture
     */
    public void setArch( String arch )
    {
        this.arch = arch.toLowerCase( Locale.US );
    }

    /**
     * Sets the desired OS version
     * 
     * @param version The OS version
     */
    public void setVersion( String version )
    {
        this.version = version.toLowerCase( Locale.US );
    }

    /**
     * Determines if the current OS matches the type of that
     * set in setFamily.
     * 
     * @see Os#setFamily(String)
     */
    public boolean eval()
        throws Exception
    {
        return isOs( family, name, arch, version );
    }

    /**
     * Determines if the current OS matches the given OS
     * family.
     * 
     * @param family the family to check for
     * @return true if the OS matches
     * @since 1.0
     */
    public static boolean isFamily( String family )
    {
        return isOs( family, null, null, null );
    }

    /**
     * Determines if the current OS matches the given OS
     * name.
     * 
     * @param name the OS name to check for
     * @return true if the OS matches
     * @since 1.0
     */
    public static boolean isName( String name )
    {
        return isOs( null, name, null, null );
    }

    /**
     * Determines if the current OS matches the given OS
     * architecture.
     * 
     * @param arch the OS architecture to check for
     * @return true if the OS matches
     * @since 1.0
     */
    public static boolean isArch( String arch )
    {
        return isOs( null, null, arch, null );
    }

    /**
     * Determines if the current OS matches the given OS
     * version.
     * 
     * @param version the OS version to check for
     * @return true if the OS matches
     * @since 1.0
     */
    public static boolean isVersion( String version )
    {
        return isOs( null, null, null, version );
    }

    /**
     * Determines if the current OS matches the given OS
     * family, name, architecture and version.
     * 
     * The name, archictecture and version are compared to
     * the System properties os.name, os.version and os.arch
     * in a case-independent way.
     * 
     * @param family The OS family
     * @param name The OS name
     * @param arch The OS architecture
     * @param version The OS version
     * @return true if the OS matches
     * @since 1.0
     */
    public static boolean isOs( String family, String name, String arch, String version )
    {
        boolean retValue = false;

        if ( family != null || name != null || arch != null || version != null )
        {

            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;

            if ( family != null )
            {
                if ( family.equalsIgnoreCase( FAMILY_WINDOWS ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_WINDOWS ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_OS2 ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_OS2 ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_NETWARE ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_NETWARE ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_DOS ) )
                {
                    isFamily = PATH_SEP.equals( ";" ) && !isFamily( FAMILY_NETWARE );
                }
                else if ( family.equalsIgnoreCase( FAMILY_MAC ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_MAC ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_TANDEM ) )
                {
                    isFamily = OS_NAME.indexOf( "nonstop_kernel" ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_UNIX ) )
                {
                    isFamily = PATH_SEP.equals( ":" ) && !isFamily( FAMILY_OPENVMS )
                        && ( !isFamily( FAMILY_MAC ) || OS_NAME.endsWith( "x" ) );
                }
                else if ( family.equalsIgnoreCase( FAMILY_WIN9X ) )
                {
                    isFamily = isFamily( FAMILY_WINDOWS )
                        && ( OS_NAME.indexOf( "95" ) >= 0 || OS_NAME.indexOf( "98" ) >= 0
                            || OS_NAME.indexOf( "me" ) >= 0 || OS_NAME.indexOf( "ce" ) >= 0 );
                }
                else if ( family.equalsIgnoreCase( FAMILY_ZOS ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_ZOS ) > -1 || OS_NAME.indexOf( "os/390" ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_OS400 ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_OS400 ) > -1;
                }
                else if ( family.equalsIgnoreCase( FAMILY_OPENVMS ) )
                {
                    isFamily = OS_NAME.indexOf( FAMILY_OPENVMS ) > -1;
                }
                else
                {
                    isFamily = false;
                }
            }
            if ( name != null )
            {
                isName = name.toLowerCase( Locale.US ).equals( OS_NAME );
            }
            if ( arch != null )
            {
                isArch = arch.toLowerCase( Locale.US ).equals( OS_ARCH );
            }
            if ( version != null )
            {
                isVersion = version.toLowerCase( Locale.US ).equals( OS_VERSION );
            }
            retValue = isFamily && isName && isArch && isVersion;
        }
        return retValue;
    }

    /**
     * Helper method to determine the current OS family.
     * 
     * @return name of current OS family.
     * @since 1.4.2
     */
    private static String getOsFamily()
    {
        // in case the order of static initialization is
        // wrong, get the list
        // safely.
        Set families = null;
        if ( !validFamilies.isEmpty() )
        {
            families = validFamilies;
        }
        else
        {
            families = setValidFamilies();
        }
        Iterator iter = families.iterator();
        while ( iter.hasNext() )
        {
            String fam = (String) iter.next();
            if ( Os.isFamily( fam ) )
            {
                return fam;
            }
        }
        return null;
    }

    /**
     * Helper method to check if the given family is in the
     * following list:
     * <ul>
     * <li>dos</li>
     * <li>mac</li>
     * <li>netware</li>
     * <li>os/2</li>
     * <li>tandem</li>
     * <li>unix</li>
     * <li>windows</li>
     * <li>win9x</li>
     * <li>z/os</li>
     * <li>os/400</li>
     * <li>openvms</li>
     * </ul>
     * 
     * @param theFamily the family to check.
     * @return true if one of the valid families.
     * @since 1.4.2
     */
    public static boolean isValidFamily( String theFamily )
    {
        return ( validFamilies.contains( theFamily ) );
    }

    /**
     * @return a copy of the valid families
     * @since 1.4.2
     */
    public static Set getValidFamilies()
    {
        return new HashSet( validFamilies );
    }
}
