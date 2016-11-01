package de.intevation.lada.importer.laf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
            errorListener.reset();
            parser.addErrorListener(errorListener);
            ParseTree tree = parser.probendatei();
            LafObjectListener listener = new LafObjectListener();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            if (!listener.hasUebertragungsformat() ||
                !listener.hasVersion()) {
                List<ReportItem> items = new ArrayList<ReportItem>();
                items.add(new ReportItem("missing header", "format", 673));
                errors.put("parser", items);
                return;
            }
            if (!errorListener.getErrors().isEmpty()) {
                errors.put("parser", errorListener.getErrors());
                return;
            }
            errors.putAll(listener.getErrors());
            mapper.setUserInfo(userInfo);
            mapper.mapObjects(listener.getData());
            logger.debug("listener errors: " + listener.getErrors().size());
            logger.debug("import mapper errors: " + mapper.getErrors().size());
            for (Entry<String, List<ReportItem>> entry : mapper.getErrors().entrySet()) {
                logger.debug("add for key: "+ entry.getKey());
                if (errors.containsKey(entry.getKey())) {
                    errors.get(entry.getKey()).addAll(entry.getValue());
                    logger.debug("done");
                }
                else {
                    errors.put(entry.getKey(), entry.getValue());
                    logger.debug("done2");
                }
            }
            warnings.putAll(mapper.getWarnings());
            logger.debug("import warnings: " + warnings.size());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public Map<String, List<ReportItem>> getErrors() {
        return this.errors;
    }

    @Override
    public Map<String, List<ReportItem>> getWarnings() {
        return this.warnings;
    }
}
