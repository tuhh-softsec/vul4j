package org.apache.tika.parser.iptc;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

public class IptcParserTest {

    private final Parser parser = new IptcAnpaParser();

    @Test(timeout = 2000)
    public void testCVE_2018_8017() throws Exception {
        Metadata metadata = new Metadata();
        InputStream stream = getClass().getResourceAsStream("/test-documents/12_hang_tika_iptc.iptc");
        parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
    }
}
