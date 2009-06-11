package net.webassembletool.taglib;

import java.util.Map;

/**
 * Interface for JSP tags which are capable of error management.
 * 
 * @author Cedric BRANDES
 */
public interface ErrorManageableTag {
    public abstract Map<Integer, String> getErrorMap();

    public abstract String getDefaultMessage();

    public abstract void setDefaultMessage(String errorMessage);
}
