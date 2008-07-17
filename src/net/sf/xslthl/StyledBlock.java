/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

/**
 * A block with a style
 */
public class StyledBlock extends Block {
    /**
     * Do not add a style, just "block" it
     */
    public static final String NO_STYLE = "none";

    /**
     * Remove this part from the result
     */
    public static final String HIDDEN_STYLE = "hidden";

    /**
     * The style name
     */
    protected String style;

    public StyledBlock(String text, String style) {
	super(text);
	this.style = style;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Block#toString()
     */
    @Override
    public String toString() {
	if (StyledBlock.HIDDEN_STYLE.equalsIgnoreCase(style)) {
	    return "";
	}
	return String.format("<%s>%s</%s>", getStyle(), getText(), getStyle());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Block#getText()
     */
    @Override
    public String getText() {
	if (StyledBlock.HIDDEN_STYLE.equalsIgnoreCase(style)) {
	    return "";
	}

	return super.getText();
    }

    /**
     * @return the style name
     */
    public String getStyle() {
	return style;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Block#isStyled()
     */
    @Override
    public boolean isStyled() {
	return style != null && !StyledBlock.NO_STYLE.equalsIgnoreCase(style)
		&& !StyledBlock.HIDDEN_STYLE.equalsIgnoreCase(style);
    }

}
