package org.esigate.aggregator;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class PutElement implements Element {
    public static final ElementType TYPE = new ElementType() {

        @Override
        public boolean isStartTag(String tag) {
            return tag.startsWith("<!--$beginput$");
        }

        @Override
        public boolean isEndTag(String tag) {
            return tag.startsWith("<!--$endput$");
        }

        @Override
        public Element newInstance() {
            return new PutElement();
        }

    };

    private IncludeTemplateElement includeTemplateElement;
    private StringBuilder body = new StringBuilder();
    private String name;

    @Override
    public boolean onError(Exception e, ParserContext ctx) {
        return false;
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) {
        includeTemplateElement.addParam(name, body.toString());
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String[] parameters = tag.split("\\$");
        if (parameters.length != 4) {
            throw new AggregationSyntaxException("Invalid syntax: " + tag);
        }
        name = parameters[2];
        includeTemplateElement = ctx.findAncestor(IncludeTemplateElement.class);
        if (includeTemplateElement == null) {
            throw new AggregationSyntaxException(tag + " should be nested in an includetemplate tag");
        }
    }

    // public void write(CharSequence content, int begin, int end, Writer out, Parser parser) {
    // body.append(content, begin, end);
    // }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        body.append(csq, start, end);
    }

}
