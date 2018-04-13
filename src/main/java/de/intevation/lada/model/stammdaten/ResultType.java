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

	//bi-directional many-to-one association to Result
	@OneToMany(mappedBy="resultType")
	private List<Result> results;

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

	public List<Result> getResults() {
		return this.results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public Result addResult(Result result) {
		getResults().add(result);
		result.setResultType(this);

		return result;
	}

	public Result removeResult(Result result) {
		getResults().remove(result);
		result.setResultType(null);

		return result;
	}
}