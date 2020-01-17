package de.intevation.lada.model.stammdaten;

import de.intevation.lada.model.land.TagZuordnung;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the tag database table
 */
@Entity
@Table(name="tag", schema="stamm")
public class Tag {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="tag")
    private String tag;

    @Column(name="mst_id")
    private String mstId;

    @OneToMany(mappedBy="tag", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<TagZuordnung> tagZuordnungs;

    private Boolean generated;


    public Tag() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public Set<TagZuordnung> getTagZuordnungs() {
        return this.tagZuordnungs;
    }

    public void setTagZuordnungs(Set<TagZuordnung> tagZuordnungs) {
        this.tagZuordnungs = tagZuordnungs;
    }

    public Boolean getGenerated() {
        return this.generated;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }
}