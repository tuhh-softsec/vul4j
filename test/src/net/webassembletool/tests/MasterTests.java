package net.webassembletool.tests;

import java.io.File;
import java.io.IOException;

import net.webassembletool.webapptest.HttpAssert;
import net.webassembletool.webapptest.WebAppTestCase;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 */
public class MasterTests extends WebAppTestCase {
    Log log = LogFactory.getLog(MasterTests.class);

    String refURLPrefix;
    
    public MasterTests() throws IOException {
	super("bin");
	refURLPrefix = new File(System.getenv("TEST_REF_DIR")).toURI().toURL().toString();
	if(!refURLPrefix.endsWith("/")) refURLPrefix += "/";
	log.debug("Using referance location : " +  refURLPrefix);
    }
    /**
     * 
     * Gets url to reference data for given relative path relative is merged with refernce prefix.
     *
     * @param relative relative path
     */
    private String referenceURL(String relative){
	return refURLPrefix + (relative.startsWith("/") ? relative.substring(1) : relative); 
    }
    
    public void testBlock() throws Exception {
	HttpMethod res = new GetMethod(getAbsoluteURL("/master/block.jsp"));
	HttpAssert.assertStatus(res, 200);
	HttpAssert.assertBodyContains(res, "&lt;--image gérée par le provider</div>");
    }

    public void testBlockAndReplace() throws Exception {
	HttpMethod res = createGet("/master/replaceblock.jsp");
	HttpAssert.assertStatus(res, 200);
	// S'assurer qu'on a bien récupéré quelque chose depuis le provider.
	HttpAssert.assertBodyContains(res, "&lt;--image gérée par le provider</div>");
	// S'assurer qu'on a bien pu intégrer notre paramèttre
	HttpAssert.assertBodyContains(res, "Bloc 2 (was : Bloc)");
	// S'assurer qu'on a bien retiré l'ancienne valeur du bloc
	HttpAssert.assertBodyNotContains(res, "aqua\">Bloc de contenu<br");
    }

    public void testTemplate() throws Exception {
	HttpMethod res = createGet("/master/template.jsp");
	HttpAssert.assertStatus(res, 200);
	//Check that body equals reference data
	HttpAssert.assertBodyEqualsURLBody(res, referenceURL("/master/template.jsp.html"));
    }

}
