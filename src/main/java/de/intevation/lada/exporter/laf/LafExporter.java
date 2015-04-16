package de.intevation.lada.exporter.laf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.util.auth.UserInfo;

/**
* The LAF exporter implements {@link Exporter} to produce a LAF file.
*
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@ExportConfig(format=ExportFormat.LAF)
public class LafExporter
implements Exporter
{
    @Inject
    private Logger logger;

    @Inject
    private LafCreator creator;

    /**
     * Export the {@link LProbe} objects.
     *
     * @param proben    List of probe ids.
     * @param auth      The authentication information.
     * @return InputStream with the LAF data.
     */
    @Override
    public InputStream export(
        List<Integer> proben,
        UserInfo userInfo
    ) {
        String laf = "";
        for (Integer probeId: proben) {
            laf += creator.create(probeId.toString());
        }
        laf += "%ENDE%";
        InputStream in = new ByteArrayInputStream(laf.getBytes());
        try {
            in.close();
        }
        catch (IOException e) {
            //TODO Exception handling.
        }
        return in;
    }
}
