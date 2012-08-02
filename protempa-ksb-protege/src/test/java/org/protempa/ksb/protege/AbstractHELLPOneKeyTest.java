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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.protempa.FinderException;
import org.protempa.proposition.Proposition;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.handler.MappingQueryResultsHandler;

/**
 * This is a test to verify that the parameters found for a test case are
 * correct.
 * 
 * The parameters considered are HELLP_I, HELLP_II,
 * HELLP_FIRST_RECOVERING_PLATELETS, HELLP_RECURRING_AND_RECOVERING_PLATELETS,
 * HELLP_RECURRING_PLATELETS.
 * 
 * @author Nora Sovarel
 * 
 */
public abstract class AbstractHELLPOneKeyTest extends AbstractTest {

    private static final List<String> HELLP_II_RECURRING_RECOVERY = Arrays.asList(new String[]{"HELLP_II",
                "HELLP_FIRST_RECOVERING_PLATELETS",
                "HELLP_RECURRING_PLATELETS",
                "HELLP_RECURRING_AND_RECOVERING_PLATELETS"});
    private static final List<String> HELLP_I_RECURRING_RECOVERY = Arrays.asList(new String[]{"HELLP_I",
                "HELLP_FIRST_RECOVERING_PLATELETS",
                "HELLP_RECURRING_PLATELETS",
                "HELLP_RECURRING_AND_RECOVERING_PLATELETS"});
    private static final List<String> HELLP_I_RECCURING = Arrays.asList(new String[]{"HELLP_I",
                "HELLP_FIRST_RECOVERING_PLATELETS",
                "HELLP_RECURRING_PLATELETS"});
    private static final List<String> HELLP_I_RECOVERY = Arrays.asList(new String[]{"HELLP_I",
                "HELLP_FIRST_RECOVERING_PLATELETS"});
    private static final List<String> HELLP_II_RECOVERY = Arrays.asList(new String[]{"HELLP_II",
                "HELLP_FIRST_RECOVERING_PLATELETS"});
    private static final List<String> HELLP_II = Arrays.asList(new String[]{"HELLP_II"});
    private static final List<String> HELLP_I = Arrays.asList(new String[]{"HELLP_I"});
    private static final String[] ALL_PARAMETERS = new String[]{
        "HELLP_I", "HELLP_II", "HELLP_FIRST_RECOVERING_PLATELETS",
        "HELLP_RECURRING_AND_RECOVERING_PLATELETS",
        "HELLP_RECURRING_PLATELETS"
    };
    protected static final HashMap<String, List<String>> RESULTS =
            new HashMap<String, List<String>>();

    @Test
    public void test9658590() throws Exception {
        runTest("9658590", HELLP_II_RECOVERY);
    }

    @Test
    public void test127382() throws Exception {
        runTest("127382", HELLP_I_RECOVERY);
    }

    @Test
    public void test97723() throws Exception {
        runTest("97723", HELLP_II_RECOVERY);
    }

    @Test
    public void test2966143() throws Exception {
        runTest("2966143", HELLP_I_RECOVERY);
    }

