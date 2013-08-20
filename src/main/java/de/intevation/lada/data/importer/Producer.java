package de.intevation.lada.data.importer;

import java.util.List;

import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;


public interface Producer
{
    public void addData(String key, Object values);
    public LProbe getProbe();
    public List<LMessung> getMessungen();
    public List<LOrt> getOrte();
    public List<LKommentarP> getProbenKommentare();
    public List<LKommentarM> getMessungsKommentare();
    public List<LMesswert> getMesswerte();
    public void reset();
    public void newMessung();
    public void newOrt();
}
