package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the query_user database table.
 * 
 */
@Entity
@Table(name="query_user")
public class QueryUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String description;

	private String name;

	@Column(name="user_id")
	private Integer userId;

	//uni-directional many-to-one association to Query
	@Column(name="query")
	private Integer query;

	//bi-directional many-to-one association to QueryMessstelle
	@OneToMany(mappedBy="queryUser", fetch=FetchType.EAGER)
	private List<QueryMessstelle> messStelles;

	public QueryUser() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getQuery() {
		return this.query;
	}

	public void setQuery(Integer query) {
		this.query = query;
	}

	public List<QueryMessstelle> getMessStelles() {
		return this.messStelles;
	}

	public void setMessStelles(List<QueryMessstelle> messStelles) {
		this.messStelles = messStelles;
	}

	public QueryMessstelle addMessStelle(QueryMessstelle messStelle) {
		getMessStelles().add(messStelle);
		messStelle.setQueryUser(this);

		return messStelle;
	}

	public QueryMessstelle removeMessStelle(QueryMessstelle messStelle) {
		getMessStelles().remove(messStelle);
		messStelle.setQueryUser(null);

		return messStelle;
	}
}