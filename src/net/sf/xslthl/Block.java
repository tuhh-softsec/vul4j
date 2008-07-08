package net.sf.xslthl;

class Block {

    private String text;

    public Block(String text) {
	this.text = text;
    }

    public String getText() {
	return text;
    }

    @Override
    public String toString() {
	return getText();
    }

    public boolean isStyled() {
	return false;
    }

    public boolean empty() {
	if (text == null || text.length() == 0) {
	    return true;
	}
	return false;
    }

}
