package de.intevation.lada;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.importer.TestLAFImporter;


@Ignore
public class ImporterTest
{
    /*
    @Ignore
    public static void main(String[] args) {
        System.setProperty(
            "de.intevation.lada.test.singleprobe",
            "/home/rrenkert/single.laf");
        System.setProperty(
            "de.intevation.lada.test.incompleteprobe",
            "/home/rrenkert/incomplete.laf");
        System.setProperty(
            "de.intevation.lada.import",
            "/opt/lada/config/import.json");
        System.out.println("ImporterTest started.");
        TestLAFImporter test = new TestLAFImporter();
        test.loadLafFiles();

        System.out.print("Testing config file not found: ");
        test.testConfigFileNotFound();
        System.out.print("success.\n");

        System.out.print("Testing config file: ");
        test.testConfigFileLoading();
        System.out.print("success.\n");

        System.out.print("Testing Parser:\n  1. Wrong header: ");
        test.testProbeHeaderFail();
        System.out.print("success.\n  2. Complete probe: ");
        test.testCompleteParser();
        System.out.print("success.\n");

        //Prepare simulated authorization.
        List<String> netzbetreiber = new ArrayList<String>();
        netzbetreiber.add("0611");
        List<String> mst = new ArrayList<String>();
        mst.add("06110");
        mst.add("06112");
        AuthenticationResponse auth = new AuthenticationResponse();
        auth.setUser("testeins");
        auth.setNetzbetreiber(netzbetreiber);
        auth.setMst(mst);
        auth.setNetzbetreiber(netzbetreiber);


        System.out.println("end.");
        return;
    }*/
}
