package de.intevation.lada.importer.laf;

/**
 * Exception thrown in the LAF import process.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LafParserException
extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LafParserException(String message) {
        super(message);
    }
}
