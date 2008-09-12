package net.webassembletool.ouput;

/**
 * An Output is designed to collect the response to a successfull HTTP request,
 * typically an HTML page or any other file type with all the headers sent by
 * the server.<br />
 * 
 * Output implementations may handle the data as needed : write it to an
 * HttpServletResponse, save it to a File or a database for example.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public interface Output {
    /**
     * Adds an HTTP Header
     * 
     * @param name The name of the HTTP header
     * @param value The value of the header
     */
    public void addHeader(String name, String value);

    /**
     * Sets the HTTP status code of the response
     * 
     * @param code The code
     * @param message The message
     */
    public void setStatus(int code, String message);

    /**
     * Defines the charset of the Output. <br /> Needed for text outputs (for
     * example HTML pages).
     * 
     * @param charset The name of the charset
     * 
     */
    public void setCharset(String charset);

    /**
     * Opens the OutputStreams that may be needed by the OutPut.<br /> The
     * headers and charset may be ignored if not defined before calling this
     * method.<br /> Any opened Output should be closed in order to release the
     * resources.
     */
    public void open();

    /**
     * Writes some bytes to the Output.<br /> The Output must be opened before
     * calling this method.<br /> This method cannot be called any more when the
     * Output has been closed.
     * 
     * @param bytes Array containing the bytes to be written
     * @param offset First byte to be written
     * @param length Number of bytes to be written
     * @throws OutputException If an problem occurs while writing the result
     */
    public void write(byte[] bytes, int offset, int length)
	    throws OutputException;

    /**
     * Closes the OutputStreams that may have been used by the OutPut.<br /> Any
     * opened Output should be closed in order to release the resources.
     */
    public void close();
}
