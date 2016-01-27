package de.intevation.lada.model.stamm;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the lada_user database table.
 * 
 */
@Entity
@Table(name="lada_user")
@NamedQuery(name="LadaUser.findAll", query="SELECT l FROM LadaUser l")
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