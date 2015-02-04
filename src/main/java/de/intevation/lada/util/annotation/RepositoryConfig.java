/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.intevation.lada.util.RepositoryType;


@Retention(RetentionPolicy.RUNTIME)
public @interface RepositoryConfig {
    RepositoryType type() default RepositoryType.RO;
    String dataSource() default "";
}
