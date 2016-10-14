package de.intevation.lada.importer.laf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.log4j.Logger;

import de.intevation.lada.importer.ImportConfig;
import de.intevation.lada.importer.ImportFormat;
import de.intevation.lada.importer.Importer;
import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.util.auth.UserInfo;

@ImportConfig(format=ImportFormat.LAF)
public class LafImporter implements Importer{

    @Inject
    private Logger logger;

    @Inject
    private LafObjectMapper mapper;

    private Map<String, List<ReportItem>> errors = new HashMap<String, List<ReportItem>>();
    private Map<String, List<ReportItem>> warnings = new HashMap<String, List<ReportItem>>();

    public void doImport(String lafString, UserInfo userInfo) {
        errors = new HashMap<String, List<ReportItem>>();
        warnings = new HashMap<String, List<ReportItem>>();

        InputStream is = new ByteArrayInputStream(lafString.getBytes(StandardCharsets.UTF_8));
        try {
            ANTLRInputStream ais = new ANTLRInputStream(is);
            LafLexer lexer = new LafLexer(ais);
            CommonTokenStream cts = new CommonTokenStream(lexer);
            LafParser parser = new LafParser(cts);
            LafErrorListener errorListener = LafErrorListener.INSTANCE;
            parser.addErrorListener(errorListener);
            ParseTree tree = parser.probendatei();
            LafObjectListener listener = new LafObjectListener();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            logger.debug("Parsed Proben: " + listener.getData().count());
            errors.put("parser", errorListener.getErrors());
            errors.putAll(listener.getErrors());
            mapper.mapObjects(listener.getData());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public Map<String, List<ReportItem>> getWarnings() {
        return this.errors;
    }

    @Override
    public Map<String, List<ReportItem>> getErrors() {
        return this.warnings;
    }
}
