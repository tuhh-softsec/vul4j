/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/rss/Attic/RSSDigester.java,v 1.1 2001/05/22 02:55:03 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/05/22 02:55:03 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.digester.rss;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * <p>Implementation of <strong>org.apache.commons.digester.Digester</strong>
 * designed to process input streams that conform to the <em>Rich Site
 * Summary</em> DTD, version 0.91.  For more information about this format,
 * see the <a href="http://my.netscape.com/publish/">My Netscape</a> site.</p>
 *
 * <p>The default implementation object returned by calling
 * <code>parse()</code> (an instance of
 * <code>org.apache.commons.digester.rss.Channel</code>)
 * knows how to render itself in XML format via the <code>render()</code>
 * method.  See the test <code>main()</code> method below for an
 * example of using these classes.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/05/22 02:55:03 $
 */

public class RSSDigester extends Digester {


    // ----------------------------------------------------------- Constructors



    // ----------------------------------------------------- Instance Variables


    /**
     * Have we been configured yet?
     */
    protected boolean configured = false;


    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };


    // ------------------------------------------------------------- Properties


    /**
     * The fully qualified class name of the <code>Channel</code>
     * implementation class.
     */
    protected String channelClass = "org.apache.commons.digester.rss.Channel";

    public String getChannelClass() {
        return (this.channelClass);
    }

    public void setChannelClass(String channelClass) {
        this.channelClass = channelClass;
    }


    /**
     * The fully qualified class name of the <code>Image</code>
     * implementation class.
     */
    protected String imageClass = "org.apache.commons.digester.rss.Image";

    public String getImageClass() {
        return (this.imageClass);
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }


    /**
     * The fully qualified class name of the <code>Item</code>
     * implementation class.
     */
    protected String itemClass = "org.apache.commons.digester.rss.Item";

    public String getItemClass() {
        return (this.itemClass);
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }


    /**
     * The fully qualified class name of the <code>TextInput</code>
     * implementation class.
     */
    protected String textInputClass =
        "org.apache.commons.digester.rss.TextInput";

    public String getTextInputClass() {
        return (this.textInputClass);
    }

    public void setTextInputClass(String textInputClass) {
        this.textInputClass = textInputClass;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Parse the content of the specified file using this Digester.  Returns
     * the root element from the object stack (which will be the Channel).
     *
     * @param file File containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(File file) throws IOException, SAXException {

        configure();
        return (super.parse(file));

    }


    /**
     * Parse the content of the specified input source using this Digester.
     * Returns the root element from the object stack (which will be the
     * Channel).
     *
     * @param input Input source containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputSource input) throws IOException, SAXException {

        configure();
        return (super.parse(input));

    }


    /**
     * Parse the content of the specified input stream using this Digester.
     * Returns the root element from the object stack (which will be
     * the Channel).
     *
     * @param input Input stream containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputStream input) throws IOException, SAXException {

        configure();
        return (super.parse(input));

    }


    /**
     * Parse the content of the specified URI using this Digester.
     * Returns the root element from the object stack (which will be
     * the Channel).
     *
     * @param uri URI containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(String uri) throws IOException, SAXException {

        configure();
        return (super.parse(uri));

    }


    // -------------------------------------------------------- Package Methods



    // ------------------------------------------------------ Protected Methods


    /**
     * Configure the parsing rules that will be used to process RSS input.
     */
    protected void configure() {

        if (configured)
            return;

        // Register local copies of the DTDs we understand
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i+1]);
            if (url != null)
                register(registrations[i], url.toString());
        }

        // FIXME - validate the "version" attribute of the rss element?

        // Add the rules for the Channel object
        addObjectCreate("rss/channel", channelClass);
        addCallMethod("rss/channel/copyright", "setCopyright", 0);
        addCallMethod("rss/channel/description", "setDescription", 0);
        addCallMethod("rss/channel/docs", "setDocs", 0);
        addCallMethod("rss/channel/language", "setLanguage", 0);
        addCallMethod("rss/channel/lastBuildDate", "setLastBuildDate", 0);
        addCallMethod("rss/channel/link", "setLink", 0);
        addCallMethod("rss/channel/managingEditor", "setManagingEditor", 0);
        addCallMethod("rss/channel/pubDate", "setPubDate", 0);
        addCallMethod("rss/channel/rating", "setRating", 0);
        addCallMethod("rss/channel/skipDays/day", "addSkipDay", 0);
        addCallMethod("rss/channel/skipHours/hour", "addSkipHour", 0);
        addCallMethod("rss/channel/title", "setTitle", 0);
        addCallMethod("rss/channel/webMaster", "setWebMaster", 0);

        // Add the rules for the Image object
        addObjectCreate("rss/channel/image", imageClass);
        addSetNext("rss/channel/image", "setImage",
                   "org.apache.commons.digester.rss.Image");
        addCallMethod("rss/channel/image/description", "setDescription", 0);
        addCallMethod("rss/channel/image/height", "setHeight", 0,
                      new Class[] { Integer.TYPE });
        addCallMethod("rss/channel/image/link", "setLink", 0);
        addCallMethod("rss/channel/image/title", "setTitle", 0);
        addCallMethod("rss/channel/image/url", "setURL", 0);
        addCallMethod("rss/channel/image/width", "setWidth", 0,
                      new Class[] { Integer.TYPE });

        // Add the rules for the Item object
        addObjectCreate("rss/channel/item", itemClass);
        addSetNext("rss/channel/item", "addItem",
                   "org.apache.commons.digester.rss.Item");
        addCallMethod("rss/channel/item/description", "setDescription", 0);
        addCallMethod("rss/channel/item/link", "setLink", 0);
        addCallMethod("rss/channel/item/title", "setTitle", 0);

        // Add the rules for the TextInput object
        addObjectCreate("rss/channel/textinput", textInputClass);
        addSetNext("rss/channel/textinput", "setTextInput",
                   "org.apache.commons.digester.rss.TextInput");
        addCallMethod("rss/channel/textinput/description",
                      "setDescription", 0);
        addCallMethod("rss/channel/textinput/link", "setLink", 0);
        addCallMethod("rss/channel/textinput/name", "setName", 0);
        addCallMethod("rss/channel/textinput/title", "setTitle", 0);

        // Mark this digester as having been configured
        configured = true;

    }


    // ------------------------------------------------------ Test Main Program


    /**
     * Test main program that parses the channel description included in this
     * package as a static resource.
     *
     * @param args The command line arguments (ignored)
     */
    public static void main(String args[]) {

        try {
            System.out.println("RSSDigester Test Program");
            System.out.println("Opening input stream ...");
            InputStream is = RSSDigester.class.getResourceAsStream
                ("/org/apache/commons/digester/rss/rss-example.xml");
            System.out.println("Creating new digester ...");
            RSSDigester digester = new RSSDigester();
            if ((args.length > 0) && (args[0].equals("-debug")))
                digester.setDebug(999);
            System.out.println("Parsing input stream ...");
            Channel channel = (Channel) digester.parse(is);
            System.out.println("Closing input stream ...");
            is.close();
            System.out.println("Dumping channel info ...");
            channel.render(System.out);
        } catch (Exception e) {
            System.out.println("-->Exception");
            e.printStackTrace(System.out);
        }

    }


 }
