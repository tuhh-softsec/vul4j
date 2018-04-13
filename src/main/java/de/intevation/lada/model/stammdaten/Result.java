package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the result database table.
 * 
 */
@Entity
@NamedQuery(name="Result.findAll", query="SELECT r FROM Result r")
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="data_index")
	private String dataIndex;

	private Boolean flex;

	private String header;

	private Integer index;

	private Integer width;

	//bi-directional many-to-one association to ResultType
	@ManyToOne
	@JoinColumn(name="data_type")
	private ResultType resultType;

	//bi-directional many-to-one association to Query
	@ManyToOne
	private Query query;

	public Result() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDataIndex() {
		return this.dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public Boolean getFlex() {
		return this.flex;
	}

	public void setFlex(Boolean flex) {
		this.flex = flex;
	}

	public String getHeader() {
		return this.header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Integer getIndex() {
		return this.index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public ResultType getResultType() {
		return this.resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	public Query getQuery() {
		return this.query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

}