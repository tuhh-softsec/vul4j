/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.webassembletool.maven.assembly;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.webassembletool.HttpErrorPage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * AssemblyMojo Integration Test
 * 
 * @author Alexis Thaveau
 * 
 */
public class AssemblyMojoTest extends AbstractMojoTestCase {

	protected AssemblyMojo newAssemblyMojo(String pDir, String pOutputDir,
			String charset) throws Exception {

		File testbasedir = new File(getBasedir(), pDir);
		File testoutputdir = new File(getBasedir(), pOutputDir);

		File testPom = new File(testbasedir, "pom.xml");
		AssemblyMojo vm = (AssemblyMojo) lookupMojo("assembly", testPom);

		// setVariableValueToObject(vm, "basedir", testbasedir);
		setVariableValueToObject(vm, "outputDirectory", testoutputdir);
		setVariableValueToObject(vm, "pagesDirectory", new File(testbasedir,
				"pages"));
		setVariableValueToObject(vm, "modulesDirectory", new File(testbasedir,
				"modules"));

		//If not set in plugin configuration
		if (vm.getCharset() == null) {
			// Simulate default charset
			//TODO : check why defaults values are not loaded during test 
			if (charset == null) {
				charset = "UTF-8";
			}
			setVariableValueToObject(vm, "charset", charset);
		}
		return vm;
	}

	private void assertNotSameContent(String resourceDir, String outputDir,
			String page) throws IOException {
		File fPage = new File(outputDir + "/" + page);
		File fExpectedPage = new File(resourceDir, "/expected/" + page);
		assertTrue("page " + page + " has not been generated", fPage.exists());
		String content = FileUtils.readFileToString(fPage).replaceAll("\r", "");
		String contentExpected = FileUtils.readFileToString(fExpectedPage)
				.replaceAll("\r", "");
		assertFalse(contentExpected.equals(content));
	}

	private void assertSameContent(String resourceDir, String outputDir,
			String page) throws IOException {

		File fPage = new File(outputDir + "/" + page);
		File fExpectedPage = new File(resourceDir, "/expected/" + page);

		assertTrue("page " + page + " has not been generated", fPage.exists());
		String content = FileUtils.readFileToString(fPage).replaceAll("\r", "")
				.replace("\n", "");
		;
		String contentExpected = FileUtils.readFileToString(fExpectedPage)
				.replaceAll("\r", "").replace("\n", "");
		assertEquals(contentExpected, content);

	}

	public void testIt1() throws Exception {

		String dir = "target/test-classes/it1";
		String outputDir = "target/it1/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir,
				"UTF-8");
		mojo.execute();
		assertSameContent(dir, outputDir, "page1.html");
		assertSameContent(dir, outputDir, "page2.html");
		assertSameContent(dir, outputDir, "page3.html");

		Collection<?> staticFile = FileUtils.listFiles(fileOutputDir,
				new SuffixFileFilter(".css"), FileFilterUtils.trueFileFilter());
		assertFalse("Ressources files were not copied", staticFile.isEmpty());
		assertEquals(2, staticFile.size());

	}

	public void testIt7() throws Exception {

		String dir = "target/test-classes/it7";
		String outputDir = "target/it7/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir, null);
		mojo.execute();
		assertNotSameContent(dir, outputDir, "page1.html");

		mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir, "ISO-8859-1");
		mojo.execute();
		assertSameContent(dir, outputDir, "page1.html");

	}

	public void testIt2() throws Exception {

		String dir = "target/test-classes/it2";
		String outputDir = "target/it2/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir,
				"UTF-8");
		try {
			mojo.execute();
			fail("Should throw an exception when a template is not found");
		} catch (MojoExecutionException e) {
			assertTrue("Should have thrown an HttpErrorPage",
					e.getCause() instanceof HttpErrorPage);
		}
	}

	public void testIt3() throws Exception {

		String dir = "target/test-classes/it3";
		String outputDir = "target/it3/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir,
				"UTF-8");
		try {
			mojo.execute();
			fail("Should throw an exception when pages directory is not found");
		} catch (MojoExecutionException e) {
			assertTrue("Exception should contain "
					+ "Directory pages not found",
					e.getMessage().startsWith("Directory pages not found"));
		}
	}

	public void testIt4() throws Exception {

		String dir = "target/test-classes/it4";
		String outputDir = "target/it4/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir,
				"UTF-8");

		try {
			mojo.execute();
			fail("Should throw an exception when modules directory is not found");
		} catch (MojoExecutionException e) {
			assertTrue("Exception should contain "
					+ "Directory modules not found",
					e.getMessage().startsWith("Directory modules not found"));
		}
	}

	public void testIt5() throws Exception {

		String dir = "target/test-classes/it5";
		String outputDir = "target/it5/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir,
				"UTF-8");

		mojo.execute();

	}

	public void testIt6() throws Exception {
		String dir = "target/test-classes/it6";
		String outputDir = "target/it9/generated-html";
		File fileOutputDir = new File(getBasedir() + "/" + outputDir);
		FileUtils.deleteDirectory(fileOutputDir);

		AssemblyMojo mojo = (AssemblyMojo) newAssemblyMojo(dir, outputDir, null);

		assertEquals("Plugin configuration is not loaded", "mycharset",
				mojo.getCharset());

	}

}