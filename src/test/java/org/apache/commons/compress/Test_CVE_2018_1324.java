package org.apache.commons.compress;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.Test;

import java.io.IOException;

import static org.apache.commons.compress.AbstractTestCase.getFile;

public class Test_CVE_2018_1324 {

    @Test(timeout = 2000, expected = ArrayIndexOutOfBoundsException.class)
    // Running test should not be in an infinite loop
    public void testCVE_2018_1324() throws IOException {
        ZipFile zf = new ZipFile(getFile("difflist_fsbwserver.f-secure.com_80_583109529_2.zip"));
        zf.close();
    }
}

