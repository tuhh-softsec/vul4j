package org.esigate.extension.surrogate.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The Surrogate-Capabilities request header allows surrogates to advertise their capabilities with capability tokens.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class SurrogateCapabilitiesHeader {
    private List<SurrogateCapabilities> surrogates;

    private SurrogateCapabilitiesHeader() {
        surrogates = new ArrayList<SurrogateCapabilities>();
    }

    public static SurrogateCapabilitiesHeader fromHeaderValue(String value) {
        SurrogateCapabilitiesHeader result = new SurrogateCapabilitiesHeader();

        if (value == null) {
            return result;
        }

        String[] surrogates = value.split(",");
        for (String surrogate : surrogates) {
            result.surrogates.add(SurrogateCapabilities.fromString(surrogate.trim()));
        }

        return result;
    }

    public List<SurrogateCapabilities> getSurrogates() {
        return surrogates;
    }

    public SurrogateCapabilities getSurrogate(String token) {
        for (SurrogateCapabilities s : surrogates) {
            if (token.equals(s.getDeviceToken())) {
                return s;
            }
        }
        return null;
    }
    
    
    

    @Override
    public String toString() {

        return StringUtils.join(surrogates, ", ");
    }
}
