package net.sf.xslthl;

class StyledBlock extends Block {
	
	private String style;
	
	public StyledBlock(String text, String style) {
		super(text);
		this.style = style;
	}

	public String toString() {
		return String.format("<%s>%s</%s>", getStyle(), getText(), getStyle());
	}
	
	public String getStyle() {
		return style;
	}

	public boolean isStyled() {
		return true;
	}

}
