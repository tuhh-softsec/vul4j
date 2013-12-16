package org.esigate.util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * A list that contains some String tokens and is intended to be used as a black list or white list. Methods "add" and
 * "remove" support single String tokens, comma-separated lists and "*" wildcard expression. The list is
 * case-insensitive.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class FilterList {
    private Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    private boolean defaultContains = false;

    /**
     * Add some tokens to the list. By default the list is empty. If the list already contains the added tokens or
     * everything, this method will have no effect.
     * 
     * @param toAdd
     *            String tokens to add to the list. This argument can be:
     *            <ul>
     *            <li>A single String token</li>
     *            <li>A comma-separated list of String tokens</li>
     *            <li>* (in this case the list will then act as if it contained any token but you will be able to make
     *            except some tokens using the remove method)</li>
     *            </ul>
     */
    public void add(Collection<String> toAdd) {
        if (toAdd.contains("*")) {
            set.clear();
            defaultContains = true;
        } else {
            if (!defaultContains) {
                set.addAll(toAdd);
            } else {
                set.removeAll(toAdd);
            }
        }
    }

    /**
     * Remove some tokens from the list. If the list is already empty or does not contains the tokens, this method will
     * have no effect.
     * 
     * @param toRemove
     *            String tokens to add to the list. This argument can be:
     *            <ul>
     *            <li>A single String token</li>
     *            <li>A comma-separated list of String tokens</li>
     *            <li>* (in this case the list will then be empty again)</li>
     *            </ul>
     */
    public void remove(Collection<String> toRemove) {
        if (toRemove.contains("*")) {
            set.clear();
            defaultContains = false;
        } else {
            if (defaultContains) {
                set.addAll(toRemove);
            } else {
                set.removeAll(toRemove);
            }
        }
    }

    /**
     * @param token
     * @return true if the list contains the token
     */
    public boolean contains(String token) {
        if (defaultContains) {
            return !set.contains(token);
        } else {
            return set.contains(token);
        }
    }
}
