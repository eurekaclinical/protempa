/**
 * 
 */
package org.protempa.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author mgrand
 * 
 */
public class XStreamWrapper extends XStream {
	private XStream underlyingXStream;

	XStreamWrapper(XStream underlyXStream) {
		this.underlyingXStream = underlyXStream;
	}

	public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
		unsupportedMutatorMethod();
	}

	private void unsupportedMutatorMethod() {
		throw new UnsupportedOperationException("This is a read-only XStream.");
	}

	public Object fromXML(String xml) {
		return underlyingXStream.fromXML(xml);
	}

	public Object fromXML(Reader reader) {
		return underlyingXStream.fromXML(reader);
	}

	public Object fromXML(URL url) {
		return underlyingXStream.fromXML(url);
	}

	public Object fromXML(String xml, Object root) {
		return underlyingXStream.fromXML(xml, root);
	}

	public Object fromXML(URL url, Object root) {
		return underlyingXStream.fromXML(url, root);
	}

	public void alias(String name, @SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}

	public void aliasType(String name, @SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}

	public void alias(String name, @SuppressWarnings("rawtypes") Class type, @SuppressWarnings("rawtypes") Class defaultImplementation) {
		unsupportedMutatorMethod();
	}

	public void aliasPackage(String name, String pkgName) {
		unsupportedMutatorMethod();
	}

	public void aliasField(String alias, @SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
		unsupportedMutatorMethod();
	}

	public void aliasAttribute(String alias, String attributeName) {
		unsupportedMutatorMethod();
	}

	public void aliasSystemAttribute(String alias, String systemAttributeName) {
		unsupportedMutatorMethod();
	}

	public void aliasAttribute(@SuppressWarnings("rawtypes") Class definedIn, String attributeName, String alias) {
		unsupportedMutatorMethod();
	}

	public void addDefaultImplementation(@SuppressWarnings("rawtypes") Class defaultImplementation, @SuppressWarnings("rawtypes") Class ofType) {
		unsupportedMutatorMethod();
	}

	public void addImmutableType(@SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}

	public void registerConverter(Converter converter) {
		unsupportedMutatorMethod();
	}

	public void registerConverter(SingleValueConverter converter) {
		unsupportedMutatorMethod();
	}

	public void registerConverter(SingleValueConverter converter, int priority) {
		unsupportedMutatorMethod();
	}

	public void registerLocalConverter(@SuppressWarnings("rawtypes") Class definedIn, String fieldName, Converter converter) {
		unsupportedMutatorMethod();
	}

	public void registerLocalConverter(@SuppressWarnings("rawtypes") Class definedIn, String fieldName, SingleValueConverter converter) {
		unsupportedMutatorMethod();
	}

	public Mapper getMapper() {
		return underlyingXStream.getMapper();
	}

	public ReflectionProvider getReflectionProvider() {
		return underlyingXStream.getReflectionProvider();
	}

	public ConverterLookup getConverterLookup() {
		return underlyingXStream.getConverterLookup();
	}

	public void setMode(int mode) {
		unsupportedMutatorMethod();
	}

	public void addImplicitCollection(@SuppressWarnings("rawtypes") Class ownerType, String fieldName) {
		unsupportedMutatorMethod();
	}

	public void addImplicitCollection(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, @SuppressWarnings("rawtypes") Class itemType) {
		unsupportedMutatorMethod();
	}

	public void addImplicitCollection(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, String itemFieldName,
			@SuppressWarnings("rawtypes") Class itemType) {
		unsupportedMutatorMethod();
	}

	public void addImplicitArray(@SuppressWarnings("rawtypes") Class ownerType, String fieldName) {
		unsupportedMutatorMethod();
	}

	public void addImplicitArray(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, @SuppressWarnings("rawtypes") Class itemType) {
		unsupportedMutatorMethod();
	}

	public void addImplicitArray(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, String itemName) {
		unsupportedMutatorMethod();
	}

	public void addImplicitMap(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, @SuppressWarnings("rawtypes") Class itemType,
			String keyFieldName) {
		unsupportedMutatorMethod();
	}

	public void addImplicitMap(@SuppressWarnings("rawtypes") Class ownerType, String fieldName, String itemFieldName,
			@SuppressWarnings("rawtypes") Class itemType, String keyFieldName) {
		unsupportedMutatorMethod();
	}

	public ObjectOutputStream createObjectOutputStream(Writer writer) throws IOException {
		return underlyingXStream.createObjectOutputStream(writer);
	}

	public ObjectOutputStream createObjectOutputStream(HierarchicalStreamWriter writer) throws IOException {
		return underlyingXStream.createObjectOutputStream(writer);
	}

	public ObjectOutputStream createObjectOutputStream(Writer writer, String rootNodeName) throws IOException {
		return underlyingXStream.createObjectOutputStream(writer, rootNodeName);
	}

	public ObjectOutputStream createObjectOutputStream(OutputStream out) throws IOException {
		return underlyingXStream.createObjectOutputStream(out);
	}

	public ObjectOutputStream createObjectOutputStream(OutputStream out, String rootNodeName) throws IOException {
		return underlyingXStream.createObjectOutputStream(out, rootNodeName);
	}

	public ObjectOutputStream createObjectOutputStream(HierarchicalStreamWriter writer, String rootNodeName) throws IOException {
		return underlyingXStream.createObjectOutputStream(writer, rootNodeName);
	}

	public ObjectInputStream createObjectInputStream(Reader xmlReader) throws IOException {
		return underlyingXStream.createObjectInputStream(xmlReader);
	}

	public ObjectInputStream createObjectInputStream(InputStream in) throws IOException {
		return underlyingXStream.createObjectInputStream(in);
	}

	public ObjectInputStream createObjectInputStream(HierarchicalStreamReader reader) throws IOException {
		return underlyingXStream.createObjectInputStream(reader);
	}

	public void setClassLoader(ClassLoader classLoader) {
		unsupportedMutatorMethod();
	}

	public ClassLoader getClassLoader() {
		return underlyingXStream.getClassLoader();
	}

	public void autodetectAnnotations(boolean mode) {
		unsupportedMutatorMethod();
	}

	public boolean equals(Object obj) {
		return underlyingXStream.equals(obj);
	}

	public Object fromXML(InputStream input) {
		return underlyingXStream.fromXML(input);
	}

	public Object fromXML(File file) {
		return underlyingXStream.fromXML(file);
	}

	public Object fromXML(Reader xml, Object root) {
		return underlyingXStream.fromXML(xml, root);
	}

	public Object fromXML(File file, Object root) {
		return underlyingXStream.fromXML(file, root);
	}

	public Object fromXML(InputStream input, Object root) {
		return underlyingXStream.fromXML(input, root);
	}

	public int hashCode() {
		return underlyingXStream.hashCode();
	}

	public void marshal(Object obj, HierarchicalStreamWriter writer) {
		underlyingXStream.marshal(obj, writer);
	}

	public void marshal(Object obj, HierarchicalStreamWriter writer, DataHolder dataHolder) {
		underlyingXStream.marshal(obj, writer, dataHolder);
	}

	public void registerConverter(Converter converter, int priority) {
		unsupportedMutatorMethod();
	}

	public DataHolder newDataHolder() {
		return underlyingXStream.newDataHolder();
	}

	public void omitField(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
		unsupportedMutatorMethod();
	}

	public void processAnnotations(@SuppressWarnings("rawtypes") Class[] types) {
		unsupportedMutatorMethod();
	}

	public void processAnnotations(@SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}

	public String toString() {
		return underlyingXStream.toString();
	}

	public String toXML(Object obj) {
		return underlyingXStream.toXML(obj);
	}

	public void toXML(Object obj, Writer out) {
		underlyingXStream.toXML(obj, out);
	}

	public void toXML(Object obj, OutputStream out) {
		underlyingXStream.toXML(obj, out);
	}

	public Object unmarshal(HierarchicalStreamReader reader) {
		return underlyingXStream.unmarshal(reader);
	}

	public Object unmarshal(HierarchicalStreamReader reader, Object root) {
		return underlyingXStream.unmarshal(reader, root);
	}

	public Object unmarshal(HierarchicalStreamReader reader, Object root, DataHolder dataHolder) {
		return underlyingXStream.unmarshal(reader, root, dataHolder);
	}

	public void useAttributeFor(String fieldName, @SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}

	public void useAttributeFor(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
		unsupportedMutatorMethod();
	}

	public void useAttributeFor(@SuppressWarnings("rawtypes") Class type) {
		unsupportedMutatorMethod();
	}
}
