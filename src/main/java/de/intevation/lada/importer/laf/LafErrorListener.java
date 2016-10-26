package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.intevation.lada.importer.ReportItem;

public class LafErrorListener extends BaseErrorListener {
    public static LafErrorListener INSTANCE =
        new LafErrorListener();

    private List<ReportItem> errors = new ArrayList<ReportItem>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        String sourceName = "Parser";
        if (e != null && e.getCtx() != null) {
            sourceName = e.getCtx().getText();
        }
        String token = "Token";
        if (e != null && e.getOffendingToken() != null) {
            e.getOffendingToken().getText();
        }
        ReportItem err = new ReportItem();
        err.setKey(sourceName);
        err.setValue(line + ":" + charPositionInLine + " - " + token);
        err.setCode(670);
        this.errors.add(err);
        System.err.println(err.getKey() + " - " +err.getValue() + " - " + err.getCode());
    }

    public void reset() {
        this.errors.clear();
    }

    public List<ReportItem> getErrors() {
        return this.errors;
    }
}
