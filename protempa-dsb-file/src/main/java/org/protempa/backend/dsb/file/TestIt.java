/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
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
import java.io.IOException;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEventIterator;
import org.protempa.proposition.Proposition;

/**
 *
 * @author arpost
 */
public class TestIt {

    public static void main(String[] args) throws IOException, DataSourceReadException {
        FixedWidthFileDataSourceBackend dsb = new FixedWidthFileDataSourceBackend();
        dsb.setKeyIdOffset(1);
        dsb.setKeyIdLength(6);
        dsb.setSkipLines(2);
        dsb.setId("foo");
        String[] specs = new String[]{
            "1|6|[Patient Constant 0].patientId$NOMINALVALUE",
            "1|6|[Patient Constant 0]>patientDetails[PatientDetails Constant 0].patientId$NOMINALVALUE",
            "65|6|[Patient Constant 0]>patientDetails[PatientDetails Constant 0].gender$NOMINALVALUE",
            "74|60|[Patient Constant 0]>patientDetails[PatientDetails Constant 0].race$NOMINALVALUE",
            "138|60|[Patient Constant 0]>patientDetails[PatientDetails Constant 0].ethnicity$NOMINALVALUE",
            "202|29|[Patient Constant 0]>patientDetails[PatientDetails Constant 0].maritalStatus$NOMINALVALUE"
        };
        dsb.parseFixedWidthColumnSpecs(specs);
        dsb.parseFiles(new String[] {"/Users/arpost/Downloads/nlstDemographicsTableDump 2.txt"});
        Set<String> propIds = Arrays.asSet(new String[]{"Patient", "PatientDetails"});
        try (DataStreamingEventIterator<Proposition> itr = dsb.readPropositions(null, propIds, null, null, null)) {
            int i = 0;
            while (itr.hasNext()) {
                itr.next();
                i++;
            }
            System.out.println("Processed " + i + " records");
        }
    }
}
