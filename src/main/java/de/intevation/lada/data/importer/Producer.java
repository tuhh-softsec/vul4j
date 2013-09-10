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
import de.intevation.lada.model.Ort;

/**
 * Defines the interface for producers that create lada entities from
 * key-value pairs.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Producer
{
    public void addData(String key, Object values)
        throws LAFParserException;
    public void finishOrt();
    public LProbe getProbe();
    public List<LMessung> getMessungen();
    public List<LOrt> getLOrte();
    public List<Ort> getOrte();
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
