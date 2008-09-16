package net.webassembletool.test.cases;

import java.io.File;

import net.webassembletool.test.junit.WebTestCase;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 */
public class WATTests extends WebTestCase {
    private String referenceFilesPath;

    public WATTests() {
	referenceFilesPath = System.getenv("referenceFilesPath");
	if (!referenceFilesPath.endsWith(File.separator))
	    referenceFilesPath += File.separator;
    }

    /**
     * Gets absolute path to reference data for given relative path relative is
     * merged with reference prefix.
     * 
     * @param relativePath relative path
     */

    private String getReferenceFilePath(String relativePath) {
	return referenceFilesPath
		+ (relativePath.startsWith(File.separator) ? relativePath
			.substring(1) : relativePath);
    }

    public void testBlock() {
	HttpMethod res = new GetMethod(getAbsoluteURL("/master/block.jsp"));
	assertStatus(res, 200);
	assertBodyContains(res, "&lt;--image gérée par le provider</div>");
    }

    public void testBlockAndReplace() {
	HttpMethod res = new GetMethod(
		getAbsoluteURL("/master/replaceblock.jsp"));
	assertStatus(res, 200);
	// S'assurer qu'on a bien récupéré quelque chose depuis le provider.
	assertBodyContains(res, "&lt;--image gérée par le provider</div>");
	// S'assurer qu'on a bien pu intégrer notre paramèttre
	assertBodyContains(res, "Bloc 2 (was : Bloc)");
	// S'assurer qu'on a bien retiré l'ancienne valeur du bloc
	assertBodyNotContains(res, "aqua\">Bloc de contenu<br");
    }

    public void testProviderWebapp() {
	assertBodyGetEqualsLocalFile(getAbsoluteURL("/provider"),
		getReferenceFilePath("provider"));
    }

    public void testMasterWebapp() {
	assertBodyGetEqualsLocalFile(getAbsoluteURL("/master"),
		getReferenceFilePath("master"));
    }

    public void testAggregatorWebapp() {
	assertBodyGetEqualsLocalFile(getAbsoluteURL("/aggregator"),
		getReferenceFilePath("aggregator"));
    }

}
