/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the messprogramm_messung database table.
 */
@Entity
@Table(name="messprogramm_messung")
public class MessprogrammMessung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="mmt_id")
    private String mmtId;

    @ManyToOne
    private Messprogramm messprogramm;

    public MessprogrammMessung() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMmtId() {
        return this.mmtId;
    }

    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    public Messprogramm getMessprogramm() {
        return this.messprogramm;
    }

    public void setMessprogramm(Messprogramm messprogramm) {
        this.messprogramm = messprogramm;
    }

}
