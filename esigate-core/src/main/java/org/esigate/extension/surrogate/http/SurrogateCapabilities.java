package org.esigate.extension.surrogate.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The name in each capability set identifies a device token, which uniquely identifies the surrogate that appended it.
 * Device tokens must be unique within a request's Surrogate-Capabilities header.
 * <p>
 * The value contains a space-separated list of capability tokens.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class SurrogateCapabilities {
    private List<Capability> capabilities;
    private String deviceToken;

    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    private SurrogateCapabilities() {
        capabilities = new ArrayList<Capability>();
        deviceToken = null;
    }

    public static SurrogateCapabilities fromString(String surrogateString) {
        SurrogateCapabilities cap = new SurrogateCapabilities();
        int equalsIndex = surrogateString.indexOf('=');

        if (equalsIndex >= 0) {

            cap.deviceToken = surrogateString.substring(0, equalsIndex).trim();

            String allCapabiities = surrogateString.substring(equalsIndex + 1).trim();

            if (allCapabiities.startsWith("\"") && allCapabiities.endsWith("\"")) {
                allCapabiities = allCapabiities.substring(1, allCapabiities.length() - 1);
            }
            String[] capabilities = allCapabiities.split(" ");
            for (String c : capabilities) {

                c = c.trim();

                if (c.length() > 0) {
                    cap.capabilities.add(Capability.fromToken(c));
                }
            }

        }
        return cap;
    }

    @Override
    public String toString() {
        return deviceToken + "=" + "\"" + StringUtils.join(capabilities, " ") + "\"";
    }
}
