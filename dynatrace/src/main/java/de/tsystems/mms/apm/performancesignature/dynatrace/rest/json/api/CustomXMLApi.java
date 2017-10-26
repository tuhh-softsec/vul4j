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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.*;

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

    private Call listDashboardsCall() throws ApiException {
        // create path and map variables
        String localVarPath = "/rest/management/dashboards";

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    public String listDashboards() throws ApiException {
        ApiResponse<String> resp = listDashboardsWithHttpInfo();
        return resp.getData();
    }

    public ApiResponse<String> listDashboardsWithHttpInfo() throws ApiException {
        Call call = listDashboardsCall();
        Type localVarReturnType = new TypeToken<String>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call getXMLDashboardCall(String dashboard, String source) throws ApiException {
        // create path and map variables
        String localVarPath = "/rest/management/dashboard/" + apiClient.escapeString(dashboard);

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (source != null) {
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "source", "stored:" + apiClient.escapeString(source)));
        }

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call getXMLDashboardValidateBeforeCall(String dashboard, String source) throws ApiException {
        if (dashboard == null) {
            throw new ApiException("Missing the required parameter 'dashboard' when calling getXMLDashboard");
        }
        if (source == null) {
            throw new ApiException("Missing the required parameter 'source' when calling getXMLDashboard");
        }

        return getXMLDashboardCall(dashboard, source);
    }

    public String getXMLDashboard(String dashboard, String source) throws ApiException {
        ApiResponse<String> resp = getXMLDashboardWithHttpInfo(dashboard, source);
        return resp.getData();
    }

    public ApiResponse<String> getXMLDashboardWithHttpInfo(String dashboard, String source) throws ApiException {
        Call call = getXMLDashboardValidateBeforeCall(dashboard, source);
        Type localVarReturnType = new TypeToken<String>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call getAgentsCall() throws ApiException {
        // create path and map variables
        String localVarPath = "/rest/management/agents";

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    public String getAgents() throws ApiException {
        ApiResponse<String> resp = getAgentsWithHttpInfo();
        return resp.getData();
    }

    public ApiResponse<String> getAgentsWithHttpInfo() throws ApiException {
        Call call = getAgentsCall();
        Type localVarReturnType = new TypeToken<String>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public Call getPDFReportCall(String dashboard, String sessionId, String comparedSessionId, String type) throws ApiException {
        // create path and map variables
        String localVarPath = "/rest/management/reports/create/" + apiClient.escapeString(dashboard);

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (sessionId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "source", "stored:" + sessionId));
        }
        if (comparedSessionId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "compare", "stored:" + comparedSessionId));
        }
        if (type != null) {
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "type", type));
        }

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/octet-stream"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

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
        if (comparedSessionId == null) {
            throw new ApiException("Missing the required parameter 'comparedSessionId' when calling getPDFReport");
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
