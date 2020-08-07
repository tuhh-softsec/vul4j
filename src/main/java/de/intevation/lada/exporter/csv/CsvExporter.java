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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;

@ExportConfig(format=ExportFormat.CSV)
public class CsvExporter implements Exporter{

    @Inject Logger logger;

    /**
     * Enum storing all possible csv options
     */
    private enum CsvOptions {
        comma(","), period("."), semicolon(";"), space(" "),
        singlequote("'"), doublequote("\""),
        linux("\n"), windows("\r\n");

        private final String value;

        CsvOptions(String value) {
            this.value = value;
        }

        public char getChar() {
            return this.value.charAt(0);
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * Export a query result.
     * @param queryResult Result to export as list of maps. Every list item represents a row,
     *               while every map key represents a column
     * @param encoding Encoding to use
     * @param options Optional export options as JSON.
     *                Valid options are:
     *                  - decimalSeparator: "comma" | "period",
     *                  - fieldSeparator: "comma" | "semicolon" | "period" | "space",
     *                  - rowDelimiter: "windows" | "linux",
     *                  - quoteType: "singlequote" | "doublequote"
     * @return Export result as input stream or null if the export failed
     */
    public InputStream export(List<Map<String, Object>> queryResult, String encoding, JsonObject options) {
        if (queryResult == null || queryResult.size() == 0) {
            return null;
        }

        //Parse options
        char decimalSeparator = CsvOptions.valueOf(
            options.containsKey("decimalSeparator")?
            options.getString("decimalSeparator"): "period").getChar();
        char fieldSeparator = CsvOptions.valueOf(
            options.containsKey("fieldSeparator")?
            options.getString("fieldSeparator"): "comma").getChar();
        String rowDelimiter = CsvOptions.valueOf(
            options.containsKey("rowDelimiter")?
            options.getString("rowDelimiter"): "windows").getValue();
        char quoteType = CsvOptions.valueOf(
            options.containsKey("quoteType")?
            options.getString("quoteType"): "doublequote").getChar();

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(decimalSeparator);
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setGroupingUsed(false);

        //Get header fields
        String[] keys = (String[]) queryResult.get(0).keySet().toArray();

        //Create CSV format
        CSVFormat format = CSVFormat.DEFAULT
            .withDelimiter(fieldSeparator)
            .withQuote(quoteType)
            .withRecordSeparator(rowDelimiter)
            .withHeader(keys);


        StringBuffer result = new StringBuffer();

        try {
            final CSVPrinter printer = new CSVPrinter(result, format);

            //For every queryResult row
            queryResult.forEach(row -> {
                ArrayList<String> rowItems = new ArrayList<String>();
                for (int i = 0; i < keys.length; i++) {
                    Object value = row.get(keys[i]);
                    if (value instanceof Double) {
                        rowItems.add(decimalFormat.format((Double) value));
                    } else {
                        rowItems.add(value.toString());
                    }
                }
                try {
                    printer.printRecord(rowItems);
                } catch (IOException ioe) {
                    logger.error(String.format("Error on printing records: %s", ioe.toString()));
                }
            });

            printer.close();
        } catch (IOException ioe) {
            logger.error(ioe.toString());
            return null;
        }
        return new ByteArrayInputStream(result.toString().getBytes(StandardCharsets.UTF_8));
    }
}