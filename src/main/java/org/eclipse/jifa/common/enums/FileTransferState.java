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
package org.eclipse.jifa.common.enums;

public enum FileTransferState {

    NOT_STARTED,

    IN_PROGRESS,

    SUCCESS,

    ERROR;

    public static FileTransferState fromProgressState(ProgressState progress) {
        switch (progress) {
            case NOT_STARTED:
                return NOT_STARTED;
            case IN_PROGRESS:
                return IN_PROGRESS;
            case SUCCESS:
                return SUCCESS;
            case ERROR:
                return ERROR;
        }
        throw new IllegalStateException();
    }

    public boolean isFinal() {
        return this == SUCCESS || this == ERROR;
    }

    public ProgressState toProgressState() {
        switch (this) {
            case NOT_STARTED:
                return ProgressState.NOT_STARTED;

            case IN_PROGRESS:
                return ProgressState.IN_PROGRESS;

            case SUCCESS:
                return ProgressState.SUCCESS;

            case ERROR:
                return ProgressState.ERROR;
        }
        throw new IllegalStateException();
    }
}
