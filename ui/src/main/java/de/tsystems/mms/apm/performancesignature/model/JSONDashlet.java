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

    /**
     * @param aggregation
     * @param id
     * @param chartDashlet
     * @param col
     * @param measure
     * @param description
     * @param dashboard
     * @param row
     */
    public JSONDashlet(final int col, final int row, final String id, final String dashboard, final String chartDashlet, final String measure,
                       final String aggregation, final String description) {
        this.col = col;
        this.row = row;
        this.id = id;
        this.dashboard = dashboard;
        this.chartDashlet = chartDashlet;
        this.measure = measure;
        this.customName = "";
        this.customBuildCount = 0;
        this.show = true;
        this.aggregation = aggregation;
        this.description = description;
    }

    public JSONDashlet(final int col, final int row, final String id, final String dashboard) {
        this(col, row, id, dashboard, "", "", "", "");
    }

    /**
     * @return The col
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col The col
     */
    public void setCol(final int col) {
        this.col = col;
    }

    /**
     * @return The row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row The row
     */
    public void setRow(final int row) {
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
    public void setId(final String id) {
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
    public void setDashboard(final String dashboard) {
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
    public void setChartDashlet(final String chartDashlet) {
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
    public void setMeasure(final String measure) {
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
    public void setCustomName(final String customName) {
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
    public void setCustomBuildCount(final int customBuildCount) {
        this.customBuildCount = customBuildCount;
    }

    /**
     * @return The show
     */
    public boolean isShow() {
        return show;
    }

    /**
     * @param show The show
     */
    public void setShow(final boolean show) {
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
    public void setAggregation(final String aggregation) {
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
    public void setDescription(final String description) {
        this.description = description;
    }
}
