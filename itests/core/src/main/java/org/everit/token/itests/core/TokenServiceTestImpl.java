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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.everit.token.api.TokenService;
import org.everit.token.api.dto.Token;

/**
 * Implementation of {@link TokenServiceTest}.
 */
public class TokenServiceTestImpl implements TokenServiceTest {

    private TokenService tokenService;

    /**
     * Create validity end date.
     * 
     * @return the validity end date. The validity end date is actual date plus one day.
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
    public void testErrorsCreate() {
        try {
            tokenService.createToken(null);
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            Date actualDate = new Date();
            Calendar validityEndDate = Calendar.getInstance();
            validityEndDate.setTime(actualDate);
            validityEndDate.add(Calendar.DATE, -1);
            tokenService.createToken(validityEndDate.getTime());
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

    }

    @Override
    public void TestErrorsGetToken() {

        String testUuuid = "abcde-fgre-234012";
        try {
            tokenService.getToken(null);
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        Token token = tokenService.getToken(testUuuid);
        Assert.assertTrue(token == null);

    }

    @Override
    public void testSuccessCreate() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken == null);
    }

    @Override
    public void testSuccessGetToken() {
        int length = 4;
        List<String> tokensUuid = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            String createToken = tokenService.createToken(createValidityEndDate());
            Assert.assertFalse(createToken == null);
            tokensUuid.add(createToken);
        }
        int index = new Random().nextInt(length);
        Token token = tokenService.getToken(tokensUuid.get(index));
        Assert.assertFalse(token == null);
    }

    @Override
    public void testSuccessRevokeToken() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken == null);

        boolean revokeToken = tokenService.revokeToken(createToken);
        Assert.assertTrue(revokeToken);

        Token token = tokenService.getToken(createToken);
        Assert.assertFalse(token == null);
        Assert.assertEquals(createToken, token.getUuid());

        Assert.assertNotNull(token.getRevocationDate());
        Assert.assertTrue(token.getCreationDate().getTime() < token.getRevocationDate().getTime());
    }

    @Override
    public void testSuccessVerifyToken() {
        String createToken = tokenService.createToken(createValidityEndDate());
        Assert.assertFalse(createToken == null);

        boolean verifyToken = tokenService.verifyToken(createToken);
        Assert.assertTrue(verifyToken);

    }

    @Override
    public void testTokens() {
        List<String> tokenUuids = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            Date actualDate = new Date();
            Calendar validityEndDate = Calendar.getInstance();
            validityEndDate.setTime(actualDate);
            validityEndDate.add(Calendar.MILLISECOND, 100);
            String expiriedToken = tokenService.createToken(validityEndDate.getTime());
            Assert.assertFalse(expiriedToken == null);
            tokenUuids.add(expiriedToken);
        }
        for (int i = 100; i < 200; i++) {
            String expiriedToken = tokenService.createToken(createValidityEndDate());
            Assert.assertFalse(expiriedToken == null);
            tokenUuids.add(expiriedToken);
        }

        int j = 0;
        for (int i = 0; i < 100; i++) {
            if (j == 0) {
                Token token2 = tokenService.getToken(tokenUuids.get(i));
                Assert.assertNotNull(token2);
                Assert.assertNotNull(token2.getExpirationDate());
            } else if (j == 1) {
                boolean verifyToken = tokenService.verifyToken(tokenUuids.get(i));
                Assert.assertFalse(verifyToken);
                j = -1;
            }
            j++;
        }

        j = 0;
        for (int i = 100; i < 200; i++) {
            if (j == 0) {
                boolean verifyToken = tokenService.verifyToken(tokenUuids.get(i));
                Assert.assertTrue(verifyToken);
                Token token = tokenService.getToken(tokenUuids.get(i));
                Assert.assertNotNull(token);
                Assert.assertNotNull(token.getDateOfUse());
                boolean revokeToken = tokenService.revokeToken(tokenUuids.get(i));
                Assert.assertFalse(revokeToken);
                token = tokenService.getToken(tokenUuids.get(i));
                Assert.assertNotNull(token);
                Assert.assertTrue(token.getRevocationDate() == null);
                verifyToken = tokenService.verifyToken(tokenUuids.get(i));
                Assert.assertFalse(verifyToken);
            } else if (j == 1) {
                boolean revokeToken = tokenService.revokeToken(tokenUuids.get(i));
                Assert.assertTrue(revokeToken);
                revokeToken = tokenService.revokeToken(tokenUuids.get(i));
                Assert.assertFalse(revokeToken);
                boolean verifyToken = tokenService.verifyToken(tokenUuids.get(i));
                Assert.assertFalse(verifyToken);
                j = -1;
            }
            j++;
        }

        Token token = tokenService.getToken(tokenUuids.get(0));
        System.out.println(token.getUuid());
        Assert.assertFalse(token == null);

        String testUuuid = "abcde-fgre-234012";
        boolean verifyToken = tokenService.verifyToken(testUuuid);
        Assert.assertFalse(verifyToken);

        boolean revokeToken = tokenService.revokeToken(testUuuid);
        Assert.assertFalse(revokeToken);

        try {
            tokenService.verifyToken(null);
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            tokenService.revokeToken(null);
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

    }

}
