package org.esigate.tags;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.aggregator.AggregationSyntaxException;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class BlockElement implements Element {
    public static final ElementType TYPE = new ElementType() {

        @Override
        public boolean isStartTag(String tag) {
            return tag.startsWith("<!--$beginblock$");
        }

        @Override
        public boolean isEndTag(String tag) {
            return tag.startsWith("<!--$endblock$");
        }

        @Override
        public Element newInstance() {
            return new BlockElement();
        }

    };

    private BlockRenderer blockRenderer;
    private boolean nameMatches;

    @Override
    public boolean onError(Exception e, ParserContext ctx) {
        return false;
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) {
        // Stop writing
        if (nameMatches) {
            blockRenderer.setWrite(false);
        }
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String[] parameters = tag.split("\\$");
        if (parameters.length != 4) {
            throw new AggregationSyntaxException("Invalid syntax: " + tag);
        }
        String name = parameters[2];
        this.blockRenderer = ctx.findAncestor(BlockRenderer.class);
        // If name matches, start writing
        nameMatches = name.equals(blockRenderer.getName());
        if (nameMatches) {
            blockRenderer.setWrite(true);
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        blockRenderer.append(csq, start, end);
    }

}
