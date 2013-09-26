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

import org.junit.Test;

/**
 * Test interface for testing {@link TokenService}.
 */
public interface TokenServiceTest {

    /**
     * Testing createToken errors. Test method for testing {@link TokenService#createToken(java.util.Date)}.
     */
    @Test
    void testErrorsCreate();

    /**
     * Testing getTokens errors. Test method for testing {@link TokenService#getToken(String)}.
     */
    @Test
    void testErrorsFindToken();

    /**
     * Testing success create. Test method for testing {@link TokenService#createToken(java.util.Date)}.
     */
    @Test
    void testSuccessCreate();

    /**
     * Testing success getToken. Test method for testing {@link TokenService#getToken(String)}.
     */
    @Test
    void testSuccessGetToken();

    /**
     * Testin success revokeToken. Test method for testing {@link TokenService#revokeToken(String)}.
     */
    @Test
    void testSuccessRevokeToken();

    /**
     * Testing success verifyToken. Test method for testing {@link TokenService#verifyToken(String)}.
     */
    @Test
    void testSuccessVerifyToken();

    /**
     * Testing expiration token, usage token revoke, verify token. Test method for testing
     * {@link TokenService#getToken(String)}, {@link TokenService#verifyToken(String)},
     * {@link TokenService#revokeToken(String)}.
     */
    @Test
    void testTokens();
}
