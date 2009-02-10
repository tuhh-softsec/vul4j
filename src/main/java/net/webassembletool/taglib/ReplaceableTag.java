package net.webassembletool.taglib;

import java.util.Map;

/**
 * Interface for tag that can take regular expression replacements rules using
 * nested Replace tags
 * 
 * @author Cedric Brandes
 * 
 */
public interface ReplaceableTag {

    public abstract Map<String, String> getReplaceRules();
}
