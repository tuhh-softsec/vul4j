package de.intevation.lada.model.query;

/**
 * Container for result configurations.
 * Provides config for the client like column header, column with and data index.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ResultConfig
{
    String dataIndex;
    String header;
    Integer flex;
    Integer width;

    /**
     * Default constructor.
     */
    public ResultConfig() {
    }

    /**
     * @param dataIndex The dataIndex
     * @param header    The column header
     * @param flex      Flexible with
     * @param width     Width in px.
     */
    public ResultConfig(String dataIndex, String header, Integer flex, Integer width) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = flex;
        this.width = width;
    }

    /**
     * @param dataIndex The dataIndex
     * @param header    The column header
     * @param flex      Flexible with
     */
    public ResultConfig(String dataIndex, String header, Integer flex) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = flex;
        this.width = null;
    }

    /**
     * @param dataIndex The dataIndex
     * @param header    The column header
     */
    public ResultConfig(String dataIndex, String header) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = 0;
        this.width = null;
    }

    /**
     * @return the dataIndex
     */
    public String getDataIndex() {
        return dataIndex;
    }

    /**
     * @param dataIndex the dataIndex to set
     */
    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the flex
     */
    public Integer getFlex() {
        return flex;
    }

    /**
     * @param flex the flex to set
     */
    public void setFlex(Integer flex) {
        this.flex = flex;
    }
}
