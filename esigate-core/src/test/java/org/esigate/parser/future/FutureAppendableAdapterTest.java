package org.esigate.parser.future;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.Assert;

import org.esigate.HttpErrorPage;
import org.junit.Test;

public class FutureAppendableAdapterTest {

    @Test
    public void testBasic() throws IOException, HttpErrorPage {

        StringWriter sw = new StringWriter();

        FutureAppendableAdapter adapter = new FutureAppendableAdapter(sw);

        adapter.enqueueAppend(new CharSequenceFuture("test1"));
        adapter.enqueueAppend(new CharSequenceFuture("test2"));
        adapter.enqueueAppend(new CharSequenceFuture("test3"));
        adapter.enqueueAppend(new CharSequenceFuture("test4"));

        adapter.performAppends();
        Assert.assertEquals("test1test2test3test4", sw.getBuffer().toString());
    }

}
