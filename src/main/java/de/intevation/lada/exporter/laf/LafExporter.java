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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

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
     * The repository used to read data.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * Export the {@link LProbe} objects.
     *
     * @param proben    List of probe ids.
     * @param messungen    List of messung ids.
     * @param auth      The authentication information.
     * @return InputStream with the LAF data.
     */
    @Override
    public InputStream exportProben(
        List<Integer> proben,
        List<Integer> messungen,
        String encoding,
        UserInfo userInfo
    ) {
        String laf = "";
        creator.setUserInfo(userInfo);
        for (Integer probeId: proben) {
            laf += creator.createProbe(probeId.toString());
        }
        for (Integer messungId: messungen) {
            Messung m = repository.getByIdPlain(
                Messung.class, messungId, Strings.LAND);
            List<Integer> mList = new ArrayList<>();
            mList.add(messungId);
            laf += creator.createMessung(
                m.getProbeId().toString(), mList);
        }
        laf += "%ENDE%";
        InputStream in;
        try {
            in = new ByteArrayInputStream(laf.getBytes(encoding));
            in.close();
            return in;
        } catch (IOException e) {
            String resp = "Error - Problem while creating the response";
            InputStream is = new ByteArrayInputStream(resp.getBytes());
            return is;
        }
    }
}
