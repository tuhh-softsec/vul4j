/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.auth;

import java.util.List;

/**
 * Response of an authentication module. Contains the user name,
 * 'Messstellen' and 'Netzbetreiber'.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class AuthenticationResponse
{
    private String user;
    private List<String> mst;
    private List<String> netzbetreiber;

    public AuthenticationResponse() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getMst() {
        return mst;
    }

    public void setMst(List<String> mst) {
        this.mst = mst;
    }

    public List<String> getNetzbetreiber() {
        return netzbetreiber;
    }

    public void setNetzbetreiber(List<String> netzbetreiber) {
        this.netzbetreiber = netzbetreiber;
    }
}
