package de.intevation.lada.exporter;

import java.io.InputStream;
import java.util.List;

import de.intevation.lada.util.auth.UserInfo;

public interface Exporter
{
    public InputStream export(List<Integer> proben, UserInfo userInfo);
}
