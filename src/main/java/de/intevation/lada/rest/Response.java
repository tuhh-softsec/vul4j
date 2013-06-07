package de.intevation.lada.rest;

/**
* This class is nice!.
*
* @author <a href="mailto:torsten@intevation.de">Torsten Irländer</a>
*/
@SuppressWarnings("serial")
public class Response implements java.io.Serializable {

    private Boolean success;
    private String message;
    private Object data;
    private String errors;
    private String warnings;

    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }
}
