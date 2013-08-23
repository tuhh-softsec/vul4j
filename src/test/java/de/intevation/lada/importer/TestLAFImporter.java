package de.intevation.lada.importer;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.intevation.lada.data.importer.EntryFormat;
import de.intevation.lada.data.importer.LAFFormat;
import de.intevation.lada.data.importer.LAFParser;
import de.intevation.lada.data.importer.LAFParserException;


public class TestLAFImporter
{

    private static final String PROBE_HEADER_FAIL =
        "%OBE%\n";
    private String singleProbe;
    private String incompleteProbe;

    @Before
    public void loadLafFiles() {
        String single = System.getProperty("de.intevation.lada.test.singleprobe");
        String incomplete =
            System.getProperty("de.intevation.lada.test.incompleteprobe");
        try {
            byte[] encodedSingle = Files.readAllBytes(Paths.get(single));
            byte[] encodedIncomplete =
                Files.readAllBytes(Paths.get(incomplete));
            Charset encoding = Charset.defaultCharset();
            singleProbe =
                encoding.decode(ByteBuffer.wrap(encodedSingle)).toString();
            incompleteProbe =
                encoding.decode(ByteBuffer.wrap(encodedIncomplete)).toString();
        }
        catch (IOException ioe) {
            singleProbe = "";
            incompleteProbe = "";
        }
    }

    @Test
    public void testConfigFileNotFound() {
        LAFFormat format = new LAFFormat();
        boolean success = format.readConfigFile("/file/not/found");
        assertEquals(false, success);
    }

    @Test
    public void testConfigFileLoading() {
        String fileName = System.getProperty("de.intevation.lada.import");
        LAFFormat format = new LAFFormat();
        boolean success = format.readConfigFile(fileName);
        assertEquals(true, success);
        List<EntryFormat> probeFormat = format.getFormat("probe");
        assertNotNull("No probe format available", probeFormat);
        assertEquals(
            "Not enough configuration elements for probe.",
            32,
            probeFormat.size());
        List<EntryFormat> messungFormat = format.getFormat("messung");
        assertNotNull("No messung format available", messungFormat);
        assertEquals(
            "Not enough configuration elements for messung.",
            10,
            messungFormat.size());
        List<EntryFormat> ortFormat = format.getFormat("ort");
        assertNotNull("No ort format available", ortFormat);
        assertEquals(
            "Not enough configuration elements for ort.",
            20,
            ortFormat.size());
    }

    @Test
    public void testProbeHeaderFail() {
        LAFParser parser = new LAFParser();
        parser.setDryRun(true);
        try {
            boolean success = parser.parse(PROBE_HEADER_FAIL);
            assertEquals(false, success);
        }
        catch (LAFParserException e) {
            assertEquals(
                "Exception cause not expected: " + e.getMessage(),
                "No %PROBE% at the begining.", e.getMessage());
        }
    }

    @Test
    @Ignore
    public void testIncompleteProbe() {
        LAFParser parser = new LAFParser();
        parser.setDryRun(true);
        try {
            parser.parse(incompleteProbe);
        }
        catch (LAFParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCompleteParser() {
        LAFParser parser = new LAFParser();
        parser.setDryRun(true);
        try {
            parser.parse(singleProbe);
        }
        catch (LAFParserException e) {
            e.printStackTrace();
        }
    }
}
