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
        if (creationDate.after(validityEndDate)) {
            // throw new exception (which expection?)
        }
        UUID uuid = UUID.randomUUID();
        while (!existUuuid(uuid.toString())) {
            uuid = UUID.randomUUID();
        }
        TokenEntity tokenEntity = new TokenEntity(uuid.toString(), creationDate, validityEndDate, null, null);
        em.persist(tokenEntity);
        em.flush();
        return tokenEntity.getTokenUuid();
    }

    /**
     * Checking the token UUID is exist.
     * 
     * @param uuid
     *            the token UUID.
     * @return <code>true</code> if exist the token UUID, otherwise false.
     */
    private boolean existUuuid(final String uuid) {
        boolean exist = false;
        TokenEntity token = getTokenEntity(uuid);
        if (token != null) {
            exist = true;
        }
        return exist;
    }

    @Override
    public Token getToken(final String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        TokenEntity token = getTokenEntity(uuid);
        if (token != null) {
            Date actualDate = new Date();
            if (actualDate.after(token.getExpirationDate())) {
                if (revokeToken(token.getTokenUuid())) {
                    token = getTokenEntity(uuid);
                }
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
        TokenEntity tokenEntity = getTokenEntity(uuid);
        tokenEntity.setRevocationDate(new Date());
        em.refresh(tokenEntity);
        em.flush();
        return true;
    }

    @Override
    public boolean verifyToken(final String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Cannot be null the parameters.");
        }
        boolean verify = false;
        TokenEntity tokenEntity = getTokenEntity(uuid);
        Date actualDate = new Date();
        if ((tokenEntity != null) && actualDate.before(tokenEntity.getExpirationDate())
                && (tokenEntity.getDateOfUse() == null) && (tokenEntity.getRevocationDate() == null)) {
            tokenEntity.setDateOfUse(actualDate);
            em.refresh(tokenEntity);
            em.flush();
            verify = true;
        }
        return verify;
    }

}
