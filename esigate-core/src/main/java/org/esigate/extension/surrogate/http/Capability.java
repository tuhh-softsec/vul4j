package org.esigate.extension.surrogate.http;

/**
 * 
 * Capability tokens indicate sets of operations (e.g., caching, processing) that a surrogate is willing to perform.
 * They follow the form of product tokens in the HTTP;
 * <p>
 * capability = token [ "/" token ]
 * <p>
 * Capability tokens are case-sensitive.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class Capability {

    private String id;
    private String version;

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    private Capability() {
        version = null;
        id = null;
    }

    public static Capability fromToken(String token) {
        Capability cap = new Capability();

        if (token == null) {
            return cap;
        }

        int separatorIndex = token.indexOf('/');
        if (separatorIndex >= 0) {
            cap.id = token.substring(0, separatorIndex).trim();
            cap.version = token.substring(separatorIndex + 1).trim();
        } else {
            cap.id = token.trim();
        }

        return cap;
    }

    @Override
    public String toString() {

        String token = id;
        if (version != null) {
            token = token + "/" + version;
        }
        return token;
    }
}
