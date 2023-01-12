package org.apache.commons.compress;
  
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.X0017_StrongEncryptionHeader;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

import static org.apache.commons.compress.AbstractTestCase.getFile;

public class Test_CVE_2018_1324 {

    @Test(timeout = 6000)
    // Running test should not be in an infinite loop
    public void testCVE_2018_1324() throws IOException {
        // the data is crafted based on the file difflist_fsbwserver.f-secure.com_80_583109529_2.zip
        byte[] data = {23, 0, 0, 0, 1, 40, 0, 0, 0, 62, 55, 15, 87, 121, -27, -23, -5, 9, -118, -2, -45, -115, -63, -110, -24};
        int offset = 4;
        int length = 0;

        ZipShort.putShort(1, data, offset + 14); // reduce the number of second loops from 65152 -> 1 to save testing time

        assertEquals(3924130135L, ZipLong.getValue(data, offset + 8));
        assertEquals(1, ZipShort.getValue(data, offset + 14));

        X0017_StrongEncryptionHeader x0017 = new X0017_StrongEncryptionHeader();
        x0017.parseCentralDirectoryFormat(data, offset, length);
    }
}
