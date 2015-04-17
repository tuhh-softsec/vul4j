package de.intevation.lada.importer.laf;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.stamm.SOrt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

/**
 * Class to produce Ort/LOrt objects from a given set of attributes.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class OrtCreator
{
    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    private Integer probeId;
    private Integer ortId;
    private String ortCode;
    private String ortTyp;
    private String zusatztext;
    private String landLang;
    private String landKurz;
    private String landS;
    private String gemSchluessel;
    private String gemName;
    private String koordArt;
    private String koord;
    private String koordS;
    private String bezeichnung;
    private String beschreibung;
    private String nuts;
    private String hoehe;
    private String koordinaten;
    private String koordinatenS;

    public OrtCreator() {
        this.ortId = null;
    }

    /**
     * @return the probeId
     */
    public Integer getProbeId() {
        return this.probeId;
    }

    /**
     * @param probeId the probeId to set
     */
    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    /**
     * @return the ortCode
     */
    public String getOrtCode() {
        return ortCode;
    }

    /**
     * @param ortCode   the ortCode to set
     */
    public void setOrtCode(String ortCode) {
        this.ortCode = ortCode;
    }

    /**
     * @return the ortTyp
     */
    public String getOrtTyp() {
        return ortTyp;
    }

    /**
     * @param ortTyp the ortTyp to set
     */
    public void setOrtTyp(String ortTyp) {
        this.ortTyp = ortTyp;
    }

    /**
     * @return the zusatztext
     */
    public String getZusatztext() {
        return zusatztext;
    }

    /**
     * @param zusatztext the zusatztext to set
     */
    public void setZusatztext(String zusatztext) {
        this.zusatztext = zusatztext;
    }

    /**
     * @return the landLang
     */
    public String getLandLang() {
        return landLang;
    }

    /**
     * @param landLang the landLang to set
     */
    public void setLandLang(String landLang) {
        this.landLang = landLang;
    }

    /**
     * @return the landKurz
     */
    public String getLandKurz() {
        return landKurz;
    }

    /**
     * @param landKurz the landKurz to set
     */
    public void setLandKurz(String landKurz) {
        this.landKurz = landKurz;
    }

    /**
     * @return the landS
     */
    public String getLandS() {
        return landS;
    }

    /**
     * @param landS the landS to set
     */
    public void setLandS(String landS) {
        this.landS = landS;
    }

    /**
     * @return the gemSchluessel
     */
    public String getGemSchluessel() {
        return gemSchluessel;
    }

    /**
     * @param gemSchluessel the gemSchluessel to set
     */
    public void setGemSchluessel(String gemSchluessel) {
        this.gemSchluessel = gemSchluessel;
    }

    /**
     * @return the koordArt
     */
    public String getKoordArt() {
        return koordArt;
    }

    /**
     * @param koordArt the koordArt to set
     */
    public void setKoordArt(String koordArt) {
        this.koordArt = koordArt;
    }

    /**
     * @return the koord
     */
    public String getKoord() {
        return koord;
    }

    /**
     * @param koord the koord to set
     */
    public void setKoord(String koord) {
        this.koord = koord;
    }

    /**
     * @return the koordS
     */
    public String getKoordS() {
        return koordS;
    }

    /**
     * @param koordS the koordS to set
     */
    public void setKoordS(String koordS) {
        this.koordS = koordS;
    }

    /**
     * @return the nuts
     */
    public String getNuts() {
        return nuts;
    }

    /**
     * @param nuts the nuts to set
     */
    public void setNuts(String nuts) {
        this.nuts = nuts;
    }

    /**
     * @return the hoehe
     */
    public String getHoehe() {
        return hoehe;
    }

    /**
     * @param hoehe the hoehe to set
     */
    public void setHoehe(String hoehe) {
        this.hoehe = hoehe;
    }

    /**
     * @return the bezeichung
     */
    public String getBezeichnung() {
        return bezeichnung;
    }

    /**
     * @param bezeichnung the bezeichnung to set
     */
    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    /**
     * @return the gemName
     */
    public String getGemName() {
        return gemName;
    }

    /**
     * @param gemName the gemName to set
     */
    public void setGemName(String gemName) {
        this.gemName = gemName;
    }

    /**
     * @return the beschreibung
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung the beschreibung to set
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * @return the koordinaten
     */
    public String getKoordinaten() {
        return koordinaten;
    }

    /**
     * @param koordinaten the koordinaten to set
     */
    public void setKoordinaten(String koordinaten) {
        this.koordinaten = koordinaten;
    }

    /**
     * @return the koordinatenS
     */
    public String getKoordinatenS() {
        return koordinatenS;
    }

    /**
     * @param koordinatenS the koordinatenS to set
     */
    public void setKoordinatenS(String koordinatenS) {
        this.koordinatenS = koordinatenS;
    }

    /**
     * Add an attribute to the OrtCreator. The creator is used to build the
     * two objects Ort and LOrt.
     *
     * @param key       The key mapping to a object member.
     * @param value     The value to set.
     * @param ort       The creator object.
     * @return The updated creator object.
     */
    public void addAttribute(
        String key,
        Object value
    ) {
        if ("ort_code".equals(key)) {
            this.setOrtCode(value.toString());
        }
        if ("ort_typ".equals(key)) {
            this.setOrtTyp(value.toString());
        }
        if ("ort_zusatz".equals(key)) {
            this.setZusatztext(value.toString());
        }
        if ("ort_land_lang".equals(key)) {
            this.setLandLang(value.toString());
        }
        if ("ort_land_kurz".equals(key)) {
            this.setLandKurz(value.toString());
        }
        if ("ort_land_s".equals(key)) {
            this.setLandS(value.toString());
        }
        if ("ort_gemeindeschluessel".equals(key)) {
            this.setGemSchluessel(value.toString());
        }
        if ("ort_bezeichnung".equals(key)) {
            this.setBezeichnung(value.toString());
        }
        if ("ort_beschreibung".equals(key)) {
            this.setBeschreibung(value.toString());
        }
        if ("ort_nuts_code".equals(key)) {
            this.setNuts(value.toString());
        }
        if ("ort_hoehe_land".equals(key)) {
            this.setHoehe(value.toString());
        }
        if ("ort_koordinaten".equals(key)) {
            this.setKoordinaten(value.toString());
        }
        if ("ort_koordinaten_s".equals(key)) {
            this.setKoordinatenS(value.toString());
        }
    }


    /**
     * Create the Ort object.
     *
     * @return The new Ort.
     */
    public SOrt toOrt() {
        if (this.ortCode != null && this.ortCode.length() > 0) {
            return null;
        }
        SOrt ort = new SOrt();
        // TODO USE NATIVE QUERY...
        //repository.create(ort, "land");
        this.ortId = ort.getId();
        boolean koord = true;
        if (this.koordinatenS != null && this.koordinatenS.length() > 0) {
            ort = setKoordinatenS(ort);
            koord = false;
            if (this.koordinaten != null && this.koordinaten.length() > 0) {
                //TODO: add warning.
            }
        }
        else if (this.koordinaten != null && this.koordinaten.length() > 0) {
            ort = setKoordinaten(ort);
            koord = false;
        }
        if (this.gemSchluessel != null &&
            this.gemSchluessel.length() > 0){
            ort = setGemeindeS(ort, koord);
            koord = false;
            if(this.gemName != null && this.gemName.length() > 0) {
                //TODO: add warning.
            }
        }
        else if (this.gemName != null && this.gemName.length() > 0) {
            ort = setGemeinde(ort, koord);
            koord = false;
        }
        if(this.landS != null && this.landS.length() > 0) {
            ort = setLandS(ort, koord);
            koord = false;
            if (this.landLang != null && this.landLang.length() > 0) {
                //TODO: add warning.
            }
            if (this.landKurz != null && this.landKurz.length() > 0) {
                //TODO: add warning.
            }
        }
        else if (this.landKurz != null && this.landKurz.length() > 0) {
            ort = setLandKurz(ort, koord);
            koord = false;
            if (this.landLang != null && this.landLang.length() > 0) {
                //TODO: add warning.
            }
        }
        else if (this.landLang != null && this.landLang.length() > 0) {
            ort = setLandLang(ort, koord);
        }
        if (koord) {
            //TODO: add warning.
            return null;
        }
        if (this.nuts != null && this.nuts.length() > 0) {
            ort.setNutsCode(nuts);
        }
        else if (ort.getVerwaltungseinheitId() != null &&
            ort.getVerwaltungseinheitId().length() > 0)
        {
            String nativeQuery = "select nuts from stammdaten.verwaltungseinheit where gem_id = '";
            nativeQuery += ort.getVerwaltungseinheitId() + "'";
            Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
            List<Object[]> result = query.getResultList();

            ort.setNutsCode(result.get(0)[0].toString());
        }
        ort.setBeschreibung(beschreibung);
        if (this.hoehe != null) {
            ort.setHoeheLand(Float.valueOf(hoehe));
        }
        //TODO USE NATIVE QUERY...
        //repository.update(ort, "land");
        return ort;
    }

    /**
     * Find the SStaat object identified by the land_lang string and set a
     * reference to the ort object.
     *
     * @param ort       The ort object.
     * @param koord     Set the coordinates or not.
     * @return The Ort object.
     */
    private SOrt setLandLang(SOrt ort, boolean koord) {
        String nativeQuery = "select id, koord_x_extern, koord_y_extern from stammdaten.staat where staat = '";
        nativeQuery += this.landLang + "'";
        Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
        List<Object[]> result = query.getResultList();

        if (result.isEmpty()) {
            //TODO: add warning.
            return ort;
        }
        ort.setStaatId(Integer.valueOf(result.get(0)[0].toString()));
        if (koord) {
            ort.setKoordXExtern(result.get(0)[1].toString());
            ort.setKoordYExtern(result.get(0)[2].toString());
        }
        return ort;
    }

    /**
     * Find the SStaat object identified by the land_kurz string and set a
     * reference to the ort object.
     *
     * @param ort       The ort object.
     * @param koord     Set the coordinates or not.
     * @return The Ort object.
     */
    private SOrt setLandKurz(SOrt ort, boolean koord) {
        String nativeQuery = "select id, koord_x_extern, koord_y_extern from stammdaten.staat where staat_kurz = '";
        nativeQuery += this.landKurz + "'";
        Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
        List<Object[]> result = query.getResultList();

        if (result.isEmpty()) {
            //TODO add warning.
            return ort;
        }
        ort.setStaatId((Integer)result.get(0)[0]);
        if (koord) {
            ort.setKoordXExtern(result.get(0)[1].toString());
            ort.setKoordYExtern(result.get(0)[2].toString());
        }
        return ort;
    }

    /**
     * Find the SStaat object identified by the land_s id and set a
     * reference to the ort object.
     *
     * @param ort       The ort object.
     * @param koord     Set the coordinates or not.
     * @return The Ort object.
     */
    private SOrt setLandS(SOrt ort, boolean koord) {
        ort.setStaatId(Integer.valueOf(this.landS));
        if (koord) {
            String nativeQuery = "select koord_x_extern, koord_y_extern from stammdaten.staat where id = '";
            nativeQuery += this.landS + "'";
            Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
            List<Object[]> result = query.getResultList();
            if (result.isEmpty()) {
                //TODO: add warning.
                return ort;
            }
            ort.setKoordXExtern(result.get(0)[0].toString());
            ort.setLongitude(Double.valueOf(result.get(0)[0].toString()));
            ort.setKoordYExtern(result.get(0)[1].toString());
            ort.setLatitude(Double.valueOf(result.get(0)[1].toString()));
        }
        return ort;
    }

    /**
     * Find the SVerwaltungseinheit object identified by the gem_name string
     * and set a reference to the ort object.
     *
     * @param ort       The ort object.
     * @param koord     Set the coordinates or not.
     * @return The Ort object.
     */
    private SOrt setGemeinde(SOrt ort, boolean koord) {
        String nativeQuery = "select id, koord_x_extern, koord_y_extern from stammdaten.verwaltungseinheit where bezeichnung = '";
        nativeQuery += this.gemName + "'";
        Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
        List<Object[]> result = query.getResultList();
        if (result.isEmpty()) {
            //TODO: add warning.
            return ort;
        }
        ort.setVerwaltungseinheitId(result.get(0)[0].toString());
        if (koord) {
            ort.setKoordXExtern(result.get(0)[1].toString());
            ort.setKoordYExtern(result.get(0)[2].toString());
        }
        return ort;
    }

    /**
     * Find the SVerwaltungseinheit object identified by the gem_schluessel id
     * and set a reference to the ort object.
     *
     * @param ort       The ort object.
     * @param koord     Set the coordinates or not.
     * @return The Ort object.
     */
    private SOrt setGemeindeS(SOrt ort, boolean koord) {
        ort.setVerwaltungseinheitId(this.gemSchluessel);
        if (koord) {
            String nativeQuery = "select koord_x_extern, koord_y_extern from stammdaten.verwaltungseinheit where bezeichnung = '";
            nativeQuery += this.gemName + "'";
            Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
            List<Object[]> result = query.getResultList();
            if (result.isEmpty()) {
                // TODO: add warning.
                return ort;
            }
            ort.setKoordXExtern(result.get(0)[0].toString());
            ort.setKoordYExtern(result.get(0)[1].toString());
        }
        return ort;
    }

    /**
     * Parse the coordinates and the the attributes to the new ort object.
     *
     * @param ort       The ort object.
     * @return The Ort object.
     */
    private SOrt setKoordinaten(SOrt ort) {
        String art = "";
        String x = "";
        String y = "";
        String tmp = "";
        if (this.koordinaten.startsWith("\"")) {
            tmp = this.koordinaten.substring(1);
            art = tmp.substring(0, tmp.indexOf("\""));
            tmp = tmp.substring(tmp.indexOf("\"") + 2);
        }
        else {
            art = this.koordinaten.substring(0, this.koordinaten.indexOf(" "));
            tmp = this.koordinaten.substring(
                0, this.koordinaten.indexOf(" ") + 1);
        }
        if (tmp.startsWith("\"")) {
            tmp = tmp.substring(1);
            x = tmp.substring(0, tmp.indexOf("\""));
            tmp = tmp.substring(0, tmp.indexOf("\"") + 2);
        }
        else {
            x = tmp.substring(0, tmp.indexOf(" "));
            tmp = tmp.substring(0, tmp.indexOf(" ") + 1);
        }
        if (tmp.startsWith("\"")) {
            tmp = tmp.substring(1);
            y = tmp.substring(0, tmp.indexOf("\""));
        }
        else {
            y = tmp;
        }
        ort.setKoordXExtern(x);
        ort.setKoordYExtern(y);
        return ort;
    }

    /**
     * Parse the coordinates and the the attributes to the new ort object.
     *
     * @param ort       The ort object.
     * @return The Ort object.
     */
    private SOrt setKoordinatenS(SOrt ort) {
        String art = "";
        String x = "";
        String y = "";
        String tmp = "";
        if (this.koordinatenS.startsWith("\"")) {
            tmp = this.koordinatenS.substring(1);
            art = tmp.substring(0, tmp.indexOf("\""));
            tmp = tmp.substring(tmp.indexOf("\"") + 2);
        }
        else {
            art = this.koordinatenS.substring(0, this.koordinatenS.indexOf(" "));
            tmp = this.koordinatenS.substring(
                this.koordinatenS.indexOf(" ") + 1);
        }
        if (tmp.startsWith("\"")) {
            tmp = tmp.substring(1);
            x = tmp.substring(0, tmp.indexOf("\""));
            tmp = tmp.substring(tmp.indexOf("\"") + 2);
        }
        else {
            x = tmp.substring(0, tmp.indexOf(" "));
            tmp = tmp.substring(tmp.indexOf(" ") + 1);
        }
        if (tmp.startsWith("\"")) {
            tmp = tmp.substring(1);
            y = tmp.substring(0, tmp.indexOf("\""));
        }
        else {
            y = tmp;
        }
        ort.setKoordXExtern(x);
        ort.setLongitude(Double.valueOf(x));
        ort.setKoordYExtern(y);
        ort.setLatitude(Double.valueOf(y));
        return ort;
    }

    /**
     * Create the new LOrt object from the given attributes.
     *
     * @return The new LOrt object.
     */
    public LOrt toLOrt() {
        if (this.ortId == null &&
            (this.ortCode == null || this.ortCode.length() == 0)
        ) {
            return null;
        }
        if(this.ortCode != null && this.ortCode.length() > 0) {
            String nativeQuery = "select id from stammdaten.ort where bezeichnung = '";
            nativeQuery += this.ortCode + "'";
            Query query = repository.entityManager("land").createNativeQuery(nativeQuery);
            List<Integer> result = query.getResultList();
            if (result != null && !result.isEmpty()) {
                this.ortId = result.get(0);
            }
        }
        LOrt ort = new LOrt();
        ort.setOrt(BigInteger.valueOf(this.ortId));
        ort.setProbeId(this.probeId);
        ort.setOrtsTyp(this.ortTyp);
        ort.setOrtszusatztext(this.zusatztext);
        return ort;
    }

    public void reset() {
        this.beschreibung = null;
        this.bezeichnung = null;
        this.gemName = null;
        this.gemSchluessel = null;
        this.hoehe = null;
        this.koord = null;
        this.koordArt = null;
        this.koordinaten = null;
        this.koordinatenS = null;
        this.koordS = null;
        this.landKurz = null;
        this.landLang = null;
        this.landS = null;
        this.nuts = null;
        this.ortCode = null;
        this.ortId = null;
        this.ortTyp = null;
        this.probeId = null;
        this.zusatztext = null;
    }
}
