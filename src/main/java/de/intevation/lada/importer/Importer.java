package de.intevation.lada.importer;

import java.util.List;
import java.util.Map;

import de.intevation.lada.util.auth.UserInfo;

public interface Importer {
    void reset();
    Map<String, List<ReportItem>> getWarnings();
    Map<String, List<ReportItem>> getErrors();
    void doImport(String content, UserInfo userInfo);
}
