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
import de.intevation.lada.model.stammdaten.ImporterConfig;
import de.intevation.lada.util.auth.UserInfo;

@ImportConfig(format=ImportFormat.LAF)
public class LafImporter implements Importer{

    @Inject
    private Logger logger;

    @Inject
    private LafObjectMapper mapper;

    private Map<String, List<ReportItem>> errors = new HashMap<String, List<ReportItem>>();
    private Map<String, List<ReportItem>> warnings = new HashMap<String, List<ReportItem>>();
    private List<Integer> importProbeIds;

    public void doImport(String lafString, UserInfo userInfo, List<ImporterConfig> config) {
        // Append newline to avoid parser errors.
        // Every line can be the last line, so it is easier to append a newline here
        // than to extend the grammar with EOF for every line.
        lafString += "\r\n";
        errors = new HashMap<String, List<ReportItem>>();
        warnings = new HashMap<String, List<ReportItem>>();
        importProbeIds = new ArrayList<Integer>();

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
            List<ReportItem> parserWarnings = listener.getParserWarnings();
            if (!listener.hasUebertragungsformat()) {
                ReportItem warn = new ReportItem();
                warn.setKey("UEBERTRAGUNGSFORMAT");
                warn.setValue("");
                warn.setCode(673);
                parserWarnings.add(warn);
            }
            if (!listener.hasVersion()) {
                ReportItem warn = new ReportItem();
                warn.setKey("VERSION");
                warn.setValue("");
                warn.setCode(673);
                parserWarnings.add(warn);
            }
            if (!errorListener.getErrors().isEmpty()) {
                errors.put("Parser", errorListener.getErrors());
                return;
            }
            errors.putAll(listener.getErrors());
            warnings.putAll(listener.getWarnings());
            if (!parserWarnings.isEmpty()) {
                warnings.put("Parser", parserWarnings);
            }
            mapper.setUserInfo(userInfo);
            mapper.setConfig(config);
            mapper.mapObjects(listener.getData());
            importProbeIds = mapper.getImportedProbeIds();
            for (Entry<String, List<ReportItem>> entry : mapper.getErrors().entrySet()) {
                if (errors.containsKey(entry.getKey())) {
                    errors.get(entry.getKey()).addAll(entry.getValue());
                }
                else {
                    errors.put(entry.getKey(), entry.getValue());
                }
            }

            for (Entry<String, List<ReportItem>> entry : mapper.getWarnings().entrySet()) {
                if (warnings.containsKey(entry.getKey())) {
                    warnings.get(entry.getKey()).addAll(entry.getValue());
                }
                else {
                    warnings.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException e) {
            logger.debug("Exception while reading LAF input", e);
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

    public List<Integer> getImportedIds() {
        return this.importProbeIds;
    }
}
