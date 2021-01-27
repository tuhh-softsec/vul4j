/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter.csv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.StatusStufe;
import de.intevation.lada.model.stammdaten.StatusWert;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * Exporter class for writing query results to CSV.
 *
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
@ExportConfig(format = ExportFormat.CSV)
public class CsvExporter implements Exporter {

    @Inject Logger logger;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    /**
     * Enum storing all possible csv options.
     */
    private enum CsvOptions {
        comma(","), period("."), semicolon(";"), space(" "),
        singlequote("'"), doublequote("\""),
        linux("\n"), windows("\r\n");

        private final String value;

        CsvOptions(String v) {
            this.value = v;
        }

        public char getChar() {
            return this.value.charAt(0);
        }

        public String getValue() {
            return this.value;
        }
    }

    private String getStatusStringByid(Integer id) {
        StatusKombi kombi =
            repository.getByIdPlain(StatusKombi.class, id, Strings.STAMM);
        StatusStufe stufe = kombi.getStatusStufe();
        StatusWert wert = kombi.getStatusWert();

        return String.format("%s - %s", stufe.getStufe(), wert.getWert());
    }

    /**
     * Return an array of readable column names.
     *
     * The names are either fetched from the database or used from the given
     * sub data column name object
     * @param keys Keys to get name for
     * @param subDataColumnNames Object containing sub data column names
     * @return Name array
     */
    private String[] getReadableColumnNames(
        String[] keys,
        JsonObject subDataColumnNames
    ) {
        String[] names = new String[keys.length];
        ArrayList<String> keysList = new ArrayList<String>(Arrays.asList(keys));
        keysList.forEach(key -> {
            QueryBuilder<GridColumn> builder = new QueryBuilder<GridColumn>(
                repository.entityManager(Strings.STAMM),
                GridColumn.class);
            builder.and("dataIndex", key);
            List<GridColumn> result =
                repository.filterPlain(builder.getQuery(), Strings.STAMM);
            String name = key;
            if (result.size() > 0) {
                GridColumn column = result.get(0);
                name = column.getName();
            } else {
                name = subDataColumnNames.containsKey(key)
                    ? subDataColumnNames.getString(key)
                    : key;
            }
            names[keysList.indexOf(key)] = name;
        });
        return names;
    }

    /**
     * Export a query result.
     * @param queryResult Result to export as list of maps.
     *                    Every list item represents a row,
     *                    while every map key represents a column
     * @param encoding Encoding to use
     * @param options Optional export options as JSON Object.
     *                Valid options are: <p>
     *   <ul>
     *     <li> decimalSeparator: "comma" | "period", defaults to "period" </li>
     *     <li> fieldSeparator: "comma" | "semicolon" | "period" |
     *          "space", defaults to "comma" </li>
     *     <li> rowDelimiter: "windows" | "linux", defaults to "windows" </li>
     *     <li> quoteType: "singlequote" |
     *          "doublequote", defaults to "doublequote" </li>
     *     <li> timezone: Target timezone for timestamp conversion </li>
     *     <li> subDataColumnNames: JsonObject containing dataIndex:
     *          ColumnName key-value-pairs used to get readable column
     *          names </li>
     *   </ul>
     *                Invalid options will cause the export to fail.
     *
     * @param columnsToInclude List of column names to include in the export.
     *                         If not set, all columns will be exported
     * @return Export result as input stream or null if the export failed
     */
    public InputStream export(
        List<Map<String, Object>> queryResult,
        String encoding,
        JsonObject options,
        ArrayList<String> columnsToInclude
    ) {
        if (queryResult == null || queryResult.size() == 0) {
            return null;
        }

        char decimalSeparator = CsvOptions.valueOf("period").getChar();
        char fieldSeparator = CsvOptions.valueOf("comma").getChar();
        String rowDelimiter = CsvOptions.valueOf("windows").getValue();
        char quoteType = CsvOptions.valueOf("doublequote").getChar();
        String timezoneOption = "UTC";
        JsonObject subDataColumnNames = null;
        //Parse options
        if (options != null) {
            try {
                decimalSeparator = CsvOptions.valueOf(
                    options.containsKey("decimalSeparator")
                    ? options.getString("decimalSeparator")
                    : "period").getChar();
                fieldSeparator = CsvOptions.valueOf(
                    options.containsKey("fieldSeparator")
                    ? options.getString("fieldSeparator") : "comma").getChar();
                rowDelimiter = CsvOptions.valueOf(
                    options.containsKey("rowDelimiter")
                    ? options.getString("rowDelimiter") : "windows").getValue();
                quoteType = CsvOptions.valueOf(
                    options.containsKey("quoteType")
                    ? options.getString("quoteType") : "doublequote").getChar();
                timezoneOption =
                    options.containsKey("timezone")
                    ? options.getString("timezone") : "UTC";
                subDataColumnNames =
                    options.containsKey("subDataColumnNames")
                    ? options.getJsonObject("subDataColumnNames") : null;
            } catch (IllegalArgumentException iae) {
                logger.error(
                    String.format(
                        "Invalid CSV options: %s", options.toString()));
                return null;
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(decimalSeparator);
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setGroupingUsed(false);

        //Get header fields
        String[] keys;
        if (columnsToInclude == null) {
            Set<String> keySet = queryResult.get(0).keySet();
            keys = new String[keySet.size()];
            keySet.toArray(keys);
        } else {
            keys = new String[columnsToInclude.size()];
            columnsToInclude.toArray(keys);
        }

        String[] header = getReadableColumnNames(keys, subDataColumnNames);
        //Create CSV format
        CSVFormat format = CSVFormat.DEFAULT
            .withDelimiter(fieldSeparator)
            .withQuote(quoteType)
            .withRecordSeparator(rowDelimiter)
            .withHeader(header);

        StringBuffer result = new StringBuffer();

        try {
            final CSVPrinter printer = new CSVPrinter(result, format);
            final String timezone = timezoneOption;
            //For every queryResult row
            queryResult.forEach(row -> {
                ArrayList<String> rowItems = new ArrayList<String>();
                for (int i = 0; i < keys.length; i++) {
                    Object value = row.get(keys[i]);

                    //Value is a status kombi
                    if (keys[i].equals("statusK")) {
                        rowItems.add(getStatusStringByid((Integer) value));
                        continue;
                    }
                    if (value instanceof Double) {
                        rowItems.add(decimalFormat.format((Double) value));
                    }
                    if (value instanceof Timestamp) {
                        //Convert to target timezone
                        Timestamp time = (Timestamp) value;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(time.getTime()));
                        SimpleDateFormat sdf =
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
                        rowItems.add(sdf.format(calendar.getTime()));
                    } else {
                        rowItems.add(value != null ? value.toString() : null);
                    }
                }
                try {
                    printer.printRecord(rowItems);
                } catch (IOException ioe) {
                    logger.error(
                        String.format(
                            "Error on printing records: %s", ioe.toString()));
                }
            });

            printer.close();
            return new ByteArrayInputStream(
                result.toString().getBytes(encoding));
        } catch (UnsupportedEncodingException uee) {
            logger.error(String.format("Unsupported encoding: %s", encoding));
            return null;
        } catch (IOException ioe) {
            logger.error(ioe.toString());
            return null;
        }
    }
}
