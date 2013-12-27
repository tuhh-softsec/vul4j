package org.esigate.events.impl;

import java.nio.charset.Charset;

import org.esigate.events.Event;

/**
 * Encoding event : when a HTTP response is read as String.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ReadEntityEvent extends Event {
    /**
     * Response mime type.
     */
    private final String mimeType;

    /**
     * Declared or detected charset.
     * <p>
     * The charset can be modified by an extension.
     * <p>
     * Note : if charset is modified, entityContent should be updated too.
     */
    private final Charset charset;

    /**
     * The raw entity content, without any character set applied. It can be used to re-decode the entity content if the
     * default charset was incorrect.
     */
    private final byte[] rawEntityContent;
    /**
     * The current, decoded entity content.
     * <p>
     * An extension can update this content if is incorrect.
     * <p>
     * Note : if entityContent is modified, charset should be updated too.
     */
    private String entityContent;

    public ReadEntityEvent(String mimeType, Charset charset, byte[] rawEntityContent) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.rawEntityContent = rawEntityContent;
    }

    public String getEntityContent() {
        return entityContent;
    }

    public void setEntityContent(String entityContent) {
        this.entityContent = entityContent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Charset getCharset() {
        return charset;
    }

    public byte[] getRawEntityContent() {
        return rawEntityContent;
    }
}
