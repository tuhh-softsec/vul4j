/* Copyright (C) 2016 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.intevation.lada.importer.ReportItem;

/**
 * Listener to track errors in parser.
 */
public class LafErrorListener extends BaseErrorListener {

    private static final int ERR670 = 670;

    /**
     * The instance of the listener.
     */
    public static final LafErrorListener INSTANCE =
        new LafErrorListener();

    private List<ReportItem> errors = new ArrayList<ReportItem>();

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line,
        int charPositionInLine,
        String msg,
        RecognitionException e
    ) {
        String sourceName = "Parser";
        if (e != null && e.getCtx() != null) {
            sourceName = e.getCtx().getText();
        }

        ReportItem err = new ReportItem();
        err.setKey(sourceName);
        err.setValue("line " + line + ": " + msg);
        err.setCode(ERR670);
        this.errors.add(err);
    }

    /**
     * Reset the list of errors.
     */
    public void reset() {
        this.errors.clear();
    }

    public List<ReportItem> getErrors() {
        return this.errors;
    }
}
