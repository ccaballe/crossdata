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

package com.stratio.crossdata.core.structures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.stratio.crossdata.common.data.CatalogName;
import com.stratio.crossdata.common.data.Name;
import com.stratio.crossdata.common.data.TableName;
import com.stratio.crossdata.common.statements.structures.SelectSelector;
import com.stratio.crossdata.core.query.BaseQuery;
import com.stratio.crossdata.core.query.SelectParsedQuery;
import com.stratio.crossdata.core.query.SelectValidatedQuery;
import com.stratio.crossdata.core.statements.SelectStatement;

/**
 * Extended Select Selector Class that allow Crossdata execute sub queries.
 */
public class ExtendedSelectSelector extends SelectSelector {

    private SelectParsedQuery selectParsedQuery;

    private SelectValidatedQuery selectValidatedQuery;

    /**
     * Class Constructor.
     * @param selectStatement The {@link com.stratio.crossdata.core.statements.SelectStatement}.
     * @param sessionCatalog The catalog affected.
     */
    public ExtendedSelectSelector(SelectStatement selectStatement, String sessionCatalog) {
        super(selectStatement.getTableName(), selectStatement.toSQL92String());
        this.selectParsedQuery = new SelectParsedQuery(
                new BaseQuery(
                        UUID.randomUUID().toString(),
                        selectStatement.toString().replaceAll(Name.UNKNOWN_NAME+".", ""),
                        new CatalogName(sessionCatalog),UUID.randomUUID().toString()),
                selectStatement);
    }


    public SelectStatement getSelectStatement() {
        return selectParsedQuery.getStatement();
    }

    public SelectParsedQuery getSelectParsedQuery() {
        return selectParsedQuery;
    }

    public SelectValidatedQuery getSelectValidatedQuery() {
        return selectValidatedQuery;
    }

    public void setSelectValidatedQuery(SelectValidatedQuery selectValidatedQuery) {
        this.selectValidatedQuery = selectValidatedQuery;
    }

    @Override
     public String toString() {
        String result = selectParsedQuery.toString();
        if(selectValidatedQuery != null){
            result = selectValidatedQuery.getStatement().toString();
        }
        return "("+ result +")";
    }

    /**
     * Get the sub query in SQL 92 syntax.
     * @return A String with the query in sql 92 syntax.
     */
    public String toSQLString() {
        return "("+ selectValidatedQuery.getStatement().toSQL92String()+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtendedSelectSelector)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ExtendedSelectSelector that = (ExtendedSelectSelector) o;

        if (selectParsedQuery != null ?
                !selectParsedQuery.equals(that.selectParsedQuery) :
                that.selectParsedQuery != null) {
            return false;
        }
        if (selectValidatedQuery != null ?
                !selectValidatedQuery.equals(that.selectValidatedQuery) :
                that.selectValidatedQuery != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (selectParsedQuery != null ? selectParsedQuery.hashCode() : 0);
        result = 31 * result + (selectValidatedQuery != null ? selectValidatedQuery.hashCode() : 0);
        return result;
    }

    @Override
    public Set<TableName> getSelectorTables() {
        return new HashSet<>(selectValidatedQuery.getTables());
    }
}
