package de.intevation.lada.data.importer;


public class ReportData
{
    private String key;
    private String value;
    private Integer code;

    public ReportData() {
    }

    public ReportData(String key, String value, Integer code) {
        this.key = key;
        this.value = value;
        this.code = code;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }
}
