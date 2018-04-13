package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the filter_type database table.
 * 
 */
@Entity
@Table(name="filter_type")
@NamedQuery(name="FilterType.findAll", query="SELECT f FROM FilterType f")
public class FilterType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private Boolean multiselect;

    private String type;

    public FilterType() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getMultiselect() {
        return this.multiselect;
    }

    public void setMultiselect(Boolean multiselect) {
        this.multiselect = multiselect;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
