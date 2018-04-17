package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the query_messstelle database table.
 * 
 */
@Entity
@Table(name="query_messstelle")
public class QueryMessstelle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="mess_stelle")
	private String messStelle;

	//bi-directional many-to-one association to QueryUser
	@ManyToOne
	@JoinColumn(name="query")
	private QueryUser queryUser;

	public QueryMessstelle() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessStelle() {
		return this.messStelle;
	}

	public void setMessStelle(String messStelle) {
		this.messStelle = messStelle;
	}

	@JsonIgnore
	public QueryUser getQueryUser() {
		return this.queryUser;
	}

	public void setQueryUser(QueryUser queryUser) {
		this.queryUser = queryUser;
	}

}