package net.webassembletool.aggregator;

/**
 * Exception thrown when an HTML document contains WAT tags with invalid
 * arguments
 * 
 * @author François-Xavier Bonnet
 */
public class AggregationSyntaxException extends Exception {

    /**
     * @param string Error message
     */
    public AggregationSyntaxException(String string) {
	super(string);
    }

    private static final long serialVersionUID = 1L;

}
