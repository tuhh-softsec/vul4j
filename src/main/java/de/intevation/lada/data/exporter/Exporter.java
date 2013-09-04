package de.intevation.lada.data.exporter;

import java.io.InputStream;
import java.util.List;

import de.intevation.lada.auth.AuthenticationResponse;


public interface Exporter
{
    public InputStream export(List<String> proben, AuthenticationResponse auth);
}
