package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the result_type database table.
 *
 */
@Entity
@Table(name="result_type")
@NamedQuery(name="ResultType.findAll", query="SELECT r FROM ResultType r")
public class ResultType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String format;

	private String name;

	public ResultType() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}