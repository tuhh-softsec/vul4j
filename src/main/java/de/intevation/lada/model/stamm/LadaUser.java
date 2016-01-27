/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the lada_user database table.
 * 
 */
@Entity
@Table(name="lada_user")
public class LadaUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String name;

    //bi-directional many-to-one association to Favorite
    @OneToMany(mappedBy="ladaUser")
    private List<Favorite> favorites;

    //bi-directional many-to-one association to FilterValue
    @OneToMany(mappedBy="ladaUser")
    private List<FilterValue> filterValues;

    public LadaUser() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Favorite> getFavorites() {
        return this.favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public Favorite addFavorite(Favorite favorite) {
        getFavorites().add(favorite);
        favorite.setLadaUser(this);

        return favorite;
    }

    public Favorite removeFavorite(Favorite favorite) {
        getFavorites().remove(favorite);
        favorite.setLadaUser(null);

        return favorite;
    }

    public List<FilterValue> getFilterValues() {
        return this.filterValues;
    }

    public void setFilterValues(List<FilterValue> filterValues) {
        this.filterValues = filterValues;
    }

    public FilterValue addFilterValue(FilterValue filterValue) {
        getFilterValues().add(filterValue);
        filterValue.setLadaUser(this);

        return filterValue;
    }

    public FilterValue removeFilterValue(FilterValue filterValue) {
        getFilterValues().remove(filterValue);
        filterValue.setLadaUser(null);

        return filterValue;
    }

}
