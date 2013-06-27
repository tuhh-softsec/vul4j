package de.intevation.lada.authentication;

import java.util.List;


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
