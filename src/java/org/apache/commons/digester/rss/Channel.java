/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/rss/Attic/Channel.java,v 1.3 2002/01/09 20:22:50 sanders Exp $
 * $Revision: 1.3 $
 * $Date: 2002/01/09 20:22:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;


/**
 * <p>Implementation object representing a <strong>channel</strong> in the
 * <em>Rich Site Summary</em> DTD, version 0.91.  This class may be subclassed
 * to further specialize its behavior.</p>
 *
 * @author Craig R. McClanahan
 * @author Ted Husted
 * @version $Revision: 1.3 $ $Date: 2002/01/09 20:22:50 $
 */

public class Channel {


    // ----------------------------------------------------- Instance Variables


    /**
     * The set of items associated with this Channel.
     */
    protected ArrayList items = new ArrayList();


    /**
     * The set of skip days for this channel.
     */
    protected ArrayList skipDays = new ArrayList();


    /**
     * The set of skip hours for this channel.
     */
    protected ArrayList skipHours = new ArrayList();


    // ------------------------------------------------------------- Properties


    /**
     * The channel copyright (1-100 characters).
     */
    protected String copyright = null;

    public String getCopyright() {
        return (this.copyright);
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }


    /**
     * The channel description (1-500 characters).
     */
    protected String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * The channel description file URL (1-500 characters).
     */
    protected String docs = null;

    public String getDocs() {
        return (this.docs);
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }


    /**
     * The image describing this channel.
     */
    protected Image image = null;

    public Image getImage() {
        return (this.image);
    }

    public void setImage(Image image) {
        this.image = image;
    }


    /**
     * The channel language (2-5 characters).
     */
    protected String language = null;

