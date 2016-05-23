/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Utilities for calculations
 *
 */
public class MathUtil {

    public static final MathContext ROUNDING_CONTEXT = new MathContext(
        3, RoundingMode.HALF_EVEN);

    public static Double roundDoubleToThree(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).round(ROUNDING_CONTEXT).doubleValue();
    }

}
