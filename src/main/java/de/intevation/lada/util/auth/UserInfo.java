package de.intevation.lada.util.auth;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String name;
    private List<String> messstellen;
    private List<String> netzbetreiber;
    private List<String> roles;

    public UserInfo() {
        messstellen = new ArrayList<String>();
        netzbetreiber = new ArrayList<String>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the messstellen
     */
    public List<String> getMessstellen() {
        return messstellen;
    }

    /**
     * @param messstellen the messstellen to set
     */
    public void setMessstellen(List<String> messstellen) {
        this.messstellen = messstellen;
    }

    /**
     * @return the netzbetreiber
     */
    public List<String> getNetzbetreiber() {
        return netzbetreiber;
    }

    /**
     * @param netzbetreiber the netzbetreiber to set
     */
    public void setNetzbetreiber(List<String> netzbetreiber) {
        this.netzbetreiber = netzbetreiber;
    }

    /**
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
