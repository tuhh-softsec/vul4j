package org.everit.token.entity;

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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The entity of the token.
 */
@Entity
@Table(name = "TOKEN")
public class TokenEntity {

    /**
     * The token UUID.
     */
    @Id
    @Column(name = "TOKEN_UUID")
    private String tokenUuid;

    /**
     * The creation date of the token.
     */
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * The expiration date of the token. The token has been used to this date, if the revocation date or date of use
     * field is not filled out.
     */
    @Column(name = "EXPERATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    /**
     * The revocation date of the token. If not used the token the field is filled in.
     */
    @Column(name = "RECOVATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revocationDate;

    /**
     * The date of the token when used.
     */
    @Column(name = "DATE_OF_USE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfUse;

    /**
     * The default constructor.
     */
    public TokenEntity() {
    }

    /**
     * The constructor when used all fields.
     * 
     * @param tokenUuid
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
    public TokenEntity(final String tokenUuid, final Date creationDate, final Date expirationDate,
            final Date revocationDate, final Date dateOfUse) {
        super();
        this.tokenUuid = tokenUuid;
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

    public String getTokenUuid() {
        return tokenUuid;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDateOfUse(final Date dateOfUse) {
        this.dateOfUse = dateOfUse;
    }

    public void setExperationDate(final Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setRevocationDate(final Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public void setTokenUuid(final String tokenUuid) {
        this.tokenUuid = tokenUuid;
    }
}
