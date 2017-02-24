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

import de.intevation.lada.util.data.JsonObjectType;

/**
 * The persistent class for the audit_trail database table.
 * 
 */
@Entity
@Table(name="audit_trail")
@TypeDefs({ @TypeDef(name = "JsonObject", typeClass = JsonObjectType.class) })
public class AuditTrail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String action;

    @Column(name="action_tstamp_clk")
    private Timestamp actionTstampClk;

    @Column(name="changed_fields")
    @Type(type="JsonObject")
    private JsonObject changedFields;

    @Column(name="object_id")
    private Integer objectId;

    @Column(name="row_data")
    @Type(type="JsonObject")
    private JsonObject rowData;

    @Column(name="table_name")
    private String tableName;

    public AuditTrail() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getActionTstampClk() {
        return this.actionTstampClk;
    }

    public void setActionTstampClk(Timestamp actionTstampClk) {
        this.actionTstampClk = actionTstampClk;
    }

    public JsonObject getChangedFields() {
        return this.changedFields;
    }

    public void setChangedFields(JsonObject changedFields) {
        this.changedFields = changedFields;
    }

    public Integer getObjectId() {
        return this.objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public JsonObject getRowData() {
        return this.rowData;
    }

    public void setRowData(JsonObject rowData) {
        this.rowData = rowData;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
