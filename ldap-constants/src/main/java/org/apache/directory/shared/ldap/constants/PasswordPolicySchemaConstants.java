/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.constants;

/**
 *  PasswordPolicySchemaConstants.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface PasswordPolicySchemaConstants
{
    String PWD_POLICY_OC                    = "pwdPolicy";
    
    String PWD_ATTRIBUTE_AT                 = "pwdAttribute";

    String PWD_LOCKOUT_DURATION_AT          = "pwdLockoutDuration";

    String PWD_MAX_FAILURE_AT               = "pwdMaxFailure";

    String PWD_FAILURE_COUNT_INTERVAL_AT    = "pwdFailureCountInterval";

    String PWD_MUST_CHANGE_AT               = "pwdMustChange";

    String PWD_ALLOW_USER_CHANGE_AT         = "pwdAllowUserChange";

    String PWD_SAFE_MODIFY_AT               = "pwdSafeModify";

    String PWD_CHANGED_TIME_AT              = "pwdChangedTime";

    String PWD_ACCOUNT_LOCKED_TIME_AT       = "pwdAccountLockedTime";

    String PWD_FAILURE_TIME_AT              = "pwdFailureTime";

    String PWD_MIN_AGE_AT                   = "pwdMinAge";

    String PWD_HISTORY_AT                   = "pwdHistory";

    String PWD_GRACE_USE_TIME_AT            = "pwdGraceUseTime";

    String PWD_RESET_AT                     = "pwdReset";

    String PWD_POLICY_SUBENTRY_AT           = "pwdPolicySubentry";

    String PWD_MIN_DELAY_AT                 = "pwdMinDelay";

    String PWD_MAX_DELAY_AT                 = "pwdMaxDelay";

    String PWD_MAX_IDLE_AT                  = "pwdMaxIdle";

    String PWD_START_TIME_AT                = "pwdStartTime";

    String PWD_END_TIME_AT                  = "pwdEndTime";

    String PWD_LAST_SUCCESS_AT              = "pwdLastSuccess";

    String PWD_MAX_AGE_AT                   = "pwdMaxAge";

    String PWD_GRACE_EXPIRE_AT              = "pwdGraceExpire";

    String PWD_MAX_LENGTH_AT                = "pwdMaxLength";

    String PWD_IN_HISTORY_AT                = "pwdInHistory";

    String PWD_CHECK_QUALITY_AT             = "pwdCheckQuality";

    String PWD_MIN_LENGTH_AT                = "pwdMinLength";

    String PWD_EXPIRE_WARNING_AT            = "pwdExpireWarning";

    String PWD_GRACE_AUTHN_LIMIT_AT         = "pwdGraceAuthNLimit";

    String PWD_LOCKOUT_AT                   = "pwdLockout";
}
