package de.intevation.lada.data;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import de.intevation.lada.rest.Response;

@RequestScoped
@Named("readonlyrepository")
public class ReadOnlyRepository
extends Repository
{

    @Override
    public Response create(Object object) {
        return null;
    }

    @Override
    public Response update(Object object) {
        return null;
    }

    @Override
    public Response filter(Map<String, String> keys) {
        return null;
    }
}
