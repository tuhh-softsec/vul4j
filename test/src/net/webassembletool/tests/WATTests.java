package net.webassembletool.tests;

import java.io.File;
import java.io.IOException;

import net.webassembletool.webapptests.http.HttpAssert;
import net.webassembletool.webapptests.http.WebAppTestCase;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 */
public class WATTests extends WebAppTestCase {
    Log log = LogFactory.getLog(WATTests.class);

    String refPathPrefix;

    public WATTests() throws IOException {
	super("bin");

	File refPath = new File(System.getenv("TEST_REF_DIR"));
	refPathPrefix = refPath.getAbsolutePath();

	if (!refPathPrefix.endsWith(File.separator))
	    refPathPrefix += File.separator;
    }

    /**
     * Gets absolute path to reference data for given relative path relative is merged
     * with refernce prefix.
     * 
     * @param relative
     *            relative path
     */

    private String referencePath(String relative) {
	return refPathPrefix
		+ (relative.startsWith(File.separator) ? relative.substring(1)
			: relative);
    }

    public void testBlock() throws Exception {
	HttpMethod res = new GetMethod(getAbsoluteURL("/master/block.jsp"));
	HttpAssert.assertStatus(res, 200);
	HttpAssert.assertBodyContains(res,
		"&lt;--image gérée par le provider</div>");
    }

    public void testBlockAndReplace() throws Exception {
	HttpMethod res = createGet("/master/replaceblock.jsp");
	HttpAssert.assertStatus(res, 200);
	// S'assurer qu'on a bien récupéré quelque chose depuis le provider.
	HttpAssert.assertBodyContains(res,
		"&lt;--image gérée par le provider</div>");
	// S'assurer qu'on a bien pu intégrer notre paramèttre
	HttpAssert.assertBodyContains(res, "Bloc 2 (was : Bloc)");
	// S'assurer qu'on a bien retiré l'ancienne valeur du bloc
	HttpAssert.assertBodyNotContains(res, "aqua\">Bloc de contenu<br");
    }

    public void testProviderWebapp() throws Exception {
	HttpAssert.assertBodyGetEqualsLocalFile(getAbsoluteURL("/provider"),
		referencePath("provider"));
    }
    
    public void testMasterWebapp() throws Exception {
	HttpAssert.assertBodyGetEqualsLocalFile(getAbsoluteURL("/master"),
		referencePath("master"));
    }
    
    public void testAggregatorWebapp() throws Exception {
	HttpAssert.assertBodyGetEqualsLocalFile(getAbsoluteURL("/aggregator"),
		referencePath("aggregator"));
    }

}
