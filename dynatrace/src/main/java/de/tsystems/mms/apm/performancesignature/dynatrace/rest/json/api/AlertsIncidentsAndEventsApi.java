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
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsIncidentsAndEventsApi {
    private ApiClient apiClient;

    public AlertsIncidentsAndEventsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public AlertsIncidentsAndEventsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for createDeploymentEvent
     *
     * @param body Event record (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call createDeploymentEventCall(DeploymentEvent body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/events/Deployment";

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
                "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, body, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call createDeploymentEventValidateBeforeCall(DeploymentEvent body) throws ApiException {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling createDeploymentEvent");
        }

        return createDeploymentEventCall(body);
    }

    /**
     * Create deployment event
     * Create an deployment event for a System Profile. The request must contain the event as JSON representation. If the request does not contain a start and end date, the current server time will be used. The default severity is &#39;informational&#39; and the default state is &#39;Created&#39;.  Events with a severity of &#39;informational&#39; are automatically set to state &#39;Confirmed&#39;. You can set such events to other states with a subsequent update.  It is possible to specify the start date and leave the end date unset, the end date can then be provided later with an update.  At least the JSON properties &#39;systemprofile&#39; and &#39;message&#39; have to be specified.
     *
     * @param body Event record (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void createDeploymentEvent(DeploymentEvent body) throws ApiException {
        createDeploymentEventWithHttpInfo(body);
    }

    /**
     * Create deployment event
     * Create an deployment event for a System Profile. The request must contain the event as JSON representation. If the request does not contain a start and end date, the current server time will be used. The default severity is &#39;informational&#39; and the default state is &#39;Created&#39;.  Events with a severity of &#39;informational&#39; are automatically set to state &#39;Confirmed&#39;. You can set such events to other states with a subsequent update.  It is possible to specify the start date and leave the end date unset, the end date can then be provided later with an update.  At least the JSON properties &#39;systemprofile&#39; and &#39;message&#39; have to be specified.
     *
     * @param body Event record (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> createDeploymentEventWithHttpInfo(DeploymentEvent body) throws ApiException {
        Call call = createDeploymentEventValidateBeforeCall(body);
        return apiClient.execute(call);
    }

    /**
     * Build call for deleteDeploymentEvent
     *
     * @param eventid ID of event (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call deleteDeploymentEventCall(String eventid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/events/Deployment/{eventid}"
                .replaceAll("\\{eventid\\}", apiClient.escapeString(eventid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call deleteDeploymentEventValidateBeforeCall(String eventid) throws ApiException {
        // verify the required parameter 'eventid' is set
        if (eventid == null) {
            throw new ApiException("Missing the required parameter 'eventid' when calling deleteDeploymentEvent");
        }

        return deleteDeploymentEventCall(eventid);
    }

    /**
     * Delete deployment event record
     * Permanently delete a deployment event record.
     *
     * @param eventid ID of event (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void deleteDeploymentEvent(String eventid) throws ApiException {
        deleteDeploymentEventWithHttpInfo(eventid);
    }

    /**
     * Delete deployment event record
     * Permanently delete a deployment event record.
     *
     * @param eventid ID of event (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> deleteDeploymentEventWithHttpInfo(String eventid) throws ApiException {
        Call call = deleteDeploymentEventValidateBeforeCall(eventid);
        return apiClient.execute(call);
    }

    /**
     * Build call for deleteDowntime
     *
     * @param suppressionname Alert Suppression name (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call deleteDowntimeCall(String suppressionname) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alertsuppression/{suppressionname}"
                .replaceAll("\\{suppressionname\\}", apiClient.escapeString(suppressionname));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams, null, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call deleteDowntimeValidateBeforeCall(String suppressionname) throws ApiException {
        // verify the required parameter 'suppressionname' is set
        if (suppressionname == null) {
            throw new ApiException("Missing the required parameter 'suppressionname' when calling deleteDowntime");
        }

        return deleteDowntimeCall(suppressionname);
    }

    /**
     * Delete Alert Suppression
     * Deletes an existing alert suppression (incident downtime).
     *
     * @param suppressionname Alert Suppression name (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void deleteDowntime(String suppressionname) throws ApiException {
        deleteDowntimeWithHttpInfo(suppressionname);
    }

    /**
     * Delete Alert Suppression
     * Deletes an existing alert suppression (incident downtime).
     *
     * @param suppressionname Alert Suppression name (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> deleteDowntimeWithHttpInfo(String suppressionname) throws ApiException {
        Call call = deleteDowntimeValidateBeforeCall(suppressionname);
        return apiClient.execute(call);
    }

    /**
     * Build call for getDeploymentEvent
     *
     * @param eventid ID of event (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getDeploymentEventCall(String eventid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/events/Deployment/{eventid}"
                .replaceAll("\\{eventid\\}", apiClient.escapeString(eventid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
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
    private Call getDeploymentEventValidateBeforeCall(String eventid) throws ApiException {
        // verify the required parameter 'eventid' is set
        if (eventid == null) {
            throw new ApiException("Missing the required parameter 'eventid' when calling getDeploymentEvent");
        }

        return getDeploymentEventCall(eventid);
    }

    /**
     * Get deployment event record
     * Get the JSON representation of a deployment event.
     *
     * @param eventid ID of event (required)
     * @return DeploymentEvent
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public DeploymentEvent getDeploymentEvent(String eventid) throws ApiException {
        ApiResponse<DeploymentEvent> resp = getDeploymentEventWithHttpInfo(eventid);
        return resp.getData();
    }

    /**
     * Get deployment event record
     * Get the JSON representation of a deployment event.
     *
     * @param eventid ID of event (required)
     * @return ApiResponse&lt;DeploymentEvent&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<DeploymentEvent> getDeploymentEventWithHttpInfo(String eventid) throws ApiException {
        Call call = getDeploymentEventValidateBeforeCall(eventid);
        Type localVarReturnType = new TypeToken<DeploymentEvent>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getDowntime
     *
     * @param suppressionname Alert Suppression name (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getDowntimeCall(String suppressionname) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alertsuppression/{suppressionname}"
                .replaceAll("\\{suppressionname\\}", apiClient.escapeString(suppressionname));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
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
    private Call getDowntimeValidateBeforeCall(String suppressionname) throws ApiException {
        // verify the required parameter 'suppressionname' is set
        if (suppressionname == null) {
            throw new ApiException("Missing the required parameter 'suppressionname' when calling getDowntime");
        }

        return getDowntimeCall(suppressionname);
    }

    /**
     * Get Alert Suppression
     * Gets the JSON representation of an existing alert suppression (incident downtime).
     *
     * @param suppressionname Alert Suppression name (required)
     * @return AlertSuppression
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public AlertSuppression getDowntime(String suppressionname) throws ApiException {
        ApiResponse<AlertSuppression> resp = getDowntimeWithHttpInfo(suppressionname);
        return resp.getData();
    }

    /**
     * Get Alert Suppression
     * Gets the JSON representation of an existing alert suppression (incident downtime).
     *
     * @param suppressionname Alert Suppression name (required)
     * @return ApiResponse&lt;AlertSuppression&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<AlertSuppression> getDowntimeWithHttpInfo(String suppressionname) throws ApiException {
        Call call = getDowntimeValidateBeforeCall(suppressionname);
        Type localVarReturnType = new TypeToken<AlertSuppression>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getDowntimes
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getDowntimesCall(String systemprofile, String incidentrule) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alertsuppression";

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (systemprofile != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "systemprofile", systemprofile));
        if (incidentrule != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "incidentrule", incidentrule));

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
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
    private Call getDowntimesValidateBeforeCall(String systemprofile, String incidentrule) throws ApiException {
        return getDowntimesCall(systemprofile, incidentrule);
    }

    /**
     * List Alert Suppressions
     * Get a list of all existing alert suppressions (incident downtimes). The response can optionally be filtered by either a System Profile or a combination of System Profile and Incident rule. The response contains the URL and the name of each alert suppression.
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @return AlertSuppressions
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public AlertSuppressions getDowntimes(String systemprofile, String incidentrule) throws ApiException {
        ApiResponse<AlertSuppressions> resp = getDowntimesWithHttpInfo(systemprofile, incidentrule);
        return resp.getData();
    }

    /**
     * List Alert Suppressions
     * Get a list of all existing alert suppressions (incident downtimes). The response can optionally be filtered by either a System Profile or a combination of System Profile and Incident rule. The response contains the URL and the name of each alert suppression.
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @return ApiResponse&lt;AlertSuppressions&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<AlertSuppressions> getDowntimesWithHttpInfo(String systemprofile, String incidentrule) throws ApiException {
        Call call = getDowntimesValidateBeforeCall(systemprofile, incidentrule);
        Type localVarReturnType = new TypeToken<AlertSuppressions>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getIncident
     *
     * @param alertid ID of alert (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getIncidentCall(String alertid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alerts/{alertid}"
                .replaceAll("\\{alertid\\}", apiClient.escapeString(alertid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
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
    private Call getIncidentValidateBeforeCall(String alertid) throws ApiException {
        // verify the required parameter 'alertid' is set
        if (alertid == null) {
            throw new ApiException("Missing the required parameter 'alertid' when calling getIncident");
        }

        return getIncidentCall(alertid);
    }

    /**
     * Get Alert record
     * Get the JSON representation of an alert (incident).
     *
     * @param alertid ID of alert (required)
     * @return Alert
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Alert getIncident(String alertid) throws ApiException {
        ApiResponse<Alert> resp = getIncidentWithHttpInfo(alertid);
        return resp.getData();
    }

    /**
     * Get Alert record
     * Get the JSON representation of an alert (incident).
     *
     * @param alertid ID of alert (required)
     * @return ApiResponse&lt;Alert&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Alert> getIncidentWithHttpInfo(String alertid) throws ApiException {
        Call call = getIncidentValidateBeforeCall(alertid);
        Type localVarReturnType = new TypeToken<Alert>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getIncidents
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @param state         Alert state (optional)
     * @param from          Minimum start date of the alert (ISO8601) (optional)
     * @param to            Maximum end date of the alert (ISO8601) (optional)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getIncidentsCall(String systemprofile, String incidentrule, String state, String from, String to) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alerts";

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (systemprofile != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "systemprofile", systemprofile));
        if (incidentrule != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "incidentrule", incidentrule));
        if (state != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "state", state));
        if (from != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "from", from));
        if (to != null)
            localVarQueryParams.addAll(apiClient.parameterToPairs("", "to", to));

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
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
    private Call getIncidentsValidateBeforeCall(String systemprofile, String incidentrule, String state, String from, String to) throws ApiException {
        return getIncidentsCall(systemprofile, incidentrule, state, from, to);
    }

    /**
     * List Alerts
     * Get a list of all alerts (incidents) that match the filter settings. If no start and end date is specified, a default time frame of three days is selected.
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @param state         Alert state (optional)
     * @param from          Minimum start date of the alert (ISO8601) (optional)
     * @param to            Maximum end date of the alert (ISO8601) (optional)
     * @return Alerts
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Alerts getIncidents(String systemprofile, String incidentrule, String state, String from, String to) throws ApiException {
        ApiResponse<Alerts> resp = getIncidentsWithHttpInfo(systemprofile, incidentrule, state, from, to);
        return resp.getData();
    }

    /**
     * List Alerts
     * Get a list of all alerts (incidents) that match the filter settings. If no start and end date is specified, a default time frame of three days is selected.
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @param state         Alert state (optional)
     * @param from          Minimum start date of the alert (ISO8601) (optional)
     * @param to            Maximum end date of the alert (ISO8601) (optional)
     * @return ApiResponse&lt;Alerts&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Alerts> getIncidentsWithHttpInfo(String systemprofile, String incidentrule, String state, String from, String to) throws ApiException {
        Call call = getIncidentsValidateBeforeCall(systemprofile, incidentrule, state, from, to);
        Type localVarReturnType = new TypeToken<Alerts>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for putDowntime
     *
     * @param suppressionname Alert Suppression name (required)
     * @param body            Alert Suppression object (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call putDowntimeCall(String suppressionname, AlertSuppressionDefinition body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/alertsuppression/{suppressionname}"
                .replaceAll("\\{suppressionname\\}", apiClient.escapeString(suppressionname));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
                "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "PUT", localVarQueryParams, body, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call putDowntimeValidateBeforeCall(String suppressionname, AlertSuppressionDefinition body) throws ApiException {
        // verify the required parameter 'suppressionname' is set
        if (suppressionname == null) {
            throw new ApiException("Missing the required parameter 'suppressionname' when calling putDowntime");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling putDowntime");
        }

        return putDowntimeCall(suppressionname, body);
    }

    /**
     * Create or replace Alert Suppression
     * Create an alert suppression (incident downtime) by sending a JSON representation as the request body. If an alert suppression with the given name already exists, it will be replaced by the newly created alert suppression.   You can either create a one-time suppression by setting the JSON property &#39;once&#39; to &#39;true&#39;, or create a repeatedly scheduled suppression by providing either a Quartz cron expression or a reference to business hours defined in the self-monitoring System Profile. Referencing business hours means the alert suppression will be active outside the defined business hours. Setting a duration will have no effect if business hours are referenced.  You can create a global alert suppression (&#x3D; affecting all existing and future System Profiles and Incident rules) by leaving System Profiles and Incident rules unspecified.
     *
     * @param suppressionname Alert Suppression name (required)
     * @param body            Alert Suppression object (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void putDowntime(String suppressionname, AlertSuppressionDefinition body) throws ApiException {
        putDowntimeWithHttpInfo(suppressionname, body);
    }

    /**
     * Create or replace Alert Suppression
     * Create an alert suppression (incident downtime) by sending a JSON representation as the request body. If an alert suppression with the given name already exists, it will be replaced by the newly created alert suppression.   You can either create a one-time suppression by setting the JSON property &#39;once&#39; to &#39;true&#39;, or create a repeatedly scheduled suppression by providing either a Quartz cron expression or a reference to business hours defined in the self-monitoring System Profile. Referencing business hours means the alert suppression will be active outside the defined business hours. Setting a duration will have no effect if business hours are referenced.  You can create a global alert suppression (&#x3D; affecting all existing and future System Profiles and Incident rules) by leaving System Profiles and Incident rules unspecified.
     *
     * @param suppressionname Alert Suppression name (required)
     * @param body            Alert Suppression object (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> putDowntimeWithHttpInfo(String suppressionname, AlertSuppressionDefinition body) throws ApiException {
        Call call = putDowntimeValidateBeforeCall(suppressionname, body);
        return apiClient.execute(call);
    }

    /**
     * Build call for updateDeploymentEvent
     *
     * @param eventid ID of event (required)
     * @param body    Event record (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call updateDeploymentEventCall(String eventid, EventUpdate body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/events/Deployment/{eventid}"
                .replaceAll("\\{eventid\\}", apiClient.escapeString(eventid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
                "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{"basicAuth"};
        return apiClient.buildCall(localVarPath, "PUT", localVarQueryParams, body, localVarHeaderParams, localVarFormParams, localVarAuthNames);
    }

    @SuppressWarnings("rawtypes")
    private Call updateDeploymentEventValidateBeforeCall(String eventid, EventUpdate body) throws ApiException {
        // verify the required parameter 'eventid' is set
        if (eventid == null) {
            throw new ApiException("Missing the required parameter 'eventid' when calling updateDeploymentEvent");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling updateDeploymentEvent");
        }

        return updateDeploymentEventCall(eventid, body);
    }

    /**
     * Update deployment event record
     * Several attributes of a deployment event can be modified by updating it. You can either retrieve the event record via the GET call first and then send the modified JSON object, or you could make a partial update by providing only the properties that should get updated.
     *
     * @param eventid ID of event (required)
     * @param body    Event record (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void updateDeploymentEvent(String eventid, EventUpdate body) throws ApiException {
        updateDeploymentEventWithHttpInfo(eventid, body);
    }

    /**
     * Update deployment event record
     * Several attributes of a deployment event can be modified by updating it. You can either retrieve the event record via the GET call first and then send the modified JSON object, or you could make a partial update by providing only the properties that should get updated.
     *
     * @param eventid ID of event (required)
     * @param body    Event record (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> updateDeploymentEventWithHttpInfo(String eventid, EventUpdate body) throws ApiException {
        Call call = updateDeploymentEventValidateBeforeCall(eventid, body);
        return apiClient.execute(call);
    }
}
