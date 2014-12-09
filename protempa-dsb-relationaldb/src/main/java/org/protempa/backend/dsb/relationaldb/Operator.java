package org.protempa.backend.dsb.relationaldb;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2014 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * SQL comparison operators.
 * 
 * @author Andrew Post
 */
public enum Operator {
    EQUAL_TO("="), LIKE("LIKE"), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<="), NOT_EQUAL_TO("<>");
    private String sqlOperator;

    private Operator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

    /**
     * Gets the {@link String} operator.
     *
     * @return
     */
    public String getSqlOperator() {
        return this.sqlOperator;
    }
    
}
