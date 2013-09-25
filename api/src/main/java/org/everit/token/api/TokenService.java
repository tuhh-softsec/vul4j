package org.everit.token.api;

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
     */
    boolean revokeToken(final String uuid);

    /**
     * The token verification.
     * 
     * @param uuid
     *            the token UUID which verification. Cannot be <code>null</code>.
     * @return <code>true</code> if valid the token UUID otherwise <code>false</code>.
     */
    boolean verifyToken(final String uuid);
}
