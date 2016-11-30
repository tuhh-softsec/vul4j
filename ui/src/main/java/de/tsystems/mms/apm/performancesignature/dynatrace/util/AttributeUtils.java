/*
 * Copyright (c) 2008-2015, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the dynaTrace software nor the names of its contributors
 *       may be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.util;

import org.apache.commons.lang.math.NumberUtils;
import org.jdom2.Element;
import org.xml.sax.Attributes;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

public final class AttributeUtils {
    private AttributeUtils() {
    }

    public static double getDoubleAttribute(final String attributeName, final Object attr) {
        double val = 0.0D;
        final String strgVal = getStringAttribute(attributeName, attr);
        if (strgVal != null) {
            if (strgVal.equals("INF")) {
                return Double.POSITIVE_INFINITY;
            }
            val = Double.parseDouble(strgVal);
        }
        return val;
    }

    public static String getStringAttribute(final String attributeName, final Object attr) {
        if (attr instanceof Attributes) {
            Attributes attributes = (Attributes) attr;
            if (attributes.getValue(attributeName) != null) {
                return attributes.getValue(attributeName);
            }
        } else if (attr instanceof Element) {
            Element element = (Element) attr;
            if (element.getChildText(attributeName) != null) {
                return element.getChildText(attributeName);
            }
        }
        return null;
    }

    public static int getIntAttribute(final String attributeName, final Object attr) {
        int val = 0;
        final String strgVal = getStringAttribute(attributeName, attr);
        if (strgVal != null) {
            val = Integer.parseInt(strgVal);
        }
        return val;
    }

    public static long getLongAttribute(final String attributeName, final Object attr) {
        long val = 0L;
        final String strgVal = getStringAttribute(attributeName, attr);
        if (strgVal != null) {
            val = Long.parseLong(strgVal);
        }
        return val;
    }

    public static Date getDateAttribute(final String attributeName, final Object attr) {
        String value = getStringAttribute(attributeName, attr);
        if (value != null) {
            if (NumberUtils.isNumber(value)) {
                return new Date(getLongAttribute(attributeName, attr));
            } else {
                return DatatypeConverter.parseDateTime(value).getTime();
            }
        }
        return new Date();
    }
}
