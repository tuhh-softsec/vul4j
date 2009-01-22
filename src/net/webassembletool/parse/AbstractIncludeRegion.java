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

    protected AbstractIncludeRegion(String provider, String page, String name) {
	this.provider = provider;
	this.page = page;
	this.name = name;
    }

    protected Driver getDriver() {
	return DriverFactory.getInstance(provider);
    }

}
