package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the query_type database table.
 * 
 */
@Entity
@Table(name="query_type")
@NamedQuery(name="QueryType.findAll", query="SELECT q FROM QueryType q")
public class QueryType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String type;

	public QueryType() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}