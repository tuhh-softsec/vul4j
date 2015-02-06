package de.intevation.lada.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.intevation.lada.util.auth.AuthenticationType;

@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationConfig {
    AuthenticationType type() default AuthenticationType.NONE;
}
