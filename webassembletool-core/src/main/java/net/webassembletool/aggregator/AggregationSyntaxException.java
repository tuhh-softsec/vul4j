package net.webassembletool.aggregator;


/**
 * Exception thrown when an HTML document contains WAT tags with invalid
 * arguments
 * 
 * @author Francois-Xavier Bonnet
 */
public class AggregationSyntaxException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** @param string Error message */
    public AggregationSyntaxException(String string) {
        super(string);
    }

}
