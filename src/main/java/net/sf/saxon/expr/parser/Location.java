////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2015 Saxonica Limited.
// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
// This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package net.sf.saxon.expr.parser;

import org.xml.sax.Locator;

import javax.xml.transform.SourceLocator;

/**
 * Saxon interface to represent a location, typically the location of an expression within a query
 * or stylesheet. The interface combines the two JAXP interfaces SourceLocator and Locator.
 */

public interface Location extends SourceLocator, Locator {

    /**
     * Get the system ID. This should be the system identifier of an XML external entity; if a stylesheet module
     * comprises multiple external entities, the distinction should be retained. This means, for example, that
     * an instruction in a stylesheet can have a different system identifier from its parent instruction. However,
     * SAX parsers only provide location information at the element level, so when part of an XPath expression
     * is in a different external entity from other parts of the same expression, this distinction is lost.
     *
     * <p>The system identifier of a node is in general not the same as its base URI. The base URI is affected
     * by xml:base attributes; the system identifier is not.</p>
     *
     * @return the system ID, or null if the information is not available.
     */

    String getSystemId();

    /**
     * Get the Public ID
     *
     * @return usually null
     */

    String getPublicId();

    /**
     * Get the line number. This line number is relative to the external entity identified by the system identifier.
     * Line numbers start at 1. The value may be an approximation; SAX parsers only provide line number information
     * at the level of element nodes.
     *
     * @return the line number, or -1 if the information is not available.
     */

    int getLineNumber();

    /**
     * Get the column number. This column number is relative to the line identified by the line number.
     * Column numbers start at 1.
     *
     * @return the column number, or -1 if the information is not available.
     */

    int getColumnNumber();

    /**
     * Get an immutable copy of this Location object. By default Location objects may be mutable, so they
     * should not be saved for later use. The result of this operation holds the same location information,
     * but in an immutable form.
     */

    Location saveLocation();


}