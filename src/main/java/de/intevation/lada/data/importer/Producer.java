package de.intevation.lada.data.importer;

import java.util.List;
import java.util.Map;

import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LZusatzWert;


public interface Producer
{
    public void addData(String key, Object values)
        throws LAFParserException;
    public LProbe getProbe();
    public List<LMessung> getMessungen();
    public List<LOrt> getOrte();
    public List<LKommentarP> getProbenKommentare();
    public List<LKommentarM> getMessungsKommentare();
    public List<LMesswert> getMesswerte();
    public List<LZusatzWert> getZusatzwerte();
    public void reset();
    public void newMessung();
    public void newOrt();
    public Map<String, List<ReportData>> getErrors();
    public Map<String, List<ReportData>> getWarnings();
}
