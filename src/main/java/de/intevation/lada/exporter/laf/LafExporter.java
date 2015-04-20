/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.laf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

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

    /**
     * The creator used to generate content.
     */
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
