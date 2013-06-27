package de.intevation.lada.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.model.Auth;

@RequestScoped
@Named("ldapauth")
public class LdapAuthentication
implements Authentication
{
    @Inject
    private EntityManager em;

    @Override
    public AuthenticationResponse authorizedGroups(HttpHeaders headers)
    throws AuthenticationException {
        List<String> groups = new ArrayList<String>();
        try {
            LdapName ldap = extractLdapName(headers);
            List<Rdn> rdns = ldap.getRdns();
            for (Rdn rdn: rdns) {
                String value = (String)rdn.getValue();
                if (rdn.getType().equals("cn")) {
                    groups.add(value);
                }
            }
        }
        catch(InvalidNameException ine) {
            throw new AuthenticationException();
        }
        AuthenticationResponse response = getDatabaseAtributes(groups);
        String user = extractUser(headers);
        if (user == null) {
            throw new AuthenticationException();
        }
        response.setUser(user);
        return response;
    }

    private String extractUser(HttpHeaders headers) {
        List<String> user = headers.getRequestHeader("x-ldap-user");
        if (user == null || user.isEmpty()) {
            return null;
        }
        return user.get(0);
    }

    private LdapName extractLdapName(HttpHeaders headers) throws InvalidNameException {
        List<String> attributes = headers.getRequestHeader("x-ldap-groups");
        if (attributes == null ||attributes.isEmpty()) {
            return new LdapName("");
        }
        LdapName ldap = new LdapName("");
        String all = attributes.get(0);
        String[] groups = all.split(";");
        for (int i = 0; i < groups.length; i++) {
            String[] items = groups[i].trim().split(",");
            for (int j = 0; j < items.length; j++) {
                ldap.add(items[j]);
            }
        }
        return ldap;
    }

    private AuthenticationResponse getDatabaseAtributes(List<String> groups) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Auth> criteria = builder.createQuery(Auth.class);
        Root<Auth> member = criteria.from(Auth.class);
        List<Predicate> orFilter = new ArrayList<Predicate>();
        for (String group: groups) {
            orFilter.add(builder.equal(member.get("ldapGroup"), group));
        }
        Predicate orf = builder.or(orFilter.toArray(new Predicate[orFilter.size()]));
        criteria.where(orf);
        List<Auth> result = em.createQuery(criteria).getResultList();
        List<String> mst = new ArrayList<String>();
        List<String> nb = new ArrayList<String>();
        for (Auth a: result) {
            if (a.getSMessStelle() != null) {
                mst.add(a.getSMessStelle().getMstId());
            }
            if (a.getSNetzBetreiber() != null) {
                nb.add(a.getSNetzBetreiber().getNetzbetreiberId());
            }
        }
        AuthenticationResponse response = new AuthenticationResponse();
        response.setMst(mst);
        response.setNetzbetreiber(nb);
        return response;
    }

}
