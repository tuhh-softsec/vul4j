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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.ActivationStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfile;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfileConfigurations;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfiles;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemProfilesApi {
    private ApiClient apiClient;

    public SystemProfilesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SystemProfilesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for getProfiles
     *
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getProfilesCall() throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles";

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

    /**
     * List System Profiles
     * Get a list of all System Profiles of the AppMon Server.
     *
     * @return SystemProfiles
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SystemProfiles getProfiles() throws ApiException {
        ApiResponse<SystemProfiles> resp = getProfilesWithHttpInfo();
        return resp.getData();
    }

    /**
     * List System Profiles
     * Get a list of all System Profiles of the AppMon Server.
     *
     * @return ApiResponse&lt;SystemProfiles&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SystemProfiles> getProfilesWithHttpInfo() throws ApiException {
        Call call = getProfilesCall();
        Type localVarReturnType = new TypeToken<SystemProfiles>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getSystemProfileConfigurationStatus
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getSystemProfileConfigurationStatusCall(String profileid, String configname) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/configurations/{configname}/status"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid))
                .replaceAll("\\{configname\\}", apiClient.escapeString(configname));

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
    private Call getSystemProfileConfigurationStatusValidateBeforeCall(String profileid, String configname) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling getSystemProfileConfigurationStatus");
        }

        // verify the required parameter 'configname' is set
        if (configname == null) {
            throw new ApiException("Missing the required parameter 'configname' when calling getSystemProfileConfigurationStatus");
        }

        return getSystemProfileConfigurationStatusCall(profileid, configname);
    }

    /**
     * Activation status of System Profile configuration
     * Retrieve the activation state of a System Profile configuration.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @return ActivationStatus
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ActivationStatus getSystemProfileConfigurationStatus(String profileid, String configname) throws ApiException {
        ApiResponse<ActivationStatus> resp = getSystemProfileConfigurationStatusWithHttpInfo(profileid, configname);
        return resp.getData();
    }

    /**
     * Activation status of System Profile configuration
     * Retrieve the activation state of a System Profile configuration.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @return ApiResponse&lt;ActivationStatus&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<ActivationStatus> getSystemProfileConfigurationStatusWithHttpInfo(String profileid, String configname) throws ApiException {
        Call call = getSystemProfileConfigurationStatusValidateBeforeCall(profileid, configname);
        Type localVarReturnType = new TypeToken<ActivationStatus>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getSystemProfileConfigurations
     *
     * @param profileid System Profile id (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getSystemProfileConfigurationsCall(String profileid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/configurations"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid));

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
    private Call getSystemProfileConfigurationsValidateBeforeCall(String profileid) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling getSystemProfileConfigurations");
        }

        return getSystemProfileConfigurationsCall(profileid);
    }

    /**
     * List System Profile configurations
     * Get a list of all configurations of the specified System Profile.
     *
     * @param profileid System Profile id (required)
     * @return SystemProfileConfigurations
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SystemProfileConfigurations getSystemProfileConfigurations(String profileid) throws ApiException {
        ApiResponse<SystemProfileConfigurations> resp = getSystemProfileConfigurationsWithHttpInfo(profileid);
        return resp.getData();
    }

    /**
     * List System Profile configurations
     * Get a list of all configurations of the specified System Profile.
     *
     * @param profileid System Profile id (required)
     * @return ApiResponse&lt;SystemProfileConfigurations&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SystemProfileConfigurations> getSystemProfileConfigurationsWithHttpInfo(String profileid) throws ApiException {
        Call call = getSystemProfileConfigurationsValidateBeforeCall(profileid);
        Type localVarReturnType = new TypeToken<SystemProfileConfigurations>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getSystemProfileMetaData
     *
     * @param profileid System Profile id (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getSystemProfileMetaDataCall(String profileid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid));

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
    private Call getSystemProfileMetaDataValidateBeforeCall(String profileid) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling getSystemProfileMetaData");
        }

        return getSystemProfileMetaDataCall(profileid);
    }

    /**
     * System Profile Metadata
     * Get a JSON representation describing the System Profile and its meta data.
     *
     * @param profileid System Profile id (required)
     * @return SystemProfile
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SystemProfile getSystemProfileMetaData(String profileid) throws ApiException {
        ApiResponse<SystemProfile> resp = getSystemProfileMetaDataWithHttpInfo(profileid);
        return resp.getData();
    }

    /**
     * System Profile Metadata
     * Get a JSON representation describing the System Profile and its meta data.
     *
     * @param profileid System Profile id (required)
     * @return ApiResponse&lt;SystemProfile&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SystemProfile> getSystemProfileMetaDataWithHttpInfo(String profileid) throws ApiException {
        Call call = getSystemProfileMetaDataValidateBeforeCall(profileid);
        Type localVarReturnType = new TypeToken<SystemProfile>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getSystemProfileState
     *
     * @param profileid System Profile id (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getSystemProfileStateCall(String profileid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/status"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid));

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
    private Call getSystemProfileStateValidateBeforeCall(String profileid) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling getSystemProfileState");
        }

        return getSystemProfileStateCall(profileid);
    }

    /**
     * Activation status of System Profile
     * Retrieve the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @return ActivationStatus
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ActivationStatus getSystemProfileState(String profileid) throws ApiException {
        ApiResponse<ActivationStatus> resp = getSystemProfileStateWithHttpInfo(profileid);
        return resp.getData();
    }

    /**
     * Activation status of System Profile
     * Retrieve the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @return ApiResponse&lt;ActivationStatus&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<ActivationStatus> getSystemProfileStateWithHttpInfo(String profileid) throws ApiException {
        Call call = getSystemProfileStateValidateBeforeCall(profileid);
        Type localVarReturnType = new TypeToken<ActivationStatus>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for putSystemProfileConfigurationStatus
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @param body       Activation state (optional)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call putSystemProfileConfigurationStatusCall(String profileid, String configname, ActivationStatus body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/configurations/{configname}/status"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid))
                .replaceAll("\\{configname\\}", apiClient.escapeString(configname));

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
    private Call putSystemProfileConfigurationStatusValidateBeforeCall(String profileid, String configname, ActivationStatus body) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling putSystemProfileConfigurationStatus");
        }

        // verify the required parameter 'configname' is set
        if (configname == null) {
            throw new ApiException("Missing the required parameter 'configname' when calling putSystemProfileConfigurationStatus");
        }

        return putSystemProfileConfigurationStatusCall(profileid, configname, body);
    }

    /**
     * Activate System Profile configuration
     * Change the activation state of a System Profile. Activating a configuration automatically sets all other configurations to DISABLED. Manually setting the activation state to DISABLED via this call is not allowed.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @param body       Activation state (optional)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void putSystemProfileConfigurationStatus(String profileid, String configname, ActivationStatus body) throws ApiException {
        putSystemProfileConfigurationStatusWithHttpInfo(profileid, configname, body);
    }

    /**
     * Activate System Profile configuration
     * Change the activation state of a System Profile. Activating a configuration automatically sets all other configurations to DISABLED. Manually setting the activation state to DISABLED via this call is not allowed.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @param body       Activation state (optional)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> putSystemProfileConfigurationStatusWithHttpInfo(String profileid, String configname, ActivationStatus body) throws ApiException {
        Call call = putSystemProfileConfigurationStatusValidateBeforeCall(profileid, configname, body);
        return apiClient.execute(call);
    }

    /**
     * Build call for putSystemProfileState
     *
     * @param profileid System Profile id (required)
     * @param body      Activation state (optional)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call putSystemProfileStateCall(String profileid, ActivationStatus body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/status"
                .replaceAll("\\{profileid\\}", apiClient.escapeString(profileid));

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
    private Call putSystemProfileStateValidateBeforeCall(String profileid, ActivationStatus body) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling putSystemProfileState");
        }

        return putSystemProfileStateCall(profileid, body);
    }

    /**
     * Enable/disable System Profile
     * Change the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @param body      Activation state (optional)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void putSystemProfileState(String profileid, ActivationStatus body) throws ApiException {
        putSystemProfileStateWithHttpInfo(profileid, body);
    }

    /**
     * Enable/disable System Profile
     * Change the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @param body      Activation state (optional)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> putSystemProfileStateWithHttpInfo(String profileid, ActivationStatus body) throws ApiException {
        Call call = putSystemProfileStateValidateBeforeCall(profileid, body);
        return apiClient.execute(call);
    }
}
