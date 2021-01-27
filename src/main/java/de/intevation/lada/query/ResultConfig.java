/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;


/**
 * Container for result configurations.
 * Provides config for the client like column header, column with
 * and data index.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ResultConfig {
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
     * @param di The dataIndex
     * @param h The column header
     * @param f Flexible with
     * @param w Width in px.
     */
    public ResultConfig(String di, String h, Integer f, Integer w) {
        this.dataIndex = di;
        this.header = h;
        this.flex = f;
        this.width = w;
    }

    /**
     * @param d The dataIndex
     * @param h The column header
     * @param f Flexible with
     */
    public ResultConfig(String d, String h, Integer f) {
        this.dataIndex = d;
        this.header = h;
        this.flex = f;
        this.width = null;
    }

    /**
     * @param d The dataIndex
     * @param h The column header
     */
    public ResultConfig(String d, String h) {
        this.dataIndex = d;
        this.header = h;
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
