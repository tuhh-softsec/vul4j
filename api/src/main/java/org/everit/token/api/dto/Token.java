package org.everit.token.api.dto;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

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