    @Test
    public void test8674935() throws Exception {
        runTest("8674935", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test3734901() throws Exception {
        runTest("3734901", HELLP_II_RECOVERY);
    }

    @Test
    public void test114862() throws Exception {
        runTest("114862", HELLP_I_RECOVERY);
    }

    @Test
    public void test126307() throws Exception {
        runTest("126307", HELLP_I_RECOVERY);
    }

    @Test
    public void test3733006() throws Exception {
        runTest("3733006", HELLP_II_RECOVERY);
    }

    @Test
    public void test5916286() throws Exception {
        runTest("5916286", HELLP_II_RECOVERY);
    }

    @Test
    public void test4662064() throws Exception {
        runTest("4662064", HELLP_II_RECOVERY);
    }

    @Test
    public void test3730437() throws Exception {
        runTest("3730437", Arrays.asList(new String[]{"HELLP_II"}));
    }

    @Test
    public void test4663847() throws Exception {
        runTest("4663847", HELLP_II_RECOVERY);
    }

    @Test
    public void test121007() throws Exception {
        runTest("121007", HELLP_I_RECOVERY);
    }

    @Test
    public void test7808339() throws Exception {
        runTest("7808339", HELLP_II_RECURRING_RECOVERY);
    }

    @Test
    public void test3542709() throws Exception {
        runTest("3542709", HELLP_I_RECOVERY);
    }

    @Test
    public void test4904427() throws Exception {
        runTest("4904427", HELLP_II_RECOVERY);
    }

    @Test
    public void test3541709() throws Exception {
        runTest("3541709", Arrays.asList(new String[]{"HELLP_II"}));
    }

    @Test
    public void test98140() throws Exception {
        runTest("98140", HELLP_II_RECOVERY);
    }

    @Test
    public void test7150173() throws Exception {
        runTest("7150173", HELLP_II_RECOVERY);
    }

    @Test
    public void test8675530() throws Exception {
        runTest("8675530", HELLP_I_RECOVERY);
    }

    @Test
    public void test3734160() throws Exception {
        runTest("3734160", HELLP_II_RECOVERY);
    }

    @Test
    public void test7150587() throws Exception {
        runTest("7150587", HELLP_II_RECOVERY);
    }

    @Test
    public void test6150945() throws Exception {
        runTest("6150945", HELLP_I_RECOVERY);
    }

    @Test
    public void test6878404() throws Exception {
        runTest("6878404", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test118429() throws Exception {
        runTest("118429", HELLP_II_RECOVERY);
    }

    @Test
    public void test107407() throws Exception {
        runTest("107407", HELLP_II_RECOVERY);
    }

    @Test
    public void test3733088() throws Exception {
        runTest("3733088", HELLP_II_RECOVERY);
    }

    @Test
    public void test7738024() throws Exception {
        runTest("7738024", HELLP_II_RECOVERY);
    }

    @Test
    public void test7280462() throws Exception {
        runTest("7280462", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test7739625() throws Exception {
        runTest("7739625", HELLP_II_RECOVERY);
    }

    @Test
    public void test11432690() throws Exception {
        runTest("11432690", HELLP_II_RECOVERY);
    }

    @Test
    public void test4262023() throws Exception {
        runTest("4262023", HELLP_I_RECOVERY);
    }

    @Test
    public void test4260229() throws Exception {
        runTest("4260229", HELLP_I_RECOVERY);
    }

    @Test
    public void test5915780() throws Exception {
        runTest("5915780", HELLP_I_RECOVERY);
    }

    @Test
    public void test6151962() throws Exception {
        runTest("6151962", HELLP_II_RECOVERY);
    }

    @Test
    public void test11510243() throws Exception {
        runTest("11510243", HELLP_I_RECOVERY);
    }

    @Test
    public void test10809935() throws Exception {
        runTest("10809935", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test2966363() throws Exception {
        runTest("2966363", HELLP_I_RECOVERY);
    }

    @Test
    public void test8676890() throws Exception {
        runTest("8676890", HELLP_II_RECURRING_RECOVERY);
    }

    @Test
    public void test3537777() throws Exception {
        runTest("3537777", HELLP_I_RECOVERY);
    }

    @Test
    public void test3734056() throws Exception {
        runTest("3734056", HELLP_I_RECOVERY);
    }

    @Test
    public void test3732386() throws Exception {
        runTest("3732386", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test5914352() throws Exception {
        runTest("5914352", HELLP_I_RECOVERY);
    }

    @Test
    public void test4659560() throws Exception {
        runTest("4659560", HELLP_II_RECOVERY);
    }

    @Test
    public void test5332440() throws Exception {
        runTest("5332440", HELLP_II_RECOVERY);
    }

    @Test
    public void test5096648() throws Exception {
        runTest("5096648", HELLP_II_RECURRING_RECOVERY);
    }

    @Test
    public void test10611563() throws Exception {
        runTest("10611563", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test5570464() throws Exception {
        runTest("5570464", HELLP_I_RECOVERY);
    }

    @Test
    public void test7740371() throws Exception {
        runTest("7740371", HELLP_II_RECOVERY);
    }

    @Test
    public void test6154834() throws Exception {
        runTest("6154834", HELLP_II_RECOVERY);
    }

    @Test
    public void test5571877() throws Exception {
        runTest("5571877", HELLP_I_RECOVERY);
    }

    @Test
    public void test4257694() throws Exception {
        runTest("4257694", HELLP_I_RECOVERY);
    }

    @Test
    public void test4904517() throws Exception {
        runTest("4904517", HELLP_I_RECOVERY);
    }

    @Test
    public void test3731701() throws Exception {
        runTest("3731701", HELLP_II);
    }

    @Test
    public void test106570() throws Exception {
        runTest("106570", HELLP_II_RECOVERY);
    }

    @Test
    public void test7455409() throws Exception {
        runTest("7455409", HELLP_II_RECOVERY);
    }

    @Test
    public void test6675996() throws Exception {
        runTest("6675996", HELLP_I_RECOVERY);
    }

    @Test
    public void test4903611() throws Exception {
        runTest("4903611", HELLP_II_RECOVERY);
    }

    @Test
    public void test4903036() throws Exception {
        runTest("4903036", HELLP_I_RECOVERY);
    }

    @Test
    public void test6152507() throws Exception {
        runTest("6152507", HELLP_I_RECOVERY);
    }

    @Test
    public void test5332630() throws Exception {
        runTest("5332630", HELLP_I_RECOVERY);
    }

    @Test
    public void test3538826() throws Exception {
        runTest("3538826", HELLP_II);
    }

    @Test
    public void test5333614() throws Exception {
        runTest("5333614", HELLP_I_RECOVERY);
    }

    @Test
    public void test106046() throws Exception {
        runTest("106046", HELLP_II_RECOVERY);
    }

    @Test
    public void test6676285() throws Exception {
        runTest("6676285", HELLP_I_RECOVERY);
    }

    @Test
    public void test5097414() throws Exception {
        runTest("5097414", HELLP_I_RECOVERY);
    }

    @Test
    public void test5097001() throws Exception {
        runTest("5097001", HELLP_I);
    }

    @Test
    public void test7504008() throws Exception {
        runTest("7504008", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test5912912() throws Exception {
        runTest("5912912", HELLP_I_RECURRING_RECOVERY);
    }

    @Test
    public void test5335871() throws Exception {
        runTest("5335871", HELLP_I_RECOVERY);
    }

    @Test
    public void test4259743() throws Exception {
        runTest("4259743", HELLP_I_RECOVERY);
    }

    @Test
    public void test5041549() throws Exception {
        runTest("5041549", HELLP_II);
    }

    @Test
    public void test100121() throws Exception {
        runTest("100121", HELLP_I_RECOVERY);
    }

    @Test
    public void test6156243() throws Exception {
        runTest("6156243", HELLP_II_RECOVERY);
    }

    @Test
    public void test4663449() throws Exception {
        runTest("4663449", HELLP_II_RECOVERY);
    }

    @Test
    public void test4662671() throws Exception {
        runTest("4662671", HELLP_I_RECOVERY);
    }

    @Test
    public void test103437() throws Exception {
        runTest("103437", HELLP_I_RECCURING);
    }

    @Test
    public void test4660128() throws Exception {
        runTest("4660128", HELLP_I_RECOVERY);
    }

    private void runTest(String keyId, Collection<String> expected)
            throws Exception {
        MappingQueryResultsHandler mqrh =
                new MappingQueryResultsHandler();
        DefaultQueryBuilder q = new DefaultQueryBuilder();
        q.setKeyIds(new String[] {keyId});
        q.setPropositionIds(ALL_PARAMETERS);
        protempa.execute(protempa.buildQuery(q), mqrh);
        Collection<Proposition> actual =
                mqrh.getResultMap().get(keyId);
        HashSet<String> names = new HashSet<String>();
        for (Proposition def : actual) {
            names.add(def.getId());
        }
        Assert.assertTrue("Key: " + keyId + " Actual: " + names + " Expected: "
                + expected, names.equals(new HashSet<String>(expected)));
    }
}
