package org.everit.token.api.dto;

import java.util.Date;

/**
 * The information of the token.
 */
public final class Token {

    /**
     * The token UUID.
     */
    private final String uuid;

    /**
     * The creation date of the token.
     */
    private final Date creationDate;

    /**
     * The expiration date of the token.
     */
    private final Date expirationDate;

    /**
     * The revocation date of the token.
     */
    private final Date revocationDate;

    /**
     * The date of the token when used.
     */
    private final Date dateOfUse;

    /**
     * The simple constructor.
     * 
     * @param uuid
     *            the token UUID.
     * @param creationDate
     *            the creation date.
     * @param expirationDate
     *            the expiration date.
     * @param revocationDate
     *            the revocation date.
     * @param dateOfUse
     *            the date of used the token.
     */
    public Token(final String uuid, final Date creationDate, final Date expirationDate, final Date revocationDate,
            final Date dateOfUse) {
        super();
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.revocationDate = revocationDate;
        this.dateOfUse = dateOfUse;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getDateOfUse() {
        return dateOfUse;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    public String getUuid() {
        return uuid;
    }

}
