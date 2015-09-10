package org.protempa.dest.deid;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import org.protempa.proposition.value.NominalValue;

/**
 *
 * @author Andrew Post
 */
public class DeidAttributes {
    public static final String IS_HIPAA_IDENTIFIER = "is-HIPAA-identifier";
    public static final String HIPAA_IDENTIFIER_TYPE = "HIPAA-identifier-type";
    
    public static NominalValue AGE_IN_YEARS = NominalValue.getInstance("AGE_IN_YEARS");
    public static NominalValue BIRTHDATE = NominalValue.getInstance("BIRTHDATE");
    public static NominalValue MRN = NominalValue.getInstance("MRN");
    
    private DeidAttributes() {}
}
