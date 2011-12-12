package org.esigate.aggregator;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;

public class AggregateRendererTest extends TestCase {
	private AggregateRenderer tested;
	private ResourceContext ctx;

	@Override
	protected void setUp() throws Exception {
		MockDriver provider = new MockDriver("mock");
		provider.addResource("/testInclude",
				"Test include");
		provider.addResource("/testBlock",
				"before <!--$beginblock$myblock$-->some text goes here<!--$endblock$myblock$--> after");
		provider.addResource("/testTemplate",
				"before <!--$begintemplate$mytemplate$-->some text goes here<!--$endtemplate$mytemplate$--> after");
		provider.addResource("/testTemplateParams",
				"before <!--$begintemplate$mytemplate$-->some text <!--$beginparam$param1$-->To be replaced<!--$endparam$param1$--> goes here<!--$endtemplate$mytemplate$--> after");
		provider.addResource("",
				"before <!--$beginblock$myblock$-->some text goes here<!--$endblock$myblock$--> after");
		provider.addResource("/testNested",
				"before <!--$beginblock$myblock$--> nested <!--$includeblock$mock$/testInclude$--> some text <!--$endincludeblock$--> /nested <!--$endblock$myblock$--> after");
		provider.addResource("/testNestedTemplate",
				"before <!--$begintemplate$myblock$--> nested <!--$includeblock$mock$/testInclude$--> some text <!--$endincludeblock$--> /nested <!--$endtemplate$myblock$--> after");

		ctx = new ResourceContext(provider, null, null, null, null);
		tested = new AggregateRenderer();
	}

	public void testIncludeBlockNoBlockName() throws IOException, HttpErrorPage {
		String page = "content <!--$includeblock$mock$/testInclude$--> some text <!--$endincludeblock$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content Test include end", out.toString());
	}

	public void testIncludeBlock() throws IOException, HttpErrorPage {
		String page = "content <!--$includeblock$mock$/testBlock$myblock$--> some text <!--$endincludeblock$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text goes here end", out.toString());

		page = "content <!--$includeblock$mock$$(vartestBlock)$myblock$--> some text <!--$endincludeblock$--> end";
		out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text goes here end", out.toString());

	}

	public void testIncludeBlockNested() throws IOException, HttpErrorPage {
		String page = "content <!--$includeblock$mock$/testNested$myblock$--> some text <!--$endincludeblock$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content  nested Test include /nested  end", out
				.toString());
	}

	public void testIncludeTemplateNested() throws IOException, HttpErrorPage {
		String page = "content <!--$includetemplate$mock$/testNestedTemplate$myblock$--> some text <!--$endincludetemplate$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content  nested Test include /nested  end", out
				.toString());
	}

	public void testIncludeBlockRoot() throws IOException, HttpErrorPage {
		String page = "content <!--$includeblock$mock$$myblock$--> some text <!--$endincludeblock$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text goes here end", out.toString());
	}

	public void testIncludeTemplateNoTemplateName() throws IOException,
			HttpErrorPage {
		String page = "content <!--$includetemplate$mock$/testInclude$--> some text <!--$endincludetemplate$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content Test include end", out.toString());
	}

	public void testIncludeTemplate() throws IOException, HttpErrorPage {
		String page = "content <!--$includetemplate$mock$/testTemplateParams$mytemplate$--> some text <!--$beginput$param1$-->Replacement<!--$endput$-->some other text<!--$endincludetemplate$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text Replacement goes here end", out
				.toString());
	}

	public void testIncludeTemplateWithVariables() throws IOException,
			HttpErrorPage {
		String page = "content <!--$includetemplate$mock$$(varTestTemplateParams)$mytemplate$--> some text <!--$beginput$param1$-->Replacement<!--$endput$-->some other text<!--$endincludetemplate$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text Replacement goes here end", out
				.toString());

		page = "content <!--$includetemplate$mock$/test$(varTemplate)Params$mytemplate$--> some text <!--$beginput$param1$-->Replacement<!--$endput$-->some other text<!--$endincludetemplate$--> end";
		out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text Replacement goes here end", out
				.toString());

		page = "content <!--$includetemplate$mock$/test$(varTemplate)$(varParams)$mytemplate$--> some text <!--$beginput$param1$-->Replacement<!--$endput$-->some other text<!--$endincludetemplate$--> end";
		out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text Replacement goes here end", out
				.toString());

	}

	public void testNestedTags() throws IOException, HttpErrorPage {
		String page = "content <!--$includetemplate$mock$/testTemplateParams$mytemplate$--> some text <!--$beginput$param1$-->aaa <!--$includeblock$mock$/testInclude$--> some text <!--$endincludeblock$--> bbb<!--$endput$-->some other text<!--$endincludetemplate$--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("content some text aaa Test include bbb goes here end",
				out.toString());

	}

}
