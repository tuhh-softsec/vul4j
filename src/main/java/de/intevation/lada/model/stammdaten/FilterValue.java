package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the filter_value database table.
 * 
 */
@Entity
@Table(name="filter_value")
public class FilterValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="user_id")
    private Integer userId;

    private String value;

    public FilterValue() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
