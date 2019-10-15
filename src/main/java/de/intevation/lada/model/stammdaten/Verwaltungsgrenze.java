package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiPolygon;


/**
 * The persistent class for the verwaltungsgrenze database table.
 *
 */
@Entity
@Table(name="verwaltungsgrenze")
public class Verwaltungsgrenze implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="gem_id")
	private String gemId;

	@Column(name="is_gemeinde")
	private Boolean isGemeinde;

	@Type(type = "jts_geometry")
	@Column(columnDefinition="geometry(MultiPolygon, 4326)")
	private MultiPolygon shape;

	public Verwaltungsgrenze() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGemId() {
		return this.gemId;
	}

	public void setGemId(String gemId) {
		this.gemId = gemId;
	}

	public MultiPolygon getShape() {
		return this.shape;
	}

	public void setShape(MultiPolygon shape) {
		this.shape = shape;
	}

}
