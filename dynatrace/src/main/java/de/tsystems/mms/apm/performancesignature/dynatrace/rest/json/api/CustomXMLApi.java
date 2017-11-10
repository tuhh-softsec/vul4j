/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.AgentList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.DashboardList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Result;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomXMLApi {
    private ApiClient apiClient;

    public CustomXMLApi() {
        this(Configuration.getDefaultApiClient());
    }

    public CustomXMLApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    private Call getXmlCall(@Nonnull String localVarPath, @Nonnull List<Pair> localVarQueryParams) throws ApiException {
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "text/xml");

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    public DashboardList listDashboards() throws ApiException {
        ApiResponse<DashboardList> resp = listDashboardsWithHttpInfo();
        return resp.getData();
    }

    public ApiResponse<DashboardList> listDashboardsWithHttpInfo() throws ApiException {
        Call call = getXmlCall("/rest/management/dashboards", new ArrayList<Pair>());
        Type localVarReturnType = new TypeToken<DashboardList>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    @SuppressWarnings("rawtypes")
    private Call getXMLDashboardValidateBeforeCall(String dashboard, String source) throws ApiException {
        if (dashboard == null) {
            throw new ApiException("Missing the required parameter 'dashboard' when calling getXMLDashboard");
        }
        if (source == null) {
            throw new ApiException("Missing the required parameter 'source' when calling getXMLDashboard");
        }

        String localVarPath = "/rest/management/dashboard/" + apiClient.escapeString(dashboard);
        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("source", "stored:" + source));

        return getXmlCall(localVarPath, localVarQueryParams);
    }

    public DashboardReport getXMLDashboard(String dashboard, String source) throws ApiException {
        ApiResponse<DashboardReport> resp = getXMLDashboardWithHttpInfo(dashboard, source);
        return resp.getData();
    }

    public ApiResponse<DashboardReport> getXMLDashboardWithHttpInfo(String dashboard, String source) throws ApiException {
        Call call = getXMLDashboardValidateBeforeCall(dashboard, source);
        Type localVarReturnType = new TypeToken<DashboardReport>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public AgentList getAgents() throws ApiException {
        ApiResponse<AgentList> resp = getAgentsWithHttpInfo();
        return resp.getData();
    }

    public ApiResponse<AgentList> getAgentsWithHttpInfo() throws ApiException {
        Call call = getXmlCall("/rest/management/agents", new ArrayList<Pair>());
        Type localVarReturnType = new TypeToken<AgentList>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    @SuppressWarnings("rawtypes")
    private Call hotSensorPlacementValidateBeforeCall(int agentId) throws ApiException {
        if (agentId == 0) {
            throw new ApiException("Missing the required parameter 'agentId' when calling hotSensorPlacement");
        }
        String localVarPath = String.format("/rest/management/agents/%1$s/hotsensorplacement", apiClient.escapeString(String.valueOf(agentId)));

        return getXmlCall(localVarPath, new ArrayList<Pair>());
    }

    public Result hotSensorPlacement(int agentId) throws ApiException {
        ApiResponse<Result> resp = hotSensorPlacementWithHttpInfo(agentId);
        return resp.getData();
    }

    public ApiResponse<Result> hotSensorPlacementWithHttpInfo(int agentId) throws ApiException {
        Call call = hotSensorPlacementValidateBeforeCall(agentId);
        Type localVarReturnType = new TypeToken<Result>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call createMemoryDumpCall(String systemProfile, String agentName, String hostName, int processId, String dumpType, boolean sessionLocked,
                                     boolean captureStrings, boolean capturePrimitives, boolean autoPostProcess, boolean doGC) throws ApiException {
        // create path and map variables
        String localVarPath = String.format("/rest/management/profiles/%1$s/memorydump", apiClient.escapeString(systemProfile));

        List<Pair> localVarQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        if (agentName != null) {
            localVarFormParams.put("agentName", agentName);
        }
        if (hostName != null) {
            localVarFormParams.put("hostName", hostName);
        }
        if (processId != 0) {
            localVarFormParams.put("processId", processId);
        }
        if (dumpType != null) {
            localVarFormParams.put("type", dumpType);
        }

        localVarFormParams.put("isSessionLocked", sessionLocked);
        localVarFormParams.put("capturestrings", captureStrings);
        localVarFormParams.put("captureprimitives", capturePrimitives);
        localVarFormParams.put("autopostprocess", autoPostProcess);
        localVarFormParams.put("dogc", doGC);

        localVarHeaderParams.put("Accept", "text/xml");
        localVarHeaderParams.put("Content-Type", "application/x-www-form-urlencoded");

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call createMemoryDumpValidateBeforeCall(final String systemProfile, final String agentName, final String hostName, final int processId, final String dumpType,
                                                    final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives,
                                                    final boolean autoPostProcess, final boolean doGC) throws ApiException {
        if (systemProfile == null) {
            throw new ApiException("Missing the required parameter 'systemProfile' when calling createMemoryDump");
        }
        if (agentName == null) {
            throw new ApiException("Missing the required parameter 'agentName' when calling createMemoryDump");
        }
        if (hostName == null) {
            throw new ApiException("Missing the required parameter 'hostName' when calling createMemoryDump");
        }
        if (processId == 0) {
            throw new ApiException("Missing the required parameter 'processId' when calling createMemoryDump");
        }
        if (dumpType == null) {
            throw new ApiException("Missing the required parameter 'dumpType' when calling createMemoryDump");
        }

        return createMemoryDumpCall(systemProfile, agentName, hostName, processId, dumpType, sessionLocked, captureStrings, capturePrimitives, autoPostProcess, doGC);
    }

    public Result createMemoryDump(final String systemProfile, final String agentName, final String hostName, final int processId, final String dumpType,
                                   final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives,
                                   final boolean autoPostProcess, final boolean doGC) throws ApiException {
        ApiResponse<Result> resp = createMemoryDumpWithHttpInfo(systemProfile, agentName, hostName, processId, dumpType, sessionLocked, captureStrings,
                capturePrimitives, autoPostProcess, doGC);
        return resp.getData();
    }

    public ApiResponse<Result> createMemoryDumpWithHttpInfo(final String systemProfile, final String agentName, final String hostName, final int processId,
                                                            final String dumpType, final boolean sessionLocked, final boolean captureStrings,
                                                            final boolean capturePrimitives, final boolean autoPostProcess, final boolean doGC) throws ApiException {
        Call call = createMemoryDumpValidateBeforeCall(systemProfile, agentName, hostName, processId, dumpType, sessionLocked, captureStrings,
                capturePrimitives, autoPostProcess, doGC);
        Type localVarReturnType = new TypeToken<Result>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    private Call getMemoryDumpStatusValidateBeforeCall(String profileName, String memoryDumpName) throws ApiException {
        if (profileName == null) {
            throw new ApiException("Missing the required parameter 'profileName' when calling getMemoryDumpStatus");
        }
        if (memoryDumpName == null) {
            throw new ApiException("Missing the required parameter 'memoryDumpName' when calling getMemoryDumpStatus");
        }

        String localVarPath = String.format("/rest/management/profiles/%1$s/memorydumpcreated/%2$s",
                apiClient.escapeString(profileName), apiClient.escapeString(memoryDumpName));

        return getXmlCall(localVarPath, new ArrayList<Pair>());
    }

    public Result getMemoryDumpStatus(String profileName, String memoryDumpName) throws ApiException {
        ApiResponse<Result> resp = getMemoryDumpStatusWithHttpInfo(profileName, memoryDumpName);
        return resp.getData();
    }

    public ApiResponse<Result> getMemoryDumpStatusWithHttpInfo(String profileName, String memoryDumpName) throws ApiException {
        Call call = getMemoryDumpStatusValidateBeforeCall(profileName, memoryDumpName);
        Type localVarReturnType = new TypeToken<Result>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call createThreadDumpCall(final String systemProfile, final String agentName, final String hostName, final int processId,
                                     final boolean sessionLocked) throws ApiException {
        // create path and map variables
        String localVarPath = String.format("/rest/management/profiles/%1$s/threaddump", apiClient.escapeString(systemProfile));

        List<Pair> localVarQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();

        if (agentName != null) {
            localVarFormParams.put("agentName", agentName);
        }
        if (hostName != null) {
            localVarFormParams.put("hostName", hostName);
        }
        if (processId != 0) {
            localVarFormParams.put("processId", processId);
        }
        localVarFormParams.put("isSessionLocked", sessionLocked);

        localVarHeaderParams.put("Accept", "text/xml");
        localVarHeaderParams.put("Content-Type", "application/x-www-form-urlencoded");

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call createThreadDumpValidateBeforeCall(final String systemProfile, final String agentName, final String hostName, final int processId,
                                                    final boolean sessionLocked) throws ApiException {
        if (systemProfile == null) {
            throw new ApiException("Missing the required parameter 'systemProfile' when calling createThreadDump");
        }
        if (agentName == null) {
            throw new ApiException("Missing the required parameter 'agentName' when calling createThreadDump");
        }
        if (hostName == null) {
            throw new ApiException("Missing the required parameter 'hostName' when calling createThreadDump");
        }
        if (processId == 0) {
            throw new ApiException("Missing the required parameter 'processId' when calling createThreadDump");
        }

        return createThreadDumpCall(systemProfile, agentName, hostName, processId, sessionLocked);
    }

    public Result createThreadDump(final String systemProfile, final String agentName, final String hostName, final int processId,
                                   final boolean sessionLocked) throws ApiException {
        ApiResponse<Result> resp = createThreadDumpWithHttpInfo(systemProfile, agentName, hostName, processId, sessionLocked);
        return resp.getData();
    }

    public ApiResponse<Result> createThreadDumpWithHttpInfo(final String systemProfile, final String agentName, final String hostName, final int processId,
                                                            final boolean sessionLocked) throws ApiException {
        Call call = createThreadDumpValidateBeforeCall(systemProfile, agentName, hostName, processId, sessionLocked);
        Type localVarReturnType = new TypeToken<Result>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    private Call getThreadDumpStatusValidateBeforeCall(String profileName, String threadDumpName) throws ApiException {
        if (profileName == null) {
            throw new ApiException("Missing the required parameter 'profileName' when calling getThreadDump");
        }
        if (threadDumpName == null) {
            throw new ApiException("Missing the required parameter 'threadDumpName' when calling getThreadDump");
        }

        String localVarPath = String.format("/rest/management/profiles/%1$s/threaddumpcreated/%2$s",
                apiClient.escapeString(profileName), apiClient.escapeString(threadDumpName));

        return getXmlCall(localVarPath, new ArrayList<Pair>());
    }

    public Result getThreadDumpStatus(String profileName, String threadDumpName) throws ApiException {
        ApiResponse<Result> resp = getThreadDumpStatusWithHttpInfo(profileName, threadDumpName);
        return resp.getData();
    }

    public ApiResponse<Result> getThreadDumpStatusWithHttpInfo(String profileName, String threadDumpName) throws ApiException {
        Call call = getThreadDumpStatusValidateBeforeCall(profileName, threadDumpName);
        Type localVarReturnType = new TypeToken<Result>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call getPDFReportCall(String dashboard, String sessionId, String comparedSessionId, String type) throws ApiException {
        // create path and map variables
        String localVarPath = "/rest/management/reports/create/" + apiClient.escapeString(dashboard);

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (sessionId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("source", "stored:" + sessionId));
        }
        if (comparedSessionId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("compare", "stored:" + comparedSessionId));
        }
        if (type != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("type", type));
        }

        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/octet-stream");

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call getPDFReportValidateBeforeCall(String dashboard, String sessionId, String comparedSessionId, String type) throws ApiException {
        if (dashboard == null) {
            throw new ApiException("Missing the required parameter 'dashboard' when calling getPDFReport");
        }
        if (sessionId == null) {
            throw new ApiException("Missing the required parameter 'sessionid' when calling getPDFReport");
        }
        if (type == null) {
            throw new ApiException("Missing the required parameter 'type' when calling getPDFReport)");
        }

        return getPDFReportCall(dashboard, sessionId, comparedSessionId, type);
    }

    public File getPDFReport(String dashboard, String sessionId, String comparedSessionId, String type) throws ApiException {
        ApiResponse<File> resp = getPDFReportWithHttpInfo(dashboard, sessionId, comparedSessionId, type);
        return resp.getData();
    }

    public ApiResponse<File> getPDFReportWithHttpInfo(String dashboard, String sessionId, String comparedSessionId, String type) throws ApiException {
        Call call = getPDFReportValidateBeforeCall(dashboard, sessionId, comparedSessionId, type);
        Type localVarReturnType = new TypeToken<File>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }
}
