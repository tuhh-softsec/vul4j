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
import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import net.webassembletool.Driver;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.StaticDriver;
import net.webassembletool.aggregator.AggregateRenderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generate a set of HTML pages with reusable components found in
 * modulesDirectory folder
 * 
 * @goal assembly
 * @phase generate-resources
 * @execute phase="process-resources"
 * 
 * @author Alexis Thaveau
 */
public class AssemblyMojo extends AbstractMojo {

	/**
	 * Filter html file
	 */
	private static IOFileFilter PAGES_TO_GENERATE_FILTER = new SuffixFileFilter(
			".html");
	/**
	 * The directory containing pages to generate.
	 * 
	 * @parameter expression="${project.build.outputDirectory}/pages"
	 * @required
	 */
	private File pagesDirectory;

	/**
	 * The directory containing template and block.
	 * 
	 * @parameter expression="${project.build.outputDirectory}/modules"
	 * @required
	 */
	private File modulesDirectory;

	/**
	 * The output directory for assembly result.
	 * 
	 * @parameter expression="${project.build.directory}/generated-html"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Pages and modules charset to use.
	 * 
	 * @parameter expression="${charset}" default-value="UTF-8"
	 */
	private String charset;

	/* package */String getCharset() {
		return charset;
	}

	/**
	 * 
	 */
	public void execute() throws MojoExecutionException {

		Properties prop = new Properties();

		prop.put("modules.localBase", outputDirectory.getAbsolutePath()
				+ "/modules");
		prop.put("modules.uriEncoding", charset);

		StaticDriver driver = new StaticDriver("modules", prop);

		try {
			checkStructure();
			init(driver);
			assemblePages(driver);
			copyStaticResources();
		} catch (HttpErrorPage e) {
			throw new MojoExecutionException("Error", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Error", e);
		} finally {
		}
	}

	/**
	 * Check directory structure
	 * 
	 * @throws MojoFailureException
	 */
	private void checkStructure() throws MojoExecutionException {
		if (!this.modulesDirectory.exists()) {
			throw new MojoExecutionException("Directory modules not found ["
					+ modulesDirectory.getAbsolutePath() + "]");
		} else if (!this.pagesDirectory.exists()) {
			throw new MojoExecutionException("Directory pages not found ["
					+ pagesDirectory.getAbsolutePath() + "]");
		}

	}

	/**
	 * Init driver
	 * 
	 * @param driver
	 * @throws IOException
	 */
	private void init(StaticDriver driver) throws IOException {
		getLog().info(
				"Initialize driver with resources in folder "
						+ modulesDirectory.getPath());

		@SuppressWarnings("rawtypes")
		Collection files = FileUtils.listFiles(this.modulesDirectory,
				PAGES_TO_GENERATE_FILTER, FileFilterUtils.trueFileFilter());
		for (Object file : files) {
			File source = (File) file;
			String fileName = getRelativePath(modulesDirectory, source);
			// String filename = (String) ofilename;
			String content = FileUtils.readFileToString(source);
			driver.addResource(fileName, content, charset);
			getLog().info("Add resource " + fileName);

		}
	}

	/**
	 * Copie les resources du repertoire static dans generated-html
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	private void copyStaticResources() throws IOException {

		getLog().info(
				"Copy static resources from " + pagesDirectory.getPath()
						+ " to " + outputDirectory.getPath());

		FileUtils.copyDirectory(pagesDirectory, outputDirectory,
				new NotFileFilter(PAGES_TO_GENERATE_FILTER), true);

	}

	/**
	 * Process pages
	 * 
	 * @param driver
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	private void assemblePages(Driver driver) throws IOException, HttpErrorPage {
		getLog().info("Assemble pages");
		// Find all html page to render
		@SuppressWarnings("rawtypes")
		Collection files = FileUtils.listFiles(pagesDirectory,
				PAGES_TO_GENERATE_FILTER, FileFilterUtils.trueFileFilter());

		AggregateRenderer renderer = new AggregateRenderer(null, null);

		for (Object ofilename : files) {
			File filePage = (File) ofilename;

			String page = getRelativePath(pagesDirectory, filePage);
			String content = FileUtils.readFileToString(filePage);
			ResourceContext resourceContext = new ResourceContext(driver, page,
					null, null, null);

			StringWriter stringWriter = new StringWriter();
			renderer.render(resourceContext, content, stringWriter);
			String result = stringWriter.toString().replaceAll("<!--#\\$",
					"<!--\\$");

			File file = new File(this.outputDirectory + "/" + page);
			FileUtils.writeStringToFile(file, result);

		}

	}

	/**
	 * Return relative path
	 * 
	 * @param directory
	 * @param file
	 * @return
	 */
	private String getRelativePath(File directory, File file) {
		return directory.toURI().relativize(file.toURI()).getPath();
	}

}
