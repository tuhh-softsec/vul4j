package net.webassembletool.taglib;

import java.util.Map;

/**
 * Interface for tags that can take additional parameters that can be included
 * in the request using nested Parameter tags
 * 
 * @author Cedric Brandes
 * 
 */
public interface IParameterTag {
    public abstract Map<String, String> getParameters();

}
