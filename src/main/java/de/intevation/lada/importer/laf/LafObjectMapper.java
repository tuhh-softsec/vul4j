package de.intevation.lada.importer.laf;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

public class LafObjectMapper {

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    private Map<String, List<ReportItem>> errors;
    private Map<String, List<ReportItem>> warnings;

    public void mapObjects(LafRawData data) {
        for (int i = 0; i < data.getProben().size(); i++) {
            generate(data.getProben().get(i));
        }
    }

    private void generate(LafRawData.Probe object) {
        object.getAttributes();
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getErrors() {
        return errors;
    }

    /**
     * @return the warnings
     */
    public Map<String, List<ReportItem>> getWarnings() {
        return warnings;
    }
}
