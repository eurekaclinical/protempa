package org.protempa.xml;

import java.util.ArrayList;
import java.util.HashSet;

import org.protempa.query.handler.table.Derivation;
import org.protempa.query.handler.table.Link;
import org.protempa.query.handler.table.Reference;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert between an array of Link objects and XML.
 * 
 * @author mgrand
 */
public class LinksConverter extends AbstractConverter {

	private static final String REFERENCE = "reference";
	private static final String DERIVATION = "derivation";

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
		Link[] links = (Link[])source;
		
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

	private static final HashSet<String> linkTags = new HashSet<String>();
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
		ArrayList<Link> linkList = new ArrayList<Link>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String tag = expect(reader, linkTags);
			if (DERIVATION.equals(tag)) {
				linkList.add((Derivation)context.convertAnother(null, Derivation.class));
			} else {
				linkList.add((Reference)context.convertAnother(null, Reference.class));
			}
			reader.moveUp();
		}
		return linkList.toArray(new Link[linkList.size()]);
	}

}
