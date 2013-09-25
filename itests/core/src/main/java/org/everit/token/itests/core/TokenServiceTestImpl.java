package org.everit.token.itests.core;

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

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.everit.token.api.TokenService;
import org.everit.token.api.dto.Token;

public class TokenServiceTestImpl implements TokenServiceTest {

    private TokenService tokenService;

    /**
     * Create validity end date.
     * 
     * @return
     */
    private Date createValidityEndDate() {
        Date actualDate = new Date();
        Calendar validityEndDate = Calendar.getInstance();
        validityEndDate.setTime(actualDate);
        validityEndDate.add(Calendar.DATE, 1);
        return validityEndDate.getTime();
    }

    public void setTokenService(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void testCreateToken() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken.equals(null));
    }

    @Override
    public void testGetToken() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken == null);
        Token token = tokenService.getToken(createToken);
        Assert.assertFalse(token == null);
        Assert.assertEquals(createToken, token.getUuid());
    }

    @Override
    public void testRevokeToken() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken == null);
        Date actualDate = new Date();
        tokenService.revokeToken(createToken);
        Token token = tokenService.getToken(createToken);
        Assert.assertFalse(token == null);
        Assert.assertEquals(createToken, token.getUuid());
        Assert.assertTrue(actualDate.before(token.getRevocationDate()));
    }

}
