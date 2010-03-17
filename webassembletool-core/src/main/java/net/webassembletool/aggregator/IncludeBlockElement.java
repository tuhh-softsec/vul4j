package net.webassembletool.aggregator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;
import net.webassembletool.tags.BlockRenderer;

public class IncludeBlockElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$includeblock$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endincludeblock$");
		}

		public Element newInstance() {
			return new IncludeBlockElement();
		}

	};

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {
                //Parsing sting <!--$includeblock$aggregated2$$(block)$myblock$-->
                //in order to retrieve includeblock, aggregated2, $(block), myblock
                Pattern pattern = Pattern.compile("(?<=\\$)(?:[^\\$]|\\$\\()*(?=\\$)");
		Matcher matcher = pattern.matcher(tag);
                List<String> listparameters = new ArrayList<String>();
		while (matcher.find()){
			listparameters.add(matcher.group());
		}
                
                String[] parameters = (String[])listparameters.toArray(new String[listparameters.size()]);
            
		Driver driver;
		if (parameters.length > 1)
			driver = DriverFactory.getInstance(parameters[1]);
		else
			driver = DriverFactory.getInstance();
		String page = "";
		if (parameters.length > 2)
			page = parameters[2];
		String name = null;
		if (parameters.length > 3)
			name = parameters[3];
		AggregateRenderer aggregateRenderer = stack.findAncestorWithClass(this,
				AggregateRenderer.class);
		driver.render(page, null, out, aggregateRenderer.getRequest(),
				aggregateRenderer.getResponse(), new BlockRenderer(name, page));
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(char c) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		// Just ignore tag body
		return this;
	}

}