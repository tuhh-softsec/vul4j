package org.everit.token.api;

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

import org.everit.token.api.dto.Token;

/**
 * Service for managing the token function.
 */
public interface TokenService {

    /**
     * Create a new token.
     * 
     * @param validityEndDate
     *            the validity date of the token. Cannot be <code>null</code>.
     * @return the token UUID.
     * 
     * @throws IllegalArgumentException
     *             if the parameter is <code>null</code> or the validityEndDate is smaller than actual date.
     */
    String createToken(final Date validityEndDate);

    /**
     * Getting the token information.
     * 
     * @param uuid
     *            the token UUID. Cannot be <code>null</code>.
     * @return the {@link Token} object. If not exist token return <code>null</code>.
     */
    Token getToken(final String uuid);

    /**
     * The token will be withdrawn without use.
     * 
     * @param uuid
     *            the token UUID which withdrawn. Cannot be <code>null</code>.
     * @return <code>true</code> if revoke the token, otherwise <code>false</code>.
     * 
     * @throws IllegalArgumentException
     *             if the parameter is <code>null</code>.
     */
    boolean revokeToken(final String uuid);

    /**
     * The token verification.
     * 
     * @param uuid
     *            the token UUID which verification. Cannot be <code>null</code>.
     * @return <code>true</code> if valid the token UUID otherwise <code>false</code>.
     * 
     * @throws IllegalArgumentException
     *             if the parameter is <code>null</code>.
     */
    boolean verifyToken(final String uuid);
}
