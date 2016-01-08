package de.intevation.lada.test.land;

import java.net.URL;
import java.util.List;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

public class QueryTest extends ServiceTest {

    /**
     * @return The test protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    @Override
    public void init(
        URL baseUrl,
        List<Protocol> protocol
    ) {
        super.init(baseUrl, protocol);
    }

    public final void execute() {
        getAll("query", "rest/query/probe");
        getAll("query", "rest/query/messprogramm");
    }
}
