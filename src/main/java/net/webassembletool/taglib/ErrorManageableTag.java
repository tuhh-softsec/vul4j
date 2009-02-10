package net.webassembletool.taglib;

import java.util.Map;

/**
 * TODO Type javadoc
 * 
 * @author Cedric BRANDES
 */
public interface ErrorManageableTag {
    public abstract Map<Integer, String> getErrorMap();

    public abstract String getDefaultMessage();

    public abstract void setDefaultMessage(String errorMessage);
}
