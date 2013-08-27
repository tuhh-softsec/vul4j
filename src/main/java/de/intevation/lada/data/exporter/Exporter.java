package de.intevation.lada.data.exporter;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import de.intevation.lada.auth.AuthenticationResponse;


public interface Exporter
{
    public InputStream export(String probe, AuthenticationResponse auth);
}
