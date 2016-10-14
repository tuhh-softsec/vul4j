package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the filter database table.
 * 
 */
@Entity
public class Filter implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="data_index")
    private String dataIndex;

    private String label;

    private Boolean multiselect;

    //bi-directional many-to-one association to Query
    @ManyToOne
    private Query query;

    @ManyToOne
    @JoinColumn(name="type", insertable=false, updatable=false)
    private FilterType type;

    @Column(name="type")
    private Integer typeId;

    @Transient
    private String value;

    public Filter() {
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

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getMultiselect() {
        return this.multiselect;
    }

    public void setMultiselect(Boolean multiselect) {
        this.multiselect = multiselect;
    }

    @JsonIgnore
    public Query getQuery() {
        return this.query;
    }

    @JsonIgnore
    public void setQuery(Query query) {
        this.query = query;
    }

    public String getType() {
        return this.type.getType();
    }

    /**
     * @return the typeId
     */
    @JsonIgnore
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    @JsonIgnore
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
