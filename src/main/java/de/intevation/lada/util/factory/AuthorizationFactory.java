package de.intevation.lada.util.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.DefaultAuthorization;

@ApplicationScoped
public class AuthorizationFactory {

    @Produces
    Authorization createAuthorization(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        AuthorizationConfig config =
            annotated.getAnnotation(AuthorizationConfig.class);
        if (config == null) {
            return new DefaultAuthorization();
        }
        Authorization auth = new DefaultAuthorization();
        if (config.type() == AuthorizationType.NONE) {
            auth = new DefaultAuthorization();
        }

        return auth;
    }
}
