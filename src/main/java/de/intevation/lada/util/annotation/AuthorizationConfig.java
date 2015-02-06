package de.intevation.lada.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.intevation.lada.util.auth.AuthorizationType;

@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationConfig {
    AuthorizationType type() default AuthorizationType.NONE;
}
