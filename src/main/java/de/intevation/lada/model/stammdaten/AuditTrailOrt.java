package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.databind.JsonNode;

import de.intevation.lada.util.data.JsonObjectType;


/**
 * The persistent class for the audit_trail_ort database table.
 *
 */
@Entity
@Table(name="audit_trail_ort")
@TypeDefs({ @TypeDef(name = "JsonObject", typeClass = JsonObjectType.class) })
public class AuditTrailOrt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String action;

    @Column(name="tstamp")
    private Timestamp tstamp;

    @Column(name="changed_fields")
    @Type(type="JsonObject")
    private JsonNode changedFields;

    @Column(name="ort_id")
    private String ortId;

    @Column(name="object_id")
    private Integer objectId;

    @Column(name="row_data")
    @Type(type="JsonObject")
    private JsonNode rowData;

    @Column(name="table_name")
    private String tableName;

    public AuditTrailOrt() {
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getTstamp() {
        return this.tstamp;
    }

    public void setTstamp(Timestamp tstamp) {
        this.tstamp = tstamp;
    }

    public JsonNode getChangedFields() {
        return this.changedFields;
    }

    public void setChangedFields(JsonNode changedFields) {
        this.changedFields = changedFields;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrtId() {
        return this.ortId;
    }

    public void setOrtId(String ortId) {
        this.ortId = ortId;
    }

    public Integer getObjectId() {
        return this.objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public JsonNode getRowData() {
        return this.rowData;
    }

    public void setRowData(JsonNode rowData) {
        this.rowData = rowData;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
