package de.intevation.lada.data.importer;

import java.util.List;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.Ort;


public interface Writer
{
    public boolean writeProbe(AuthenticationResponse auth, LProbe probe);
    public boolean writeMessungen(
        AuthenticationResponse auth,
        List<LMessung> messungen);
    public boolean writeLOrte(AuthenticationResponse auth, List<LOrt> orte);
    public boolean writeOrte(AuthenticationResponse auth, List<Ort> orte);
    public boolean writeProbenKommentare(
        AuthenticationResponse auth,
        List<LKommentarP> kommentare);
    public boolean writeMessungKommentare(
        AuthenticationResponse auth,
        List<LKommentarM> kommentare);
    public boolean writeMesswerte(
        AuthenticationResponse auth,
        List<LMesswert> werte);
    public List<ReportData> getErrors();
    public List<ReportData> getWarnings();
    public void reset();
}
