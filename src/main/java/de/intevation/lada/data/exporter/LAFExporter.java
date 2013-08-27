package de.intevation.lada.data.exporter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.model.LProbe;

@Named("lafexporter")
public class LAFExporter
implements Exporter
{

    @Inject
    @Named("lafcreator")
    private Creator creator;

    @Override
    public InputStream export(
        String probe,
        AuthenticationResponse auth
    ) {
        String laf = "";
        laf += creator.create(probe);
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
