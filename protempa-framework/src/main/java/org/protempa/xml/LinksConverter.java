/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.protempa.dest.table.Derivation;
import org.protempa.dest.table.Link;
import org.protempa.dest.table.Reference;

import java.util.ArrayList;
import java.util.HashSet;
import org.protempa.KnowledgeSource;

/**
 * Convert between an array of Link objects and XML.
 *
 * @author mgrand
 */
class LinksConverter extends AbstractConverter {

    private static final String REFERENCE = "reference";
    private static final String DERIVATION = "derivation";

    LinksConverter(KnowledgeSource knowledgeSource) {
        super(knowledgeSource);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
     * lang.Class)
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return Link[].class.equals(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Link[] links = (Link[]) source;

        for (Link link : links) {
            if (link instanceof Derivation) {
                writer.startNode(DERIVATION);
            } else if (link instanceof Reference) {
                writer.startNode(REFERENCE);
            }
            context.convertAnother(link);
            writer.endNode();
        }
    }

    private static final HashSet<String> linkTags = new HashSet<>();

    static {
        linkTags.add(DERIVATION);
        linkTags.add(REFERENCE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks
     * .xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        expectChildren(reader);
        ArrayList<Link> linkList = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String tag = expect(reader, linkTags);
            if (DERIVATION.equals(tag)) {
                linkList.add((Derivation) context.convertAnother(null, Derivation.class));
            } else {
                linkList.add((Reference) context.convertAnother(null, Reference.class));
            }
            reader.moveUp();
        }
        return linkList.toArray(new Link[linkList.size()]);
    }

}
