package org.esigate.tags;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.aggregator.AggregationSyntaxException;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class TemplateElement implements Element {
    private TemplateRenderer templateRenderer;
    private boolean nameMatches;
    public static final ElementType TYPE = new ElementType() {

        @Override
        public boolean isStartTag(String tag) {
            return tag.startsWith("<!--$begintemplate$");
        }

        @Override
        public boolean isEndTag(String tag) {
            return tag.startsWith("<!--$endtemplate$");
        }

        @Override
        public Element newInstance() {
            return new TemplateElement();
        }

    };

    @Override
    public boolean onError(Exception e, ParserContext ctx) {
        return false; // do not handle errors
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        // Stop writing
        if (nameMatches) {
            templateRenderer.setWrite(false);
        }
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String[] parameters = tag.split("\\$");
        if (parameters.length != 4) {
            throw new AggregationSyntaxException("Invalid syntax: " + tag);
        }
        String name = parameters[2];
        this.templateRenderer = ctx.findAncestor(TemplateRenderer.class);
        // If name matches, start writing
        nameMatches = name.equals(templateRenderer.getName());
        if (nameMatches) {
            templateRenderer.setWrite(true);
        }
    }

    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        if (nameMatches) {
            templateRenderer.append(csq, start, end);
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    public boolean isNameMatches() {
        return nameMatches;
    }

}
