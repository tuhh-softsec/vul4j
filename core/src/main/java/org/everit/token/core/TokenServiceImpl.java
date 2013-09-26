package org.everit.token.core;

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
import java.util.UUID;

import javax.persistence.EntityManager;

import org.everit.token.api.TokenService;
import org.everit.token.api.dto.Token;
import org.everit.token.entity.TokenEntity;

/**
 * Implementation of {@link TokenService}.
 */
public class TokenServiceImpl implements TokenService {

    /**
     * EntityManager to hold data.
     */
    private EntityManager em;

    /**
     * Convert token entity object to token object.
     * 
     * @param tokenEntity
     *            the {@link TokenEntity} object.
     * @return the {@link Token} object. If token entity is null return <code>null</code>.
     */
    private Token convertTokenEntityToToken(final TokenEntity tokenEntity) {
        if (tokenEntity == null) {
            return null;
        }
        return new Token(tokenEntity.getTokenUuid(), tokenEntity.getCreationDate(), tokenEntity.getExpirationDate(),
                tokenEntity.getRevocationDate(),
                tokenEntity.getDateOfUse());
    }

    @Override
    public String createToken(final Date validityEndDate) {
        if (validityEndDate == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        Date creationDate = new Date();
        if (creationDate.getTime() > validityEndDate.getTime()) {
            throw new IllegalArgumentException("The creation date is older than validity end date.");
        }
        UUID uuid = UUID.randomUUID();
        TokenEntity tokenEntity = new TokenEntity(uuid.toString(), creationDate, validityEndDate, null, null);
        em.persist(tokenEntity);
        em.flush();
        return tokenEntity.getTokenUuid();
    }

    @Override
    public Token getToken(final String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        TokenEntity token = getTokenEntity(uuid);
        if (token != null) {
            Date actualDate = new Date();
            if ((actualDate.getTime() > token.getExpirationDate().getTime()) && revokeToken(token.getTokenUuid())) {
                token = getTokenEntity(uuid);
            }
        }
        return convertTokenEntityToToken(token);
    }

    /**
     * Getting the token entity object.
     * 
     * @param uuid
     *            the token UUID.
     * @return the {@link TokenEntity} object.
     */
    private TokenEntity getTokenEntity(final String uuid) {
        return em.find(TokenEntity.class, uuid);
    }

    @Override
    public boolean revokeToken(final String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        boolean revoke = false;
        TokenEntity tokenEntity = getTokenEntity(uuid);
        if ((tokenEntity != null) && (tokenEntity.getRevocationDate() == null)
                && (tokenEntity.getDateOfUse() == null)) {
            tokenEntity.setRevocationDate(new Date());
            em.merge(tokenEntity);
            em.flush();
            revoke = true;
        }
        return revoke;
    }

    public void setEm(final EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean verifyToken(final String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        boolean verify = false;
        TokenEntity tokenEntity = getTokenEntity(uuid);
        Date actualDate = new Date();
        if ((tokenEntity != null) && (actualDate.getTime() < tokenEntity.getExpirationDate().getTime())
                && (tokenEntity.getDateOfUse() == null) && (tokenEntity.getRevocationDate() == null)) {
            tokenEntity.setDateOfUse(actualDate);
            em.merge(tokenEntity);
            em.flush();
            verify = true;
        }
        return verify;
    }

}
