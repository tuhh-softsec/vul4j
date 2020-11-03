package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the status_kombi database table.
 *
 */
@Entity
@Table(name="status_kombi")
public class StatusKombi implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    //bi-directional many-to-one association to StatusStufe
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stufe_id")
    private StatusStufe statusStufe;

    //bi-directional many-to-one association to StatusWert
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="wert_id")
    private StatusWert statusWert;

    public StatusKombi() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatusStufe getStatusStufe() {
        return this.statusStufe;
    }

    public void setStatusStufe(StatusStufe statusStufe) {
        this.statusStufe = statusStufe;
    }

    public StatusWert getStatusWert() {
        return this.statusWert;
    }

    public void setStatusWert(StatusWert statusWert) {
        this.statusWert = statusWert;
    }

}
