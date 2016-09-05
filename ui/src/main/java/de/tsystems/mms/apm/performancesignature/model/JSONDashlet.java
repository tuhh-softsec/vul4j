package de.tsystems.mms.apm.performancesignature.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONDashlet {

    @SerializedName("col")
    @Expose
    private int col;
    @SerializedName("row")
    @Expose
    private int row;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("dashboard")
    @Expose
    private String dashboard;
    @SerializedName("chartDashlet")
    @Expose
    private String chartDashlet;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("customName")
    @Expose
    private String customName;
    @SerializedName("customBuildCount")
    @Expose
    private int customBuildCount;
    @SerializedName("show")
    @Expose
    private boolean show;
    @SerializedName("aggregation")
    @Expose
    private String aggregation;
    @SerializedName("description")
    @Expose
    private String description;

    public JSONDashlet(final int col, final int row, final String id, final String dashboard, final String chartDashlet, final String measure,
                       final String customName, final int customBuildCount, final boolean show, final String aggregation, final String description) {
        this.col = col;
        this.row = row;
        this.id = id;
        this.dashboard = dashboard;
        this.chartDashlet = chartDashlet;
        this.measure = measure;
        this.customName = customName;
        this.customBuildCount = customBuildCount;
        this.show = show;
        this.aggregation = aggregation;
        this.description = description;
    }

    public JSONDashlet(final int col, final int row, final String id, final String dashboard) {
        this(col, row, id, dashboard, "", "", "", 0, true, "", "");
    }

    /**
     * @return The col
     */
    public Integer getCol() {
        return col;
    }

    /**
     * @param col The col
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * @return The row
     */
    public Integer getRow() {
        return row;
    }

    /**
     * @param row The row
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The dashboard
     */
    public String getDashboard() {
        return dashboard;
    }

    /**
     * @param dashboard The dashboard
     */
    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    /**
     * @return The chartDashlet
     */
    public String getChartDashlet() {
        return chartDashlet;
    }

    /**
     * @param chartDashlet The chartDashlet
     */
    public void setChartDashlet(String chartDashlet) {
        this.chartDashlet = chartDashlet;
    }

    /**
     * @return The measure
     */
    public String getMeasure() {
        return measure;
    }

    /**
     * @param measure The measure
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * @return The customName
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * @param customName The customName
     */
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    /**
     * @return The customBuildCount
     */
    public int getCustomBuildCount() {
        return customBuildCount;
    }

    /**
     * @param customBuildCount The customBuildCount
     */
    public void setCustomBuildCount(int customBuildCount) {
        this.customBuildCount = customBuildCount;
    }

    /**
     * @return The show
     */
    public Boolean getShow() {
        return show;
    }

    /**
     * @param show The show
     */
    public void setShow(Boolean show) {
        this.show = show;
    }

    /**
     * @return The aggregation
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation The aggregation
     */
    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
