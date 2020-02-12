/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.land;

import java.io.StringReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import de.intevation.lada.BaseTest;
import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

/**
 * Class containing methods used for testing the generation of probe records
 * from a messprogramm.
 */
public class PepGenerationTest extends ServiceTest {

    /**
     * Messprogramm records from datasource
     */
    JsonArray messprogramms;

    /**
     * Current expected tag serial number
     */
    int expectedTagSerNo = 0;

    @Override
    public void init(
        URL baseUrl,
        List<Protocol> protocol
    ) {
        super.init(baseUrl, protocol);
        messprogramms = readJsonResource("/datasets/dbUnit_pep_gen.json")
            .getJsonArray("stamm.messprogramm");
    }

    /**
     * Execute all available tests
     */
    public void execute() {
        //Test generation in specific intervals
        testDailyGeneration();
        testWeeklyGeneration();
        test2WeeklyGeneration();
        test4WeeklyGeneration();
        testMonthlyGeneration();
        testQuarterlyGeneration();
        testHalfYearlyGeneration();
        testYearlyGeneration();

        //Test generation during leap years with interval offset
        testYearlyGenerationInLeapYear();
        testHalfYearlyGenerationInLeapYear();
        testQuarterlyGenerationInLeapYear();
        testMonthlyGenerationInLeapYear();
        test4WeeklyGenerationInLeapYear();
        test2WeeklyGenerationInLeapYear();
        testWeeklyGenerationInLeapYear();
        testDailyGenerationInLeapYear();

        //Various tests
        testPartialIntevals();
        testGenerationFromIdList();
        testGenerationRejectUnauthorized();
        testGenerationRejectInvalidParams();
        testGenerationRejectNegativeParams();
    }

