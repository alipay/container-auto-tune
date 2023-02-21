/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.jifa.common;

public enum ErrorCode {

    SHOULD_NOT_REACH_HERE,

    UNKNOWN_ERROR,

    ILLEGAL_ARGUMENT,

    SANITY_CHECK,

    FILE_DOES_NOT_EXIST,

    FILE_HAS_BEEN_DELETED,

    TRANSFER_ERROR,

    NOT_TRANSFERRED,

    FILE_TYPE_MISMATCHED,

    HOST_IP_MISMATCHED,

    TRANSFERRING,

    UPLOADING,

    UPLOAD_TO_OSS_ERROR,

    /**
     * for master
     */
    DUMMY_ERROR_CODE,

    FORBIDDEN,

    PENDING_JOB,

    IMMEDIATE_JOB,

    JOB_DOES_NOT_EXIST,

    WORKER_DOES_NOT_EXIST,

    WORKER_DISABLED,

    PRIVATE_HOST_IP,

    REPEATED_USER_WORKER,

    SERVER_TOO_BUSY,

    UNSUPPORTED_OPERATION,

    ACL_CHECKING_FAILED,

    FILE_IS_IN_USED,

    FILE_IS_BEING_DELETING,

    FILE_TOO_BIG,

    RETRY,

    RELEASE_PENDING_JOB,

    READINESS_PROBE_FAILURE;

    public boolean isFatal() {
        switch (this) {
            case ILLEGAL_ARGUMENT:
            case FILE_DOES_NOT_EXIST:
            case FORBIDDEN:
            case FILE_IS_IN_USED:
                return false;
            default:
                return true;
        }
    }
}
