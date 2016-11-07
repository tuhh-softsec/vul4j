package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.intevation.lada.importer.ReportItem;

public class LafObjectListener extends LafBaseListener {

    LafRawData data;
    LafRawData.Probe currentProbe;
    LafRawData.Messung currentMessung;
    Map<String, String> currentUOrt;
    Map<String, String> currentEOrt;
    Map<String, List<ReportItem>> errors;
    ArrayList<ReportItem> currentErrors;

    private boolean hasDatenbasis = false;
    private boolean hasMessprogramm = false;
    private boolean hasUmwelt = false;
    private boolean hasZeitbasis = false;
    private boolean hasUebertragungsformat = false;
    private boolean hasVersion = false;
    private boolean hasEHerkunfstland = false;
    private boolean hasEGemeinde = false;
    private boolean hasEKoordinaten = false;
    private boolean hasUHerkunfstland = false;
    private boolean hasUGemeinde = false;
    private boolean hasUKoordinaten = false;

    public LafObjectListener() {
        data = new LafRawData();
        errors = new HashMap<String, List<ReportItem>>();
        currentErrors = new ArrayList<ReportItem>();
        currentUOrt = new HashMap<String, String>();
    }

    public LafRawData getData() {
        return data;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getErrors() {
        return errors;
    }

    /**
     * @return the hasUebertragungsformat
     */
    public boolean hasUebertragungsformat() {
        return hasUebertragungsformat;
    }

    /**
     * @return the hasVersion
     */
    public boolean hasVersion() {
        return hasVersion;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbendatei(LafParser.ProbendateiContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitProbendatei(LafParser.ProbendateiContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterEnd(LafParser.EndContext ctx) {
        if (currentProbe != null) {
            data.addProbe(currentProbe);
            if (!currentErrors.isEmpty()) {
                errors.put(currentProbe.getIdentifier(),
                    (ArrayList)currentErrors.clone());
            }

            currentErrors.clear();
            currentProbe = null;
            hasDatenbasis = false;
            hasMessprogramm = false;
            hasUmwelt = false;
            hasZeitbasis = false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbe(LafParser.ProbeContext ctx) {
        if (currentMessung != null) {
            currentProbe.addMessung(currentMessung);
            currentMessung = null;
        }
        if (currentUOrt != null && !currentUOrt.isEmpty()) {
            currentProbe.addUrsprungsOrt(currentUOrt);
            currentUOrt.clear();
        }
        if (currentEOrt != null && !currentEOrt.isEmpty()) {
            currentProbe.addEntnahmeOrt(currentEOrt);
            currentEOrt.clear();
        }
        currentEOrt = new HashMap<String, String>();
        currentProbe = data.new Probe();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitProbe(LafParser.ProbeContext ctx) {
        data.addProbe(currentProbe);
        if (!currentErrors.isEmpty()) {
            errors.put(currentProbe.getIdentifier(),
                (ArrayList)currentErrors.clone());
        }
        currentErrors.clear();
        currentProbe = null;
        hasDatenbasis = false;
        hasMessprogramm = false;
        hasUmwelt = false;
        hasZeitbasis = false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterDb(LafParser.DbContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMp(LafParser.MpContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUb(LafParser.UbContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterRei(LafParser.ReiContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPh(LafParser.PhContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPg(LafParser.PgContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPk(LafParser.PkContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterZb(LafParser.ZbContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPzb(LafParser.PzbContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPkom(LafParser.PkomContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSdm(LafParser.SdmContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPnh(LafParser.PnhContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUh(LafParser.UhContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitUh(LafParser.UhContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUg(LafParser.UgContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUk(LafParser.UkContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessung(LafParser.MessungContext ctx) {
        if (currentMessung != null) {
            currentProbe.addMessung(currentMessung);
        }
        if (currentUOrt != null && !currentUOrt.isEmpty()) {
            currentProbe.addUrsprungsOrt(currentUOrt);
            currentUOrt.clear();
        }
        if (currentEOrt != null && !currentEOrt.isEmpty()) {
            currentProbe.addEntnahmeOrt(currentEOrt);
            currentEOrt.clear();
        }
        currentMessung = data.new Messung();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitMessung(LafParser.MessungContext ctx) {
        currentProbe.addMessung(currentMessung);
        currentMessung = null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUrsprungsort(LafParser.UrsprungsortContext ctx) {
        if (currentMessung != null) {
            currentProbe.addMessung(currentMessung);
            currentMessung = data.new Messung();
        }
        if (currentUOrt != null && !currentUOrt.isEmpty()) {
            currentProbe.addUrsprungsOrt(currentUOrt);
        }
        if (currentEOrt != null && !currentEOrt.isEmpty()) {
            currentProbe.addEntnahmeOrt(currentEOrt);
            currentEOrt.clear();
        }
        currentUOrt.clear();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitUrsprungsort(LafParser.UrsprungsortContext ctx) {
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMm(LafParser.MmContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMw(LafParser.MwContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPn(LafParser.PnContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUebertragungsformat(LafParser.UebertragungsformatContext ctx) {
        hasUebertragungsformat = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterVersion(LafParser.VersionContext ctx) {
        hasVersion = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterDatenbasis(LafParser.DatenbasisContext ctx) {
        if (this.hasDatenbasis) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        // Trim double qoutes.
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C6)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasDatenbasis = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterDatenbasis_s(LafParser.Datenbasis_sContext ctx) {
        if (this.hasDatenbasis) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasDatenbasis = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterNetzkennung(LafParser.NetzkennungContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterErzeuger(LafParser.ErzeugerContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterStaat_der_messstelle_lang(LafParser.Staat_der_messstelle_langContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterStaat_der_messstelle_kurz(LafParser.Staat_der_messstelle_kurzContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterStaat_der_messstelle_s(LafParser.Staat_der_messstelle_sContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessstelle(LafParser.MessstelleContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SC5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesslabor(LafParser.MesslaborContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SC5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbe_id(LafParser.Probe_idContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C16)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessungs_id(LafParser.Messungs_idContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProben_nr(LafParser.Proben_nrContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterHauptprobennummer(LafParser.HauptprobennummerContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C20)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterNebenprobennummer(LafParser.NebenprobennummerContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessprogramm_c(LafParser.Messprogramm_cContext ctx) {
        if (this.hasMessprogramm) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasMessprogramm = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessprogramm_s(LafParser.Messprogramm_sContext ctx) {
        if (this.hasMessprogramm) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SC1)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasMessprogramm = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessprogramm_land(LafParser.Messprogramm_landContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenahmeinstitution(LafParser.ProbenahmeinstitutionContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenart(LafParser.ProbenartContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C1)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterZeitbasis(LafParser.ZeitbasisContext ctx) {
        if (this.hasZeitbasis) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasZeitbasis = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterZeitbasis_s(LafParser.Zeitbasis_sContext ctx) {
        if (this.hasZeitbasis) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI1)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasZeitbasis = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSoll_datum_uhrzeit_a(LafParser.Soll_datum_uhrzeit_aContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSoll_datum_uhrzeit_e(LafParser.Soll_datum_uhrzeit_eContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUrsprungs_datum_uhrzeit(LafParser.Ursprungs_datum_uhrzeitContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenahme_datum_uhrzeit_a(LafParser.Probenahme_datum_uhrzeit_aContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenahme_datum_uhrzeit_e(LafParser.Probenahme_datum_uhrzeit_eContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUmweltbereich_c(LafParser.Umweltbereich_cContext ctx) {
        if (this.hasUmwelt) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasUmwelt = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterUmweltbereich_s(LafParser.Umweltbereich_sContext ctx) {
        if (this.hasUmwelt) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SC3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
        this.hasUmwelt = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterDeskriptoren(LafParser.DeskriptorenContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C26)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterRei_programmpunkt(LafParser.Rei_programmpunktContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C10)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterRei_programmpunktgruppe(LafParser.Rei_programmpunktgruppeContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C21)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterReferenz_datum_uhrzeit(LafParser.Referenz_datum_uhrzeitContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterTestdaten(LafParser.TestdatenContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.BOOL)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSzenario(LafParser.SzenarioContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C20)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSek_datenbasis(LafParser.Sek_datenbasisContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterSek_datenbasis_s(LafParser.Sek_datenbasis_sContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_herkunftsland_lang(LafParser.U_herkunftsland_langContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasUHerkunfstland = true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_herkunftsland_kurz(LafParser.U_herkunftsland_kurzContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasUHerkunfstland = true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_herkunftsland_s(LafParser.U_herkunftsland_sContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasUHerkunfstland = true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_gemeindeschluessel(LafParser.U_gemeindeschluesselContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasUGemeinde= true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_gemeindename(LafParser.U_gemeindenameContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasUGemeinde= true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_orts_zusatzkennzahl(LafParser.U_orts_zusatzkennzahlContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_koordinaten(LafParser.U_koordinatenContext ctx) {
        if (ctx.getChildCount() < 6) {
            return;
        }
        String art = ctx.getChild(1).toString();
        art = art.replaceAll("\"", "");
        if (!art.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(art);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord1 = ctx.getChild(3).toString();
        koord1 = koord1.replaceAll("\"", "");
        if (!koord1.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord1);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord2 = ctx.getChild(5).toString();
        koord2 = koord2.replaceAll("\"", "");
        if (!koord2.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord2);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put("U_KOORDINATEN_X", koord1);
        currentUOrt.put("U_KOORDINATEN_Y", koord2);
        currentUOrt.put("U_KOORDINATEN_ART", art);
        hasUKoordinaten = true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_koordinaten_s(LafParser.U_koordinaten_sContext ctx) {
        System.out.println("koordinaten");
        if (ctx.getChildCount() < 6) {
            return;
        }
        String art = ctx.getChild(1).toString();
        art = art.replaceAll("\"", "");
        if (!art.matches(LafDataTypes.SI2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(art);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord1 = ctx.getChild(3).toString();
        koord1 = koord1.replaceAll("\"", "");
        if (!koord1.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord1);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord2 = ctx.getChild(5).toString();
        koord2 = koord2.replaceAll("\"", "");
        if (!koord2.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord2);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        System.out.println("add");
        currentUOrt.put("U_KOORDINATEN_X", koord1);
        currentUOrt.put("U_KOORDINATEN_Y", koord2);
        currentUOrt.put("U_KOORDINATEN_ART_S", art);
        hasUKoordinaten = true;
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_orts_zusatzcode(LafParser.U_orts_zusatzcodeContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_orts_zusatztext(LafParser.U_orts_zusatztextContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.MC50)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterU_nuts_code(LafParser.U_nuts_codeContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C10)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentUOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "ursprungsort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_herkunftsland_lang(LafParser.P_herkunftsland_langContext ctx) {
        if (hasEHerkunfstland) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasEHerkunfstland = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_herkunftsland_kurz(LafParser.P_herkunftsland_kurzContext ctx) {
        if (hasEHerkunfstland) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasEHerkunfstland = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_herkunftsland_s(LafParser.P_herkunftsland_sContext ctx) {
        if (hasEHerkunfstland) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SI8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasEHerkunfstland = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_gemeindeschluessel(LafParser.P_gemeindeschluesselContext ctx) {
        if (hasEGemeinde) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasEGemeinde = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_gemeindename(LafParser.P_gemeindenameContext ctx) {
        if (hasEGemeinde) {
            return;
        }
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        hasEGemeinde = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_orts_zusatzkennzahl(LafParser.P_orts_zusatzkennzahlContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_koordinaten(LafParser.P_koordinatenContext ctx) {
        if (hasEKoordinaten) {
            return;
        }
        if (ctx.getChildCount() < 6) {
            return;
        }
        String art = ctx.getChild(1).toString();
        art = art.replaceAll("\"", "");
        if (!art.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(art);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord1 = ctx.getChild(3).toString();
        koord1 = koord1.replaceAll("\"", "");
        if (!koord1.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord1);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord2 = ctx.getChild(5).toString();
        koord2 = koord2.replaceAll("\"", "");
        if (!koord2.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord2);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put("P_KOORDINATEN_X", koord1);
        currentEOrt.put("P_KOORDINATEN_Y", koord2);
        currentEOrt.put("P_KOORDINATEN_ART", art);
        hasEKoordinaten = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_koordinaten_s(LafParser.P_koordinaten_sContext ctx) {
        if (hasEKoordinaten) {
            return;
        }
        if (ctx.getChildCount() < 6) {
            return;
        }
        String art = ctx.getChild(1).toString();
        art = art.replaceAll("\"", "");
        if (!art.matches(LafDataTypes.SI2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(art);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord1 = ctx.getChild(3).toString();
        koord1 = koord1.replaceAll("\"", "");
        if (!koord1.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord1);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String koord2 = ctx.getChild(5).toString();
        koord2 = koord2.replaceAll("\"", "");
        if (!koord2.matches(LafDataTypes.C22)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(koord2);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put("P_KOORDINATEN_X", koord1);
        currentEOrt.put("P_KOORDINATEN_Y", koord2);
        currentEOrt.put("P_KOORDINATEN_ART_S", art);
        hasEKoordinaten = true;
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_orts_zusatzcode(LafParser.P_orts_zusatzcodeContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_orts_zusatztext(LafParser.P_orts_zusatztextContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.MC50)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_nuts_code(LafParser.P_nuts_codeContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C10)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_site_id(LafParser.P_site_idContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_site_name(LafParser.P_site_nameContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_hoehe_nn(LafParser.P_hoehe_nnContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.F10)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterP_hoehe_land(LafParser.P_hoehe_landContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.F10)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentEOrt.put(ctx.getChild(0).toString().toUpperCase(), value);
        // TODO: Add to "entnahmeort"
        //currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMehrzweckfeld(LafParser.MehrzweckfeldContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.MC300)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        currentProbe.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMess_datum_uhrzeit(LafParser.Mess_datum_uhrzeitContext ctx) {
        if (ctx.getChildCount() < 4) {
            return;
        }
        String date = ctx.getChild(1).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(3).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), date + ' ' + time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesszeit_sekunden(LafParser.Messzeit_sekundenContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.I8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessmethode_c(LafParser.Messmethode_cContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMessmethode_s(LafParser.Messmethode_sContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.SC2)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterBearbeitungsstatus(LafParser.BearbeitungsstatusContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.C4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPep_flag(LafParser.Pep_flagContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.BOOL)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterErfassung_abgeschlossen(LafParser.Erfassung_abgeschlossenContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String value = ctx.getChild(1).toString();
        value = value.replaceAll("\"", "");
        if (!value.matches(LafDataTypes.BOOL)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(value);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addAttribute(ctx.getChild(0).toString().toUpperCase(), value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenzusatzbeschreibung(LafParser.ProbenzusatzbeschreibungContext ctx) {
        // c7* f12 c9 f9
        if (ctx.getChildCount() < 8) {
            return;
        }
        String groesse = ctx.getChild(1).toString();
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = ctx.getChild(3).toString();
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = ctx.getChild(5).toString();
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = ctx.getChild(7).toString();
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> zusatzwert = new HashMap<String, String>();
        zusatzwert.put("PZS", groesse);
        zusatzwert.put("MESSWERT_PZS", wert);
        zusatzwert.put("EINHEIT", einheit);
        zusatzwert.put("MESSFEHLER", fehler);
        currentProbe.addZusatzwert(zusatzwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterPzb_s(LafParser.Pzb_sContext ctx) {
        // sc8* f12 si3 f9
        if (ctx.getChildCount() < 8) {
            return;
        }
        String groesse = ctx.getChild(1).toString();
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.SC8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = ctx.getChild(3).toString();
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = ctx.getChild(5).toString();
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.SI3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = ctx.getChild(7).toString();
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> zusatzwert = new HashMap<String, String>();
        zusatzwert.put("PZS_ID", groesse);
        zusatzwert.put("MESSWERT_PZS", wert);
        zusatzwert.put("EINHEIT_ID", einheit);
        zusatzwert.put("MESSFEHLER", fehler);
        currentProbe.addZusatzwert(zusatzwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert(LafParser.MesswertContext ctx) {
        // c50* f12 c9 f9**
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 5) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = null;
        if (ctx.getChildCount() >= 5) {
            fehler = children.get(4);
            fehler = fehler.replaceAll("\"", "");
            if (!fehler.matches(LafDataTypes.F9)) {
                ReportItem err = new ReportItem();
                err.setKey(ctx.getChild(0).toString());
                err.setValue(fehler);
                err.setCode(670);
                currentErrors.add(err);;
                return;
            }
        }
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_s(LafParser.Messwert_sContext ctx) {
        // si8 f12 si3 f9**
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 5) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.SI8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.SI3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE_ID", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH_ID", einheit);
        messwert.put("MESSFEHLER", fehler);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_i(LafParser.Messwert_iContext ctx) {
        // C50* f12 c9 f9** f9** f9** c50*
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 8) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        // TODO: handle all values
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_g(LafParser.Messwert_gContext ctx) {
        // C50* f12 c9 f9** f9** f9** c1
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 8) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = null;
        if (ctx.getChildCount() >= 8) {
            fehler = children.get(4);
            fehler = fehler.replaceAll("\"", "");
            if (!fehler.matches(LafDataTypes.F9)) {
                ReportItem err = new ReportItem();
                err.setKey(ctx.getChild(0).toString());
                err.setValue(fehler);
                err.setCode(670);
                currentErrors.add(err);;
                return;
            }
        }
        // TODO: handle all values
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_nwg(LafParser.Messwert_nwgContext ctx) {
        // C50* f12 c9 f9** f12
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 6) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String nwg = children.get(5);
        nwg = nwg.replaceAll("\"", "");
        if (!nwg.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(nwg);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        messwert.put("NWG", nwg);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_nwg_s(LafParser.Messwert_nwg_sContext ctx) {
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 6) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.SI8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.SI3)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String nwg = children.get(5);
        nwg = nwg.replaceAll("\"", "");
        if (!nwg.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(nwg);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        // TODO: handle all values
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE_ID", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH_ID", einheit);
        messwert.put("MESSFEHLER", fehler);
        messwert.put("NWG", nwg);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_nwg_i(LafParser.Messwert_nwg_iContext ctx) {
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 8) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String nwg = children.get(5);
        nwg = nwg.replaceAll("\"", "");
        if (!nwg.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(nwg);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        // TODO: handle all values
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        messwert.put("NWG", nwg);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterMesswert_nwg_g(LafParser.Messwert_nwg_gContext ctx) {
        // TODO
        List<String> children = new ArrayList<String>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!ctx.getChild(i).toString().startsWith(" ")) {
                children.add(ctx.getChild(i).toString());
            }
        }
        if (children.size() < 8) {
            return;
        }
        String groesse = children.get(1);
        groesse = groesse.replaceAll("\"", "");
        if (!groesse.matches(LafDataTypes.C_STAR)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(groesse);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String wert = children.get(2);
        wert = wert.replaceAll("\"", "");
        if (!wert.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(wert);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String einheit = children.get(3);
        einheit = einheit.replaceAll("\"", "");
        if (!einheit.matches(LafDataTypes.C9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(einheit);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String fehler = children.get(4);
        fehler = fehler.replaceAll("\"", "");
        if (!fehler.matches(LafDataTypes.F9)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(fehler);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String nwg = children.get(5);
        nwg = nwg.replaceAll("\"", "");
        if (!nwg.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(nwg);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String gw = children.get(8);
        gw = gw.replaceAll("\"", "");
        if (!gw.matches(LafDataTypes.F12)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(gw);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        // TODO: handle all values
        Map<String, String> messwert = new HashMap<String, String>();
        messwert.put("MESSGROESSE", groesse);
        messwert.put("MESSWERT", wert);
        messwert.put("MEH", einheit);
        messwert.put("MESSFEHLER", fehler);
        messwert.put("NWG", nwg);
        messwert.put("GRENZWERT", gw);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addMesswert(messwert);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterKommentar(LafParser.KommentarContext ctx) {
        // c5 d8 t4 mc300
        if (ctx.getChildCount() < 8) {
            return;
        }
        String mst = ctx.getChild(1).toString();
        mst = mst.replaceAll("\"", "");
        if (!mst.matches(LafDataTypes.C5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(mst);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String date = ctx.getChild(3).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(5).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String text = ctx.getChild(7).toString();
        text = text.replaceAll("\"", "");
        if (!text.matches(LafDataTypes.MC300)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(text);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> kommentar = new HashMap<String, String>();
        kommentar.put("MST_ID", mst);
        kommentar.put("DATE", date);
        kommentar.put("TIME", time);
        kommentar.put("TEXT", text);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addKommentar(kommentar);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterKommentar_t(LafParser.Kommentar_tContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String text = ctx.getChild(1).toString();
        text = text.replaceAll("\"", "");
        if (!text.matches(LafDataTypes.MC300)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(text);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> kommentar = new HashMap<String, String>();
        kommentar.put("TEXT", text);
        if (currentMessung == null) {
            currentMessung = data.new Messung();
        }
        currentMessung.addKommentar(kommentar);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenkommentar(LafParser.ProbenkommentarContext ctx) {
        // c5 d8 t4 mc300
        if (ctx.getChildCount() < 8) {
            return;
        }
        String mst = ctx.getChild(1).toString();
        mst = mst.replaceAll("\"", "");
        if (!mst.matches(LafDataTypes.C5)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(mst);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String date = ctx.getChild(3).toString();
        date = date.replaceAll("\"", "");
        if (!date.matches(LafDataTypes.D8)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(date);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String time = ctx.getChild(5).toString();
        time = time.replaceAll("\"", "");
        if (!time.matches(LafDataTypes.T4)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(time);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        String text = ctx.getChild(7).toString();
        text = text.replaceAll("\"", "");
        if (!text.matches(LafDataTypes.MC300)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(text);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> kommentar = new HashMap<String, String>();
        kommentar.put("MST_ID", mst);
        kommentar.put("DATE", date);
        kommentar.put("TIME", time);
        kommentar.put("TEXT", text);
        currentProbe.addKommentar(kommentar);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterProbenkommentar_t(LafParser.Probenkommentar_tContext ctx) {
        if (ctx.getChildCount() < 2) {
            return;
        }
        String text = ctx.getChild(1).toString();
        text = text.replaceAll("\"", "");
        if (!text.matches(LafDataTypes.MC300)) {
            ReportItem err = new ReportItem();
            err.setKey(ctx.getChild(0).toString());
            err.setValue(text);
            err.setCode(670);
            currentErrors.add(err);;
            return;
        }
        Map<String, String> kommentar = new HashMap<String, String>();
        kommentar.put("TEXT", text);
        currentProbe.addKommentar(kommentar);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void enterEveryRule(ParserRuleContext ctx) {
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitEveryRule(ParserRuleContext ctx) {
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void visitTerminal(TerminalNode node) {
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void visitErrorNode(ErrorNode node) {
    }
}
