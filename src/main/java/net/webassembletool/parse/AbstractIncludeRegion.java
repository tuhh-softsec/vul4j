package net.webassembletool.parse;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;

/**
 * Base abstract class for {@linkplain IRegion} implementations like
 * {@linkplain IncludeBlockRegion} and {@linkplain IncludeTemplateRegion}
 * 
 * @author Stanislav Bernatskyi
 */
abstract class AbstractIncludeRegion implements IRegion {
    private final String provider;
    protected final String page;
    protected final String name;
    protected boolean propagateJsessionId;

    protected AbstractIncludeRegion(String provider, String page, String name,
            boolean propagateJsessionId) {
        this.provider = provider;
        this.page = page;
        this.name = name;
        this.propagateJsessionId = propagateJsessionId;
    }

    protected Driver getDriver() {
        return DriverFactory.getInstance(provider);
    }

}
