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

package org.apache.directory.shared.ldap.model.constants;

/**
 * PasswordPolicySchemaConstants.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class PasswordPolicySchemaConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private PasswordPolicySchemaConstants()
    {
    }

    public final static String PWD_POLICY_OC                    = "pwdPolicy";
    
    public final static String PWD_ATTRIBUTE_AT                 = "pwdAttribute";

    public final static String PWD_LOCKOUT_DURATION_AT          = "pwdLockoutDuration";

    public final static String PWD_MAX_FAILURE_AT               = "pwdMaxFailure";

    public final static String PWD_FAILURE_COUNT_INTERVAL_AT    = "pwdFailureCountInterval";

    public final static String PWD_MUST_CHANGE_AT               = "pwdMustChange";

    public final static String PWD_ALLOW_USER_CHANGE_AT         = "pwdAllowUserChange";

    public final static String PWD_SAFE_MODIFY_AT               = "pwdSafeModify";

    public final static String PWD_CHANGED_TIME_AT              = "pwdChangedTime";

    public final static String PWD_ACCOUNT_LOCKED_TIME_AT       = "pwdAccountLockedTime";

    public final static String PWD_FAILURE_TIME_AT              = "pwdFailureTime";

    public final static String PWD_MIN_AGE_AT                   = "pwdMinAge";

    public final static String PWD_HISTORY_AT                   = "pwdHistory";

    public final static String PWD_GRACE_USE_TIME_AT            = "pwdGraceUseTime";

    public final static String PWD_RESET_AT                     = "pwdReset";

    public final static String PWD_POLICY_SUBENTRY_AT           = "pwdPolicySubentry";

    public final static String PWD_MIN_DELAY_AT                 = "pwdMinDelay";

    public final static String PWD_MAX_DELAY_AT                 = "pwdMaxDelay";

    public final static String PWD_MAX_IDLE_AT                  = "pwdMaxIdle";

    public final static String PWD_START_TIME_AT                = "pwdStartTime";

    public final static String PWD_END_TIME_AT                  = "pwdEndTime";

    public final static String PWD_LAST_SUCCESS_AT              = "pwdLastSuccess";

    public final static String PWD_MAX_AGE_AT                   = "pwdMaxAge";

    public final static String PWD_GRACE_EXPIRE_AT              = "pwdGraceExpire";

    public final static String PWD_MAX_LENGTH_AT                = "pwdMaxLength";

    public final static String PWD_IN_HISTORY_AT                = "pwdInHistory";

    public final static String PWD_CHECK_QUALITY_AT             = "pwdCheckQuality";

    public final static String PWD_MIN_LENGTH_AT                = "pwdMinLength";

    public final static String PWD_EXPIRE_WARNING_AT            = "pwdExpireWarning";

    public final static String PWD_GRACE_AUTHN_LIMIT_AT         = "pwdGraceAuthNLimit";

    public final static String PWD_LOCKOUT_AT                   = "pwdLockout";
}
