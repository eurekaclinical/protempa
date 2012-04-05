/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.ksb.protege;

import java.io.File;

import org.protempa.AlgorithmSource;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.Protempa;
import org.protempa.ProtempaStartupException;

/**
 * 
 * @author Andrew Post
 */
public class AbstractTest {

    static {
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                System.getProperty("user.home") + File.separator
                        + ".protempa-protege-tests");
    }
    static Protempa protempa;
    static DataSource dataSource;
    static KnowledgeSource knowledgeSource;
    static AlgorithmSource algorithmSource;

    static void initProtempa(String configurationsId) {
        try {
            protempa = Protempa.newInstance(configurationsId);
            dataSource = protempa.getDataSource();
            knowledgeSource = protempa.getKnowledgeSource();
            algorithmSource = protempa.getAlgorithmSource();
        } catch (ProtempaStartupException ex) {
            throw new RuntimeException(ex);
        }
    }

    static void shutdownProtempa() {
        protempa.close();
    }
}
