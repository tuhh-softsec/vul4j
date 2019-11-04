package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.json.JsonObject;
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
 * The persistent class for the audit_trail_probe database table.
 * 
 */
@Entity
@Table(name="audit_trail_probe")
@TypeDefs({ @TypeDef(name = "JsonObject", typeClass = JsonObjectType.class) })
public class AuditTrailProbe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name="changed_fields")
    @Type(type="JsonObject")
    private JsonNode changedFields;

    @Column(name="row_data")
    @Type(type="JsonObject")
    private JsonNode rowData;

    @Column(name="tstamp")
    private Timestamp tstamp;

    @Column(name="action")
    private String action;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="object_id")
    private Integer objectId;

    @Column(name="ort_id")
    private Integer ortId;

    @Column(name="probe_id")
    private Integer probeId;

    @Column(name="table_name")
    private String tableName;

    public AuditTrailProbe() {
    }

    public JsonNode getChangedFields() {
        return this.changedFields;
    }

    public void setChangedFields(JsonNode changedFields) {
        this.changedFields = changedFields;
    }

    public JsonNode getRowData() {
        return rowData;
    }

    public void setRowData(JsonNode rowData) {
        this.rowData = rowData;
    }

    public Timestamp getTstamp() {
        return tstamp;
    }

    public void setTstamp(Timestamp tstamp) {
        this.tstamp = tstamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public Integer getObjectId() {
        return this.objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getOrtId() {
        return this.ortId;
    }

    public void setOrtId(Integer ortId) {
        this.ortId = ortId;
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
