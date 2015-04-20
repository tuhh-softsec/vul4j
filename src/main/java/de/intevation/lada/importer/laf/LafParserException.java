/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer.laf;

/**
 * Exception thrown in the LAF import process.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LafParserException
extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LafParserException(String message) {
        super(message);
    }
}
