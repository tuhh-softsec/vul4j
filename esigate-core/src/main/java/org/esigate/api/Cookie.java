package org.esigate.api;

import java.util.Date;

/**
 * Cookie interface represents a token or short packet of state information
 * (also referred to as "magic-cookie") that the HTTP agent and the target
 * server can exchange to maintain a session. In its simples form an HTTP
 * cookie is merely a name / value pair.
 *
 * @since 4.0
 */
public interface Cookie {
	
	 // RFC2109 attributes
    public static final String VERSION_ATTR    = "version";
    public static final String PATH_ATTR       = "path";
    public static final String DOMAIN_ATTR     = "domain";
    public static final String MAX_AGE_ATTR    = "max-age";
    public static final String SECURE_ATTR     = "secure";
    public static final String COMMENT_ATTR    = "comment";
    public static final String EXPIRES_ATTR    = "expires";

    // RFC2965 attributes
    public static final String PORT_ATTR       = "port";
    public static final String COMMENTURL_ATTR = "commenturl";
    public static final String DISCARD_ATTR    = "discard";

    String getAttribute(String name);

    boolean containsAttribute(String name);
    /**
     * Returns the name.
     *
     * @return String name The name
     */
    String getName();

    /**
     * Returns the value.
     *
     * @return String value The current value.
     */
    String getValue();

    /**
     * Returns the comment describing the purpose of this cookie, or
     * <tt>null</tt> if no such comment has been defined.
     *
     * @return comment
     */
    String getComment();

    /**
     * If a user agent (web browser) presents this cookie to a user, the
     * cookie's purpose will be described by the information at this URL.
     */
    String getCommentURL();

    /**
     * Returns the expiration {@link Date} of the cookie, or <tt>null</tt>
     * if none exists.
     * <p><strong>Note:</strong> the object returned by this method is
     * considered immutable. Changing it (e.g. using setTime()) could result
     * in undefined behaviour. Do so at your peril. </p>
     * @return Expiration {@link Date}, or <tt>null</tt>.
     */
    Date getExpiryDate();

    /**
     * Returns <tt>false</tt> if the cookie should be discarded at the end
     * of the "session"; <tt>true</tt> otherwise.
     *
     * @return <tt>false</tt> if the cookie should be discarded at the end
     *         of the "session"; <tt>true</tt> otherwise
     */
    boolean isPersistent();

    /**
     * Returns domain attribute of the cookie. The value of the Domain
     * attribute specifies the domain for which the cookie is valid.
     *
     * @return the value of the domain attribute.
     */
    String getDomain();

    /**
     * Returns the path attribute of the cookie. The value of the Path
     * attribute specifies the subset of URLs on the origin server to which
     * this cookie applies.
     *
     * @return The value of the path attribute.
     */
    String getPath();

    /**
     * Get the Port attribute. It restricts the ports to which a cookie
     * may be returned in a Cookie request header.
     */
    int[] getPorts();

    /**
     * Indicates whether this cookie requires a secure connection.
     *
     * @return  <code>true</code> if this cookie should only be sent
     *          over secure connections, <code>false</code> otherwise.
     */
    boolean isSecure();

    /**
     * Returns the version of the cookie specification to which this
     * cookie conforms.
     *
     * @return the version of the cookie.
     */
    int getVersion();

    /**
     * Returns true if this cookie has expired.
     * @param date Current time
     *
     * @return <tt>true</tt> if the cookie has expired.
     */
    boolean isExpired(final Date date);
    
    /**
     * If a user agent (web browser) presents this cookie to a user, the
     * cookie's purpose will be described by the information at this URL.
     */
    void setCommentURL(String commentURL);

    /**
     * Sets the Port attribute. It restricts the ports to which a cookie
     * may be returned in a Cookie request header.
     */
    void setPorts(int[] ports);

    /**
     * Set the Discard attribute.
     *
     * Note: <tt>Discard</tt> attribute overrides <tt>Max-age</tt>.
     *
     * @see #isPersistent()
     */
    void setDiscard(boolean discard);
    
    void setValue(String value);

    /**
     * If a user agent (web browser) presents this cookie to a user, the
     * cookie's purpose will be described using this comment.
     *
     * @param comment
     *
     * @see #getComment()
     */
    void setComment(String comment);

    /**
     * Sets expiration date.
     * <p><strong>Note:</strong> the object returned by this method is considered
     * immutable. Changing it (e.g. using setTime()) could result in undefined
     * behaviour. Do so at your peril.</p>
     *
     * @param expiryDate the {@link Date} after which this cookie is no longer valid.
     *
     * @see Cookie#getExpiryDate
     *
     */
    void setExpiryDate (Date expiryDate);

    /**
     * Sets the domain attribute.
     *
     * @param domain The value of the domain attribute
     *
     * @see Cookie#getDomain
     */
    void setDomain(String domain);

    /**
     * Sets the path attribute.
     *
     * @param path The value of the path attribute
     *
     * @see Cookie#getPath
     *
     */
    void setPath(String path);

    /**
     * Sets the secure attribute of the cookie.
     * <p>
     * When <tt>true</tt> the cookie should only be sent
     * using a secure protocol (https).  This should only be set when
     * the cookie's originating server used a secure protocol to set the
     * cookie's value.
     *
     * @param secure The value of the secure attribute
     *
     * @see #isSecure()
     */
    void setSecure (boolean secure);

    /**
     * Sets the version of the cookie specification to which this
     * cookie conforms.
     *
     * @param version the version of the cookie.
     *
     * @see Cookie#getVersion
     */
    void setVersion(int version);

}
