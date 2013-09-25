package org.everit.token.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TOKEN")
public class TokenEntity {

    /**
     * The token UUID.
     */
    @Id
    @Column(name = "TOKEN_UUID")
    private String tokenUuid;

    /**
     * The creation date of the token.
     */
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * The experation date of the token. The token has been used to this date, if the revocation date or date of use
     * field is not filled out.
     */
    @Column(name = "EXPERATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date experationDate;

    /**
     * The revocation date of the token. If not used the token the field is filled in.
     */
    @Column(name = "RECOVATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revocationDate;

    /**
     * The date of the token when used.
     */
    @Column(name = "DATE_OF_USE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfUse;

    /**
     * The default constructor.
     */
    public TokenEntity() {
    }

    /**
     * The constructor when used all fields.
     * 
     * @param tokenUuid
     *            the token UUID.
     * @param creationDate
     *            the creation date.
     * @param experationDate
     *            the experation date.
     * @param revocationDate
     *            the revocation date.
     * @param dateOfUse
     *            the date of used the token.
     */
    public TokenEntity(final String tokenUuid, final Date creationDate, final Date experationDate,
            final Date revocationDate, final Date dateOfUse) {
        super();
        this.tokenUuid = tokenUuid;
        this.creationDate = creationDate;
        this.experationDate = experationDate;
        this.revocationDate = revocationDate;
        this.dateOfUse = dateOfUse;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getDateOfUse() {
        return dateOfUse;
    }

    public Date getExperationDate() {
        return experationDate;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    public String getTokenUuid() {
        return tokenUuid;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDateOfUse(final Date dateOfUse) {
        this.dateOfUse = dateOfUse;
    }

    public void setExperationDate(final Date experationDate) {
        this.experationDate = experationDate;
    }

    public void setRevocationDate(final Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public void setTokenUuid(final String tokenUuid) {
        this.tokenUuid = tokenUuid;
    }
}
