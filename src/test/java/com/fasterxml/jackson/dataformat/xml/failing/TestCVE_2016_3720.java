package com.fasterxml.jackson.dataformat.xml.failing;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import static org.junit.Assert.*;

import java.util.Map;

public class TestCVE_2016_3720 extends XmlTestBase {

    public void testCVE_2016_3720() throws Exception {
        InputStream in = getClass().getResourceAsStream("/simple_xxe.xml");
        InputStreamReader inReader = new InputStreamReader(in);

        BufferedReader reader = new BufferedReader(inReader);
        StringBuffer sb = new StringBuffer();
        String xml;
        while((xml = reader.readLine())!= null){
            sb.append(xml);
        }
        XmlMapper mapper = new XmlMapper();
        try {
            mapper.readValue(sb.toString(), Map.class);
        } catch (Exception e) {
            assertTrue(e.getMessage() != null && e.getMessage().contains("\"javax.xml.stream.isSupportingExternalEntities\" disabled"));
            return;
        }
        fail("should raise exception");
    }
}
