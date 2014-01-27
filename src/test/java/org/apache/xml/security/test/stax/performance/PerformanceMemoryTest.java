/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.stax.performance;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerformanceMemoryTest extends AbstractPerformanceTest {

    private static final int runs = 40;
    private static final int xmlResizeFactor = 1000;

    //junit creates for every test method a new class instance so we need a static list
    private static Map<Integer, File> signedFiles = new TreeMap<Integer, File>();
    private static Map<Integer, File> encryptedFiles = new TreeMap<Integer, File>();


    @Override
    protected File getTmpFilePath() {
        return new File("target/performanceMemoryTest");
    }

    @Test
    public void testRunFirstOutboundSignatureMemoryPerformance() throws Exception {
        System.out.println("Testing Outbound Signature Memory Performance");
        FileWriter outSignatureSamplesWriter = new FileWriter("target/signatureOutMemorySamples.txt", false);
        for (int i = 1; i <= runs; i++) {
            System.out.println("Run " + i);

            File file = generateLargeXMLFile(i * xmlResizeFactor);

            int startTagCount = countXMLStartTags(file);
            outSignatureSamplesWriter.write("" + startTagCount);

            long startMem = getUsedMemory();
            MemorySamplerThread mst = new MemorySamplerThread(startMem);
            Thread thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            File signedFile = doStreamingSignatureOutbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            outSignatureSamplesWriter.write(" " + mst.getMaxUsedMemory());
            signedFiles.put(startTagCount, signedFile);

            startMem = getUsedMemory();
            mst = new MemorySamplerThread(startMem);
            thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doDOMSignatureOutbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            outSignatureSamplesWriter.write(" " + mst.getMaxUsedMemory());

            outSignatureSamplesWriter.write("\n");
        }
        outSignatureSamplesWriter.close();
    }

    @Test
    public void testRunSecondInboundSignatureMemoryPerformance() throws Exception {
        System.out.println("Testing Inbound Signature Memory Performance");
        FileWriter inSignatureSamplesWriter = new FileWriter("target/signatureInMemorySamples.txt", false);

        int run = 1;
        Iterator<Map.Entry<Integer, File>> mapIterator = signedFiles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Integer, File> entry = mapIterator.next();
            System.out.println("Run " + (run++));

            File file = entry.getValue();
            Integer startTagCount = entry.getKey();
            inSignatureSamplesWriter.write("" + startTagCount);

            long startMem = getUsedMemory();
            MemorySamplerThread mst = new MemorySamplerThread(startMem);
            Thread thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doStreamingSignatureInbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            inSignatureSamplesWriter.write(" " + mst.getMaxUsedMemory());

            startMem = getUsedMemory();
            mst = new MemorySamplerThread(startMem);
            thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doDOMSignatureInbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            inSignatureSamplesWriter.write(" " + mst.getMaxUsedMemory());

            inSignatureSamplesWriter.write("\n");
        }
        inSignatureSamplesWriter.close();
    }

    @Test
    public void testRunFirstOutboundEncryptionMemoryPerformance() throws Exception {
        System.out.println("Testing Outbound Encryption Memory Performance");
        FileWriter outEncryptionSamplesWriter = new FileWriter("target/encryptionOutMemorySamples.txt", false);
        for (int i = 1; i <= runs; i++) {
            System.out.println("Run " + i);

            File file = generateLargeXMLFile(i * xmlResizeFactor);

            int startTagCount = countXMLStartTags(file);
            outEncryptionSamplesWriter.write("" + startTagCount);

            long startMem = getUsedMemory();
            MemorySamplerThread mst = new MemorySamplerThread(startMem);
            Thread thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            File encryptedFile = doStreamingEncryptionOutbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            outEncryptionSamplesWriter.write(" " + mst.getMaxUsedMemory());
            encryptedFiles.put(startTagCount, encryptedFile);

            startMem = getUsedMemory();
            mst = new MemorySamplerThread(startMem);
            thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doDOMEncryptionOutbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            outEncryptionSamplesWriter.write(" " + mst.getMaxUsedMemory());

            outEncryptionSamplesWriter.write("\n");
        }
        outEncryptionSamplesWriter.close();
    }

    @Test
    public void testRunSecondInboundDecryptionMemoryPerformance() throws Exception {
        System.out.println("Testing Inbound Decryption Memory Performance");
        FileWriter inEncryptionSamplesWriter = new FileWriter("target/encryptionInMemorySamples.txt", false);

        int run = 1;
        Iterator<Map.Entry<Integer, File>> mapIterator = encryptedFiles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Integer, File> entry = mapIterator.next();
            System.out.println("Run " + (run++));

            File file = entry.getValue();
            Integer startTagCount = entry.getKey();
            inEncryptionSamplesWriter.write("" + startTagCount);

            long startMem = getUsedMemory();
            MemorySamplerThread mst = new MemorySamplerThread(startMem);
            Thread thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doStreamingDecryptionInbound(file, startTagCount);
            mst.setStop(true);
            thread.join();
            inEncryptionSamplesWriter.write(" " + mst.getMaxUsedMemory());

            startMem = getUsedMemory();
            mst = new MemorySamplerThread(startMem);
            thread = new Thread(mst);
            thread.setPriority(9);
            thread.start();
            doDOMDecryptionInbound(file, startTagCount);
            inEncryptionSamplesWriter.write(" " + mst.getMaxUsedMemory());
            mst.setStop(true);
            thread.join();

            inEncryptionSamplesWriter.write("\n");
        }
        inEncryptionSamplesWriter.close();
    }

    private static void gc() {
        System.gc();
        System.runFinalization();
        System.gc();
    }

    private static long getUsedMemory() {
        gc();
        gc();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return totalMemory - freeMemory;
    }

    class MemorySamplerThread implements Runnable {

        private long memoryDiff = 0;
        private volatile boolean stop = false;

        private List<Integer> memory = new LinkedList<Integer>();

        MemorySamplerThread(long memoryDiff) {
            this.memoryDiff = memoryDiff;
            System.out.println("memory diff " + memoryDiff / 1024 / 1024);
        }

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void run() {
            int sleepTime = 50;
            while (!isStop()) {
                try {
                    Thread.sleep(sleepTime);
                    if (isStop()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                memory.add((int) (((getUsedMemory()) - memoryDiff) / 1024.0 / 1024.0));
            }
        }

        public int getMaxUsedMemory() {
            System.out.println("Collected " + memory.size() + " samples");
            int maxMem = Integer.MIN_VALUE;
            for (int i = 0; i < memory.size(); i++) {
                int mem = memory.get(i);
                maxMem = mem > maxMem ? mem : maxMem;
            }
            System.out.println("Max memory usage: " + maxMem + "MB");
            
            if (maxMem > Integer.MIN_VALUE) {
                return maxMem;
            }
            return 0;
        }
    }
}
