package de.intevation.lada.util.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import de.intevation.lada.util.annotation.AuthenticationConfig;
import de.intevation.lada.util.auth.Authentication;
import de.intevation.lada.util.auth.AuthenticationType;
import de.intevation.lada.util.auth.DefaultAuthentication;


@ApplicationScoped
public class AuthenticationFactory {

    @Produces
    Authentication createAuthentication(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        AuthenticationConfig config =
            annotated.getAnnotation(AuthenticationConfig.class);
        if (config == null) {
            return new DefaultAuthentication();
        }
        Authentication auth = new DefaultAuthentication();
        if (config.type() == AuthenticationType.NONE) {
            auth = new DefaultAuthentication();
        }
        // TODO Add more Authentication methods.
        return auth;
    }

}