    /**
     * Test the generation of daily probe records
     */
    private void testDailyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("Daily");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1007;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/01/2020 @ 12:00am (UTC)
        Long start = 1580515200000L;
        // 02/12/2020 @ 12:00am (UTC)
        Long end = 1581465600000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(12, entity, prot, mpId);
        checkGeneratedTag(entity, prot);
        prot.setPassed(true);
    }

    /**
     * Test the generation of weekly probe records
     */
    private void testWeeklyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("Weekly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1006;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/01/2020 @ 12:00am (UTC)
        Long start = 1580515200000L;
        // 06/01/2020 @ 12:00am (UTC)
        Long end = 1590969600000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(18, entity, prot, mpId);
        checkGeneratedTag(entity, prot);
        prot.setPassed(true);
    }

    /**
     * Test the generation of two-weekly probe records
     */
    private void test2WeeklyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("2Weekly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1005;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/01/2020 @ 12:00am (UTC)
        Long start = 1580515200000L;
        // 06/01/2020 @ 12:00am (UTC)
        Long end = 1590969600000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(9, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Test the generation of four-weekly probe records
     */
    private void test4WeeklyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("4Weekly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1004;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/01/2020 @ 12:00am (UTC)
        Long start = 1580515200000L;
        // 06/01/2020 @ 12:00am (UTC)
        Long end = 1590969600000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(5, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Test the generation of monthly probe records
     */
    private void testMonthlyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("monthly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1003;
        List<Integer> id = new ArrayList<Integer>();
        id.add(mpId);

        //Generate 61 records for five years
        //01/01/2025 @ 12:00am (UTC)
        Long start = 1735689600000L;
        //01/01/2030 @ 12:00am (UTC)
        Long end =   1893456000000L;
        String entity = generateFromMpIds(id, start, end);
        checkGeneratedProbeCount(61, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Check the generation of quarterly probe records
     */
    private void testQuarterlyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("quarterly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1002;
        List<Integer> id = new ArrayList<Integer>();
        id.add(mpId);
        //01/01/2020 @ 12:00am (UTC)
        Long start = 1577836800000L;
        //02/02/2021 @ 12:00am (UTC)
        Long end = 1612224000000L;
        String entity = generateFromMpIds(id, start, end);
        checkGeneratedProbeCount(4, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Test the generation of half-yearly probe records
     */
    private void testHalfYearlyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("half yearly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1001;
        List<Integer> id = new ArrayList<Integer>();
        id.add(mpId);

        //01/01/2020 @ 12:00am (UTC)
        Long start = 1577836800000L;
        //02/02/2021 @ 12:00am (UTC)
        Long end = 1612224000000L;
        String entity = generateFromMpIds(id, start, end);
        checkGeneratedProbeCount(2, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true); 
    }

    /**
     * Test a simple yearly generation of probe records
     */
    private void testYearlyGeneration() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("yearly");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1000;
        List<Integer> id = new ArrayList<Integer>();
        id.add(mpId);

        //01/01/2020 @ 12:00am (UTC)
        Long start = 1577836800000L;
        //01/01/2030 @ 12:00am (UTC)
        Long end = 1893456000000L;

        String entity = generateFromMpIds(id, start, end);
        checkGeneratedProbeCount(11, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Test the generation of probe records starting on the 29th of February
     * in a leap year
     */
    private void testYearlyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("yearly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        Integer mpId = 1100;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        //02/29/2020 @ 12:00am (UTC)
        Long start = 1577836800000L;
        //03/01/2030 @ 12:00am (UTC)
        Long end = 1898553600000L;

        String entity = generateFromMpIds(idParam, start, end);
        int expectedCount = 11;
        checkGeneratedProbeCount(expectedCount, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582934400000L);
        //Expected second record: 03/01/2021 @ 12:00am (UTC)
        expectedValues.put(1, 1614556800000L);
        //Expected last record: 02/29/2030 @ 12:00am (UTC)
        expectedValues.put(expectedCount - 1, 1898553600000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);

        prot.setPassed(true);
    }

    /**
     * Test the half yearly generation of probe records in leap years
     */
    private void testHalfYearlyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("half yearly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        Integer mpId = 1015;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        //02/29/2016 @ 12:00am (UTC)
        Long start = 1456704000000L;
        //02/29/2020 @ 12:00am (UTC)
        Long end = 1582934400000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(8, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 08/28/2016 @ 12:00am (UTC)
        expectedValues.put(0, 1472428800000L);
        //Expected last record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(7, 1582934400000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);

        prot.setPassed(true);
    }

    /**
     * Test the quarterly generation of probe records in leap years
     */
    private void testQuarterlyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("quarterly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        Integer mpId = 1016;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        //02/29/2016 @ 12:00am (UTC)
        Long start = 1456704000000L;
        //02/29/2020 @ 12:00am (UTC)
        Long end = 1582934400000L;

        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(16, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 05/30/2016 @ 12:00am (UTC)
        expectedValues.put(0, 1464566400000L);
        //Expected last record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(15, 1582934400000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);
        

        prot.setPassed(true);
    }

   /**
     * Test the generation of monthly probe records in leap years
     */
    private void testMonthlyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("monthly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        Integer mpId = 1103;
        List<Integer> idParam = new ArrayList<Integer>();

        idParam.add(mpId);
        //01/29/2020 @ 12:00am (UTC)
        Long start = 1580256000000L;
        //04/01/2021 @ 12:00am (UTC)
        Long end =   1617235200000L;
        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(15, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582934400000L);
        //Expected second record: 03/29/2020 @ 12:00am (UTC)
        expectedValues.put(1, 1585440000000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);

        prot.setPassed(true);
    }

    /**
    * Test the generation of four-weekly probe records in leap years
    */
    private void test4WeeklyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("4Weekly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1017;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/24/2020 @ 12:00am (UTC)
        Long start = 1582502400000L;
        // 03/01/2021 @ 12:00am (UTC)
        Long end = 1614556800000L;

        String entity = generateFromMpIds(idParam, start, end);
        int expectedCount = 13;
        checkGeneratedProbeCount(expectedCount, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582934400000L);
        //Expected last record: 02/27/2021 @ 12:00am (UTC)
        expectedValues.put(expectedCount - 1, 1614384000000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);


        prot.setPassed(true);
    }

    /**
     * Test the generation of two-weekly probe records in leap years
    */
    private void test2WeeklyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("2Weekly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1018;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/24/2020 @ 12:00am (UTC)
        Long start = 1582502400000L;
        // 03/01/2021 @ 12:00am (UTC)
        Long end = 1614556800000L;

        String entity = generateFromMpIds(idParam, start, end);
        int expectedCount = 26;
        checkGeneratedProbeCount(expectedCount, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582934400000L);
        //Expected last record: 02/27/2021 @ 12:00am (UTC)
        expectedValues.put(expectedCount - 1, 1614384000000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);

        prot.setPassed(true);
    }

    /**
     * Test the generation of weekly probe records in leap years
    */
    private void testWeeklyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("Weekly in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1019;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        // 02/24/2020 @ 12:00am (UTC)
        Long start = 1582502400000L;
        // 03/01/2021 @ 12:00am (UTC)
        Long end = 1614556800000L;

        String entity = generateFromMpIds(idParam, start, end);
        int expectedCount = 53;
        checkGeneratedProbeCount(expectedCount, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582934400000L);
        //Expected last record: 03/06/2021 @ 12:00am (UTC)
        expectedValues.put(expectedCount - 1, 1614988800000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);

        prot.setPassed(true);
    }

    /**
     * Test the generation of daily probe records in a leap year.
     * Should generate 368 records from 02/28/2020 to 03/01/2021.
     */
    private void testDailyGenerationInLeapYear() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("daily in leap year");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId =1012;
        List<Integer> idParam = new ArrayList<Integer>();
        idParam.add(mpId);

        //02/28/2020 @ 12:00am (UTC)
        Long start = 1582848000000L;
        //03/01/2021 @ 12:00am (UTC)
        Long end = 1614556800000L;
        String entity = generateFromMpIds(idParam, start, end);
        int expectedCount = 368;
        checkGeneratedProbeCount(expectedCount, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        //Check return data
        String startAttribute = "solldatumBeginn";
        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();
        //Expected first record: 02/28/2020 @ 12:00am (UTC)
        expectedValues.put(0, 1582848000000L);
        //Expected second record: 02/29/2020 @ 12:00am (UTC)
        expectedValues.put(1, 1582934400000L);
        //Expected last record: 03/01/2021 @ 12:00am (UTC)
        expectedValues.put(expectedCount - 1, 1614556800000L);
        checkEntityAttributeValues(entity, mpId, startAttribute, expectedValues);
        

        prot.setPassed(true);
    }
    /**
     * Test the generation of probe records with a partial interval set.
     */
    private void testPartialIntevals() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("monthly with partial interval");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1008;
        List<Integer> idParam = new ArrayList<Integer>();

        idParam.add(mpId);
        //01/29/2020 @ 12:00am (UTC)
        Long start = 1580256000000L;
        //04/01/2021 @ 12:00am (UTC)
        Long end =   1617235200000L;
        String entity = generateFromMpIds(idParam, start, end);
        checkGeneratedProbeCount(14, entity, prot, mpId);
        checkGeneratedTag(entity, prot);

        prot.setPassed(true);
    }

    /**
     * Tests the genereation from a list of mpIds.
     */
    private void testGenerationFromIdList() {
        Protocol prot = new Protocol();
        prot.setName("Pep Gen");
        prot.setType("Generation from list");
        prot.setPassed(false);
        protocol.add(prot);

        //01/01/2020 @ 12:00am (UTC)
        Long start = 1577836800000L;
        //02/14/2020 @ 12:00am (UTC)
        Long end = 1581638400000L;
        int monthlyMpId = 1013;
        int dailyMpId = 1014;

        List<Integer> idParam = Arrays.asList(monthlyMpId, dailyMpId);
        String entity = generateFromMpIds(idParam, start, end);
        //Monthy mp should generate two records
        checkGeneratedProbeCount(2, entity, prot, monthlyMpId);
        //Daily mp should generate 45 records
        checkGeneratedProbeCount(45, entity, prot, dailyMpId);
        prot.setPassed(true);
    }

    /**
     * Test if a generation request will be rejected if unathorized.
     */
    private void testGenerationRejectUnauthorized() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("reject unauthorized");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1009;
        List<Integer> idParam = new ArrayList<Integer>();

        idParam.add(mpId);
        //01/29/2020 @ 12:00am (UTC)
        Long start = 1580256000000L;
        //04/01/2021 @ 12:00am (UTC)
        Long end =   1617235200000L;
        String entity = generateFromMpIds(idParam, start, end);

        //Request should have failed with message 699
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject content = reader.readObject();
        JsonObject data = content.getJsonObject("data");
        JsonObject mpData = data.getJsonObject("proben").getJsonObject(Integer.toString(mpId));

        Assert.assertTrue(mpData.get("data") == JsonValue.NULL);
        Assert.assertFalse(mpData.getBoolean("success"));
        Assert.assertEquals(699, mpData.getInt("message"));

        prot.setPassed(true);
    }

    /**
     * Test if server rejects a request containing invalid params
     */
    private void testGenerationRejectInvalidParams() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("reject invalid params");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1010;
        List<Integer> idParam = new ArrayList<Integer>();

        idParam.add(mpId);
        //01/29/2020 @ 12:00am (UTC)
        Long end = 1580256000000L;
        //04/01/2021 @ 12:00am (UTC)
        Long start =   1617235200000L;
        String entity = generateFromMpIds(idParam, start, end);

        //Request should have failed with message 699
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject content = reader.readObject();
        JsonObject data = content.getJsonObject("data");
        JsonObject mpData = data.getJsonObject("proben").getJsonObject(Integer.toString(mpId));

        Assert.assertTrue(mpData.get("data") == JsonValue.NULL);
        Assert.assertFalse(mpData.getBoolean("success"));
        Assert.assertEquals(699, mpData.getInt("message"));

        prot.setPassed(true);
    }

    /**
     * Test if generation request is rejected if time parameters are invalid
     */
    private void testGenerationRejectNegativeParams() {
        Protocol prot = new Protocol();
        prot.setName("PEP-Gen");
        prot.setType("reject negative params");
        prot.setPassed(false);
        protocol.add(prot);

        int mpId = 1010;
        List<Integer> idParam = new ArrayList<Integer>();

        idParam.add(mpId);
        //01/29/2020 @ 12:00am (UTC)
        Long end = -1L;
        //04/01/2021 @ 12:00am (UTC)
        Long start = -5L;
        String entity = generateFromMpIds(idParam, start, end);

        //Request should have failed with message 699
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject content = reader.readObject();
        JsonObject data = content.getJsonObject("data");
        JsonObject mpData = data.getJsonObject("proben").getJsonObject(Integer.toString(mpId));

        Assert.assertTrue(mpData.get("data") == JsonValue.NULL);
        Assert.assertFalse(mpData.getBoolean("success"));
        Assert.assertEquals(699, mpData.getInt("message"));

        prot.setPassed(true);
    }



    /**
     * Checks if the tag stored in the given entity matches the expected one
     * @param entity Entity to check
     * @param prot Protocol to use
     */
    private void checkGeneratedTag(String entity, Protocol prot) {
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject content = reader.readObject();
        JsonObject data = content.getJsonObject("data");
        String tag = data.getString("tag");

        String date = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        String expectedTag = "PEP_" + date + "_" + expectedTagSerNo;

        Assert.assertFalse(tag == null || tag.equals(""));
        Assert.assertTrue(tag.equals(expectedTag));
    }

    /**
     * Checks if a generation process resulted in the expected number of probe records
     * @param count Expected count of records
     * @param entity Result entity to check
     * @param prot Protocol to use
     */
    private void checkGeneratedProbeCount(int count, String entity, Protocol prot, int mpId) {
        try{
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            JsonObject data = content.getJsonObject("data");
            JsonArray proben = null;
            try {
                /* Verify the response*/
                Assert.assertTrue(content.getBoolean("success"));
                prot.addInfo("success", content.getBoolean("success"));
                Assert.assertEquals("200", content.getString("message"));
                prot.addInfo("message", content.getString("message"));
                Assert.assertNotNull(content.getJsonObject("data"));

                //Get data for given messprogramm
                JsonObject mpData = data.getJsonObject("proben").getJsonObject("" + mpId);
                Assert.assertNotNull(mpData);

                //Check if data is an array of records
                proben = mpData.getJsonArray("data");
            } catch (ClassCastException cce) {
                Assert.fail(cce.getMessage());
            }

            prot.addInfo("objects", proben.size());
            Assert.assertEquals(count, proben.size());
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
    }

    /**
     * Generate probe records from a list of messprogramm ids, a start timestamp
     * and an end timestamp
     * @param ids List of messprogramm ids to generate from
     * @param start Timestamp in ms to start with
     * @param end Timestamp in ms to end with
     * @return Response enitity String containing the generated objects
     */
    private String generateFromMpIds(List<Integer> ids, Long start, Long end) {
        System.out.print(".");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "rest/probe/messprogramm");
        JsonArrayBuilder idArrayBuilder = Json.createArrayBuilder();
        ids.forEach(item -> {
            idArrayBuilder.add(item);
        });
        JsonObject payload = Json.createObjectBuilder()
            .add("start", start)
            .add("end", end)
            .add("ids", idArrayBuilder.build()).build();

        Response response = target.request()
            .header("X-SHIB-user", BaseTest.TEST_USER)
            .header("X-SHIB-roles", BaseTest.TEST_ROLES)
            .post(Entity.json(payload.toString()));

        String entity = response.readEntity(String.class);
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject content = reader.readObject();
        JsonObject data = content.getJsonObject("data");

        //If a tag was applied, increase serial number
        if (data.containsKey("tag") && data.getString("tag") != null) {
            expectedTagSerNo++;
        }
        return entity;
    }

    /**
     * Check if the given entity's long attribute equals the expected values given in a map
     * @param entity Entity to check
     * @param mpId mpId to check
     * @param attribute Attribute name to check
     * @param expectedValues Map containing record index as key and expected value as value
     */
    private void checkEntityAttributeValues(String entity, Integer mpId, String attribute, Map<Integer, Long> expectedValues) {
        expectedValues.forEach((index, value) -> {
            JsonObject record = getRecordAtIndex(entity, mpId, index);
            Assert.assertNotNull(record);
            long startDate = record.getJsonNumber(attribute).longValue();
            Assert.assertEquals(value.longValue(), startDate);
        });
    }

    /**
     * Parses an entity and returns the record at the given index for messprogramm with given id.
     * @param entity Entity to use
     * @param mpId MpId to uses
     * @param index Record index
     * @return Record as JsonObject
     */
    private JsonObject getRecordAtIndex(String entity, Integer mpId, int index) {
        JsonObject result = null;
        try {
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            JsonObject data = content.getJsonObject("data");
            JsonArray proben = data.getJsonObject("proben")
                    .getJsonObject(mpId.toString()).getJsonArray("data");
            result = proben.getJsonObject(index);
        } catch (JsonException je) {
            return null;
        }
        return result;
    }
}