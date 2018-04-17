package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * The persistent class for the query database table.
 * 
 */
@Entity
public class Query implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String sql;

	public Query() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}