    public String getLanguage() {
        return (this.language);
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * The channel last build date (1-100 characters).
     */
    protected String lastBuildDate = null;

    public String getLastBuildDate() {
        return (this.lastBuildDate);
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }


    /**
     * The channel link (1-500 characters).
     */
    protected String link = null;

    public String getLink() {
        return (this.link);
    }

    public void setLink(String link) {
        this.link = link;
    }


    /**
     * The managing editor (1-100 characters).
     */
    protected String managingEditor = null;

    public String getManagingEditor() {
        return (this.managingEditor);
    }

    public void setManagingEditor(String managingEditor) {
        this.managingEditor = managingEditor;
    }


    /**
     * The channel publication date (1-100 characters).
     */
    protected String pubDate = null;

    public String getPubDate() {
        return (this.pubDate);
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    /**
     * The channel rating (20-500 characters).
     */
    protected String rating = null;

    public String getRating() {
        return (this.rating);
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    /**
     * The text input description for this channel.
     */
    protected TextInput textInput = null;

    public TextInput getTextInput() {
        return (this.textInput);
    }

    public void setTextInput(TextInput textInput) {
        this.textInput = textInput;
    }


    /**
     * The channel title (1-100 characters).
     */
    protected String title = null;

    public String getTitle() {
        return (this.title);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * The RSS specification version number used to create this Channel.
     */
    protected double version = 0.91;

    public double getVersion() {
        return (this.version);
    }

    public void setVersion(double version) {
        this.version = version;
    }


    /**
     * The webmaster email address (1-100 characters).
     */
    protected String webMaster = null;

    public String getWebMaster() {
        return (this.webMaster);
    }

    public void setWebMaster(String webMaster) {
        this.webMaster = webMaster;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add an additional item.
     *
     * @param item The item to be added
     */
    public void addItem(Item item) {
        synchronized (items) {
            items.add(item);
        }
    }


    /**
     * Add an additional skip day name.
     *
     * @param skipDay The skip day to be added
     */
    public void addSkipDay(String skipDay) {
        synchronized (skipDays) {
            skipDays.add(skipDay);
        }
    }


    /**
     * Add an additional skip day name.
     *
     * @param skipDay The skip day to be added
     */
    public void addSkipHour(String skipHour) {
        synchronized (skipHours) {
            skipHours.add(skipHour);
        }
    }


    /**
     * Return the items for this channel.
     */
    public Item[] findItems() {
        synchronized (items) {
            Item items[] = new Item[this.items.size()];
            return ((Item[]) this.items.toArray(items));
        }
    }


    /**
     * Return the items for this channel.
     */
    public Item[] getItems() {
        return findItems();
    }


    /**
     * Return the skip days for this channel.
     */
    public String[] findSkipDays() {
        synchronized (skipDays) {
            String skipDays[] = new String[this.skipDays.size()];
            return ((String[]) this.skipDays.toArray(skipDays));
        }
    }


    /**
     * Return the skip hours for this channel.
     */
    public String[] getSkipHours() {
        return findSkipHours();
    }


    /**
     * Return the skip hours for this channel.
     */
    public String[] findSkipHours() {
        synchronized (skipHours) {
            String skipHours[] = new String[this.skipHours.size()];
            return ((String[]) this.skipHours.toArray(skipHours));
        }
    }


    /**
     * Return the skip days for this channel.
     */
    public String[] getSkipDays() {
        return findSkipDays();
    }


    /**
     * Remove an item for this channel.
     *
     * @param item The item to be removed
     */
    public void removeItem(Item item) {
        synchronized (items) {
            items.remove(item);
        }
    }


    /**
     * Remove a skip day for this channel.
     *
     * @param skipDay The skip day to be removed
     */
    public void removeSkipDay(String skipDay) {
        synchronized (skipDays) {
            skipDays.remove(skipDay);
        }
    }


    /**
     * Remove a skip hour for this channel.
     *
     * @param skipHour The skip hour to be removed
     */
    public void removeSkipHour(String skipHour) {
        synchronized (skipHours) {
            skipHours.remove(skipHour);
        }
    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified output stream, with no indication of character
     * encoding.
     *
     * @param stream The output stream to write to
     */
    public void render(OutputStream stream) {

        try {
            render(stream, null);
        } catch (UnsupportedEncodingException e) {
            ; // Can not happen
        }

    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified output stream, with the specified character encoding.
     *
     * @param stream The output stream to write to
     * @param encoding The character encoding to declare, or <code>null</code>
     *  for no declaration
     *
     * @exception UnsupportedEncodingException if the named encoding
     *  is not supported
     */
    public void render(OutputStream stream, String encoding)
            throws UnsupportedEncodingException {

        PrintWriter pw = null;
        if (encoding == null) {
            pw = new PrintWriter(stream);
        } else {
            pw = new PrintWriter(new OutputStreamWriter(stream, encoding));
        }
        render(pw, encoding);
        pw.flush();

    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified writer, with no indication of character encoding.
     *
     * @param writer The writer to render output to
     */
    public void render(Writer writer) {

        render(writer, null);

    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified writer, indicating the specified character encoding.
     *
     * @param writer The writer to render output to
     * @param encoding The character encoding to declare, or <code>null</code>
     *  for no declaration
     */
    public void render(Writer writer, String encoding) {

        PrintWriter pw = new PrintWriter(writer);
        render(pw, encoding);
        pw.flush();

    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified writer, with no indication of character encoding.
     *
     * @param writer The writer to render output to
     */
    public void render(PrintWriter writer) {

        render(writer, null);

    }


    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified writer, indicating the specified character encoding.
     *
     * @param writer The writer to render output to
     * @param encoding The character encoding to declare, or <code>null</code>
     *  for no declaration
     */
    public void render(PrintWriter writer, String encoding) {

        writer.print("<?xml version=\"1.0\"");
        if (encoding != null) {
            writer.print(" encoding=\"");
            writer.print(encoding);
            writer.print("\"");
        }
        writer.println("?>");
        writer.println();

        writer.println("<!DOCTYPE rss PUBLIC");
        writer.println("  \"-//Netscape Communications//DTD RSS 0.91//EN\"");
        writer.println("  \"http://my.netscape.com/publish/formats/rss-0.91.dtd\">");
        writer.println();

        writer.println("<rss version=\"0.91\">");
        writer.println();

        writer.println("  <channel>");
        writer.println();

        writer.print("    <title>");
        writer.print(title);
        writer.println("</title>");

        writer.print("    <description>");
        writer.print(description);
        writer.println("</description>");

        writer.print("    <link>");
        writer.print(link);
        writer.println("</link>");

        writer.print("    <language>");
        writer.print(language);
        writer.println("</language>");

        if (rating != null) {
            writer.print("    <rating>");
            writer.print(rating);
            writer.println("</rating>");
        }

        if (copyright != null) {
            writer.print("    <copyright>");
            writer.print(copyright);
            writer.print("</copyright>");
        }


        if (pubDate != null) {
            writer.print("    <pubDate>");
            writer.print(pubDate);
            writer.println("</pubDate>");
        }

        if (lastBuildDate != null) {
            writer.print("    <lastBuildDate>");
            writer.print(lastBuildDate);
            writer.println("</lastBuildDate>");
        }

        if (docs != null) {
            writer.print("    <docs>");
            writer.print(docs);
            writer.println("</docs>");
        }

        if (managingEditor != null) {
            writer.print("    <managingEditor>");
            writer.print(managingEditor);
            writer.println("</managingEditor>");
        }

        if (webMaster != null) {
            writer.print("    <webMaster>");
            writer.print(webMaster);
            writer.println("</webMaster>");
        }

        writer.println();

        if (image != null) {
            image.render(writer);
            writer.println();
        }

        if (textInput != null) {
            textInput.render(writer);
            writer.println();
        }

        String skipDays[] = findSkipDays();
        if (skipDays.length > 0) {
            writer.println("    <skipDays>");
            for (int i = 0; i < skipDays.length; i++) {
                writer.print("      <skipDay>");
                writer.print(skipDays[i]);
                writer.println("</skipDay>");
            }
            writer.println("    </skipDays>");
        }

        String skipHours[] = findSkipHours();
        if (skipHours.length > 0) {
            writer.println("    <skipHours>");
            for (int i = 0; i < skipHours.length; i++) {
                writer.print("      <skipHour>");
                writer.print(skipHours[i]);
                writer.println("</skipHour>");
            }
            writer.println("    </skipHours>");
            writer.println();
        }

        Item items[] = findItems();
        for (int i = 0; i < items.length; i++) {
            items[i].render(writer);
            writer.println();
        }

        writer.println("  </channel>");
        writer.println();

        writer.println("</rss>");

    }


}
