package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the mess_einheit database table.
 * 
 */
@Entity
@Table(name="mess_einheit")
public class MessEinheit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    private String einheit;

    /**
     * Get all MasseinheitUmrechnungs for units that can be converted into
     * this one.
     */
    @OneToMany(mappedBy="mehIdZu", fetch=FetchType.EAGER)
    @JsonIgnore
    private List<MassEinheitUmrechnung> massEinheitUmrechnungZus;

    @Column(name="eudf_messeinheit_id")
    private String eudfMesseinheitId;

    @Column(name="umrechnungs_faktor_eudf")
    private Long umrechnungsFaktorEudf;

    /**
     * Attribute used to distinguish between primary and secondary messeinheit
     * records.
     */
    @Transient
    private Boolean primary;

    public MessEinheit() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getEinheit() {
        return this.einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public String getEudfMesseinheitId() {
        return this.eudfMesseinheitId;
    }

    public void setEudfMesseinheitId(String eudfMesseinheitId) {
        this.eudfMesseinheitId = eudfMesseinheitId;
    }

    public Long getUmrechnungsFaktorEudf() {
        return this.umrechnungsFaktorEudf;
    }

    public void setUmrechnungsFaktorEudf(Long umrechnungsFaktorEudf) {
        this.umrechnungsFaktorEudf = umrechnungsFaktorEudf;
    }

    public List<MassEinheitUmrechnung> getMassEinheitUmrechnungZus() {
        return this.massEinheitUmrechnungZus;
    }

    public Boolean getPrimary() {
        return this.primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }
}
