package de.intevation.lada.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

/**
* Response object storing information about success, warnings, errors and
* the data object. This class is used as return value in REST services.
*
* @author <a href="mailto:torsten@intevation.de">Torsten Irländer</a>
*/
@SuppressWarnings("serial")
public class Response implements java.io.Serializable {


    /**
     * The logger for this class.
     */
    @Inject
    private Logger log;

    private Boolean success;
    private String message;
    private Object data;
    private Map<String, String> errors;
    private Map<String, String> warnings;
    private Boolean readonly;

    /**
     * Constructor to create a basic Response object.
     *
     * @param success   Information if the operation was successful.
     * @param code      The return code.
     * @param data      The data object wrapped by the response.
     */
    public Response(boolean success, int code, Object data) {
        this.success = success;
        this.message = Integer.toString(code);
        this.data = data;
        this.errors = new HashMap<String, String>();
        this.warnings = new HashMap<String, String>();
        this.readonly = Boolean.FALSE;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = Integer.toString(message);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Integer> errors) {
        this.errors = this.convertCodes(errors);
    }

    public Map<String, String> getWarnings() {
        return warnings;
    }

    public void setWarnings(Map<String, Integer> warnings) {
        this.warnings = this.convertCodes(warnings);
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    private HashMap<String, String> convertCodes(Map<String, Integer> codes) {
        HashMap<String, String> converted = new HashMap<String, String>();
        if (codes == null || codes.isEmpty()) {
            return converted;
        }
        for (Map.Entry<String, Integer> entry: codes.entrySet()) {
            converted.put(entry.getKey(), Integer.toString(entry.getValue()));
        }
        return converted;
    }

    /* Currently unused but might be helpfull later */
    private String codes2string(Map<String, Integer> codes) {
        String response = "{";
        if (codes == null || codes.isEmpty()) {
            response += "}";
            return response;
        }
        boolean first = true;
        for (Map.Entry<String, Integer> entry: codes.entrySet()) {
            if (!first) {
                response +=",";
            }
            response += entry.getKey() + ":" + "\"" + entry.getValue() + "\"";
            first = false;
        }
        response += "}";
        return response;
    }
}
