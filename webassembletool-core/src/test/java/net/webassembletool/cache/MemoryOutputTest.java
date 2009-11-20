package net.webassembletool.cache;

import junit.framework.TestCase;

/**
 * Test class for net.webassembletool.cache.MemoryOutput.
 * 
 * @author nricheton
 */
public class MemoryOutputTest extends TestCase {

	final static String CONTENT = "My content";
	final static String CHARSET = "UTF-8";

	/**
	 * Ensure 'cacheMaxFileSize' from configuration works as expected.
	 * @see  "https://sourceforge.net/tracker/?func=detail&aid=2837564&group_id=209844&atid=1011350"
	 */
	public void testCacheMaxFileSize() {
		
		// Size > Content : content is written
		CacheOutput memoryOutput = new CacheOutput(500);
		memoryOutput.setCharsetName(CHARSET);
		memoryOutput.open();
		memoryOutput.write(CONTENT);
		memoryOutput.close();
		assertFalse(memoryOutput.toResource().isEmpty());

		// Size < Content : content is NOT written
		memoryOutput = new CacheOutput(5);
		memoryOutput.setCharsetName(CHARSET);
		memoryOutput.open();
		memoryOutput.write(CONTENT);
		memoryOutput.close();
		assertTrue(memoryOutput.toResource().isEmpty());

		// Size =0 (means no limit) : content is written
		// see http://webassembletool.sourceforge.net/configuration.html
		memoryOutput = new CacheOutput(0);
		memoryOutput.setCharsetName(CHARSET);
		memoryOutput.open();
		memoryOutput.write(CONTENT);
		memoryOutput.close();
		assertFalse(memoryOutput.toResource().isEmpty());

	}

}
