package de.intevation.lada.test;

import org.junit.Assert;

import de.intevation.lada.rest.QueryService;
import de.intevation.lada.util.rest.Response;


public class QueryServiceTest {

    public final void test(QueryService queryService) throws Exception {
        queryService(queryService);
    }

    private final void queryService(QueryService queryService)
    throws Exception {
        Response response = queryService.get();
        Assert.assertEquals("200", response.getMessage());
        Assert.assertTrue(response.getSuccess());
        Assert.assertNotNull(response.getData());
    }

}
