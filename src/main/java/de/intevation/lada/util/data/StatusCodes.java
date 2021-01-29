/* Copyright (C) 2021 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.util.data;

public class StatusCodes {
    private StatusCodes() { };
    public static final int OK = 200;
    public static final int NOT_EXISTING = 600;
    public static final int PRESENT = 601;
    public static final int NOT_A_PROBE = 602;
    public static final int ERROR_DB_CONNECTION = 603;
    public static final int ERROR_VALIDATION = 604;
    public static final int ERROR_MERGING = 605;
    public static final int ERROR_DELETE = 606;
    public static final int VALUE_AMBIGOUS = 611;
    public static final int VALUE_OUTSIDE_RANGE = 612;
    public static final int VALUE_MISSING = 631;
    public static final int VALUE_NOT_MATCHING = 632;
    public static final int VAL_DESK = 633;
    public static final int VAL_MEASURE = 634;
    public static final int VAL_UNCERT = 635;
    public static final int VAL_SEC_UNIT = 636;
    public static final int VAL_S1_NOTSET = 637;
    public static final int VAL_OBL_MEASURE = 638;
    public static final int VAL_SINGLE_DATE = 639;
    public static final int VAL_DATE_IN_FUTURE = 641;
    public static final int VAL_MEAS_DATE_BEFORE = 642;
    public static final int VAL_END_BEFORE_BEGIN = 643;
    public static final int VAL_UNIT_UMW = 644;
    public static final int GEO_COORD_UNCHECKED = 650;
    public static final int GEO_POINT_OUTSIDE = 651;
    public static final int GEO_NOT_MATCHING = 652;
    public static final int GEO_UNCHANGEABLE_COORD = 653;
    public static final int STATUS_RO = 654;
    public static final int DATE_IN_FUTURE = 661;
    public static final int DATE_BEGIN_AFTER_END = 662;
    public static final int IMP_PARSER_ERROR = 670;
    public static final int IMP_PRESENT = 671;
    public static final int IMP_DUPLICATE = 672;
    public static final int IMP_MISSING_VALUE = 673;
    public static final int IMP_DATE_ERROR = 674;
    public static final int IMP_INVALID_VALUE = 675;
    public static final int IMP_UNCHANGABLE = 676;
    public static final int OP_NOT_POSSIBLE = 696;
    public static final int CHANGED_VALUE = 697;
    public static final int NO_ACCESS = 698;
    public static final int NOT_ALLOWED = 699;
}
