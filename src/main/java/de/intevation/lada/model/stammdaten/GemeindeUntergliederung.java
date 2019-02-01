package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Point;



/**
 * The persistent class for the ort database table.
 *
 */
@Entity
@Table(name="gemeindeuntergliederung")
public class GemeindeUntergliederung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    @Column(name="gem_id")
    private String gemId;

    @Column(name="ozk_id")
    private Integer ozkId;

    @Column(name="gemeindeuntergliederung")
    private String gemeindeUntergliederung;

    @Column(name="kda_id")
    private Integer kdaId;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    @Column(name="letzte_aenderung", insertable=false)
    private Timestamp letzteAenderung;

    @Column(columnDefinition="geometry(Point, 4326)")
    private Point geom;

    @Transient
    private boolean readonly;

    @Transient
    private Double longitude;

    @Transient
    private Double latitude;

    public GemeindeUntergliederung() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

    public String getGemId() {
        return this.gemId;
    }

    public void setGemId(String gemId) {
        this.gemId = gemId;
    }

    public Integer getOzkId() {
        return this.ozkId;
    }

    public void setOzkId(Integer ozkId) {
        this.ozkId = ozkId;
    }

    public String getGemeindeUntergliederung() {
        return this.gemeindeUntergliederung;
    }

    public void setGemeindeUntergliederung(String gemeindeUntergliederung) {
        this.gemeindeUntergliederung = gemeindeUntergliederung;
    }

    public Integer getKdaId() {
        return this.kdaId;
    }

    public void setKdaId(Integer kdaId) {
        this.kdaId = kdaId;
    }

    public String getKoordXExtern() {
        return this.koordXExtern;
    }

    public void setKoordXExtern(String koordXExtern) {
        this.koordXExtern = koordXExtern;
    }

    public String getKoordYExtern() {
        return this.koordYExtern;
    }

    public void setKoordYExtern(String koordYExtern) {
        this.koordYExtern = koordYExtern;
    }

    public Double getLatitude() {
        // We might want to serialize an object without geom
        return this.geom != null
            ? this.geom.getY()
            : null;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Double getLongitude() {
        // We might want to serialize an object without geom
        return this.geom != null
            ? this.geom.getX()
            : null;
    }

    @JsonIgnore
    public Point getGeom() {
        return geom;
    }

    @JsonIgnore
    public void setGeom(Point geom) {
        this.geom = geom;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

}
