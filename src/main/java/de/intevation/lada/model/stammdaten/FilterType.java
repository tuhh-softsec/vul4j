package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the filter_type database table.
 * 
 */
@Entity
@Table(name="filter_type")
@NamedQuery(name="FilterType.findAll", query="SELECT f FROM FilterType f")
public class FilterType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private Boolean multiselect;

	private String type;

	//bi-directional many-to-one association to Filter
	@OneToMany(mappedBy="filterType")
	private List<Filter> filters;

	public FilterType() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getMultiselect() {
		return this.multiselect;
	}

	public void setMultiselect(Boolean multiselect) {
		this.multiselect = multiselect;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Filter> getFilters() {
		return this.filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public Filter addFilter(Filter filter) {
		getFilters().add(filter);
		filter.setFilterType(this);

		return filter;
	}

	public Filter removeFilter(Filter filter) {
		getFilters().remove(filter);
		filter.setFilterType(null);

		return filter;
	}

}