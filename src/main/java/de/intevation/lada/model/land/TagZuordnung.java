package de.intevation.lada.model.land;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.intevation.lada.model.stammdaten.Tag;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the tagzuordnung database table
 */
@Entity
@Table(name="tagzuordnung", schema="land")
public class TagZuordnung {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="probe_id")
    private Integer probeId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name="tag_id")
    private Tag tag;

    @Transient
    private Integer tagId;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probe) {
        this.probeId = probe;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Integer getTagId() {
        return this.tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}