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
     *            the validity date of the token.
     * @return the token UUID.
     */
    String createToken(final Date validityEndDate);

    /**
     * Getting the token information.
     * 
     * @param uuid
     *            the token UUID.
     * @return the {@link Token} object.
     */
    Token getToken(final String uuid);

    /**
     * The token will be withdrawn without use.
     * 
     * @param uuid
     *            the token UUID which withdrawn.
     * @return <code>true</code> if revoke the token, otherwise <code>false</code>.
     */
    boolean revokeToken(final String uuid);

    /**
     * The token verification.
     * 
     * @param uuid
     *            the token UUID which verification.
     * @return <code>true</code> if valid the token UUID otherwise <code>false</code>.
     */
    boolean verifyToken(final String uuid);
}
