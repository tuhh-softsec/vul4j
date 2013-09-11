package de.intevation.lada.data.exporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.model.LProbe;

/**
* The LAF exporter implements {@link Exporter} to produce a LAF file.
*
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Named("lafexporter")
public class LAFExporter
implements Exporter
{

    @Inject
    @Named("lafcreator")
    private Creator creator;

    /**
     * Export the {@link LProbe} objects.
     *
     * @param proben    List of probe ids.
     * @param auth      The authentication information.
     * @return InputStream with the LAF data.
     */
    @Override
    public InputStream export(
        List<String> proben,
        AuthenticationResponse auth
    ) {
        String laf = "";
        for (String probeId: proben) {
            laf += creator.create(probeId);
        }
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
