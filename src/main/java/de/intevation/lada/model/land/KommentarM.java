package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the kommentar_m database table.
 * 
 */
@Entity
@Table(name="kommentar_m", schema="land")
public class KommentarM implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private Timestamp datum;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="mst_id")
    private String mstId;

    private String text;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public KommentarM() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getDatum() {
        return this.datum;
    }

    public void setDatum(Timestamp datum) {
        this.datum = datum;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the owner
     */
    public boolean isOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(boolean owner) {
        this.owner = owner;
	}

	/**
	 * @return the readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * @param readonly the readonly to set
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

}
