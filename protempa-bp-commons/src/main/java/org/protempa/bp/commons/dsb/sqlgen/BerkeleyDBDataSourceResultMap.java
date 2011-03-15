/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.bp.commons.dsb.sqlgen;

import com.sleepycat.collections.StoredIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.protempa.DataSourceResultMap;

/**
 *
 * @author Andrew Post
 */
public class BerkeleyDBDataSourceResultMap<P> extends DataSourceResultMap<P> {

    BerkeleyDBDataSourceResultMap(List<Map<String, List<P>>> maps) {
        super(maps);
    }

    @Override
    public Set<String> keySet() {
        Set<String> result = new HashSet<String>();
        for (Map<String, List<P>> map : getMaps()) {
            Iterator<String> itr = map.keySet().iterator();
            while (itr.hasNext()) {
                result.add(itr.next());
            }
            StoredIterator.close(itr);
        }
        return result;
    }

    @Override
    public Collection<List<P>> values() {
        Collection<List<P>> result = new ArrayList<List<P>>();
        for (Map<String, List<P>> map : getMaps()) {
            Iterator<List<P>> itr = map.values().iterator();
            while (itr.hasNext()) {
                result.add(itr.next());
            }
            StoredIterator.close(itr);
        }
        return result;
    }

    @Override
    public Set<Entry<String, List<P>>> entrySet() {
        Set<Entry<String, List<P>>> result =
                new HashSet<Entry<String, List<P>>>();
        for (Map<String, List<P>> map : getMaps()) {
            Iterator<Map.Entry<String, List<P>>> itr =
                    map.entrySet().iterator();
            while(itr.hasNext()) {
                Map.Entry<String, List<P>> entry = itr.next();
                DataSourceResultMapEntry newMe = new DataSourceResultMapEntry(
                        entry.getKey(), entry.getValue());
                result.add(newMe);
            }
            StoredIterator.close(itr);
        }
        return result;
    }


}
