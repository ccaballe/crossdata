/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.common.exceptions;

/**
 * Planning exception thrown by the Driver if the statement could not be planned.
 */
public class PlanningException extends Exception {

    /**
     * Serial version UID in order to be {@link java.io.Serializable}.
     */
    private static final long serialVersionUID = 1878003904827417242L;

    /**
     * Constructor class.
     * @param message The message of the exception.
     */
    public PlanningException(String message) {
        super(message);
    }

    /**
     * Constructor class.
     * @param msg The message of the exception.
     * @param cause The throwable exception.
     */
    public PlanningException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
