/* Copyright (C) 2021 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter to store a given empty string as null
 */
@Converter
public class EmptyStringConverter
    implements AttributeConverter<String, String> {

    public String convertToDatabaseColumn(String attribute) {
        return (attribute == null || attribute.isEmpty()) ? null : attribute;
    }

    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
