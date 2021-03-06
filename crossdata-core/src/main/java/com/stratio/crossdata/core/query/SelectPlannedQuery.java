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

package com.stratio.crossdata.core.query;

import com.stratio.crossdata.common.annotation.Experimental;
import com.stratio.crossdata.common.executionplan.ExecutionWorkflow;
import com.stratio.crossdata.common.executionplan.QueryWorkflow;
import com.stratio.crossdata.common.logicalplan.LogicalWorkflow;
import com.stratio.crossdata.common.result.QueryStatus;

/**
 * Select Planned Query Class.
 */
public class SelectPlannedQuery extends SelectValidatedQuery implements IPlannedQuery {

    private final ExecutionWorkflow executionWorkflow;

    /**
     * Constructor class.
     *
     * @param selectValidatedQuery Select Query that was validated
     * @param executionWorkflow    The execution workflow of the query
     */
    public SelectPlannedQuery(SelectValidatedQuery selectValidatedQuery, ExecutionWorkflow executionWorkflow) {
        super(selectValidatedQuery);
        setQueryStatus(QueryStatus.PLANNED);
        this.executionWorkflow = executionWorkflow;
    }

    /**
     * Constructor class.
     *
     * @param plannedQuery The planned query
     */
    public SelectPlannedQuery(SelectPlannedQuery plannedQuery) {
        this(plannedQuery, plannedQuery.getExecutionWorkflow());
    }

    @Override
    public ExecutionWorkflow getExecutionWorkflow() {
        return executionWorkflow;
    }

    @Experimental
    public LogicalWorkflow getLastLogicalWorkflow(){
        ExecutionWorkflow lastExecutionWorkflow = executionWorkflow;
        while(lastExecutionWorkflow.getNextExecutionWorkflow() != null){
            lastExecutionWorkflow = lastExecutionWorkflow.getNextExecutionWorkflow();
        }
        return ((QueryWorkflow) lastExecutionWorkflow).getWorkflow();
    }
}
