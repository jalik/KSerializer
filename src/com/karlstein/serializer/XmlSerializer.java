
/*
 * Copyright 2013 Karl STEIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karlstein.serializer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is used to read/write XML data
 *
 * @author Karl STEIN
 */
public class XmlSerializer extends Serializer {

    /**
     * The attributes
     */
    private Map<Class<?>, Set<String>> attributes = new HashMap<Class<?>, Set<String>>();
    /**
     * The encoding
     */
    private String encoding = "UTF-8";
    /**
     * The object namespaces
     */
    private Map<String, String> namespaces = new HashMap<String, String>();

    /**
     * Creates an XML converter
     */
    public XmlSerializer() {
        // Use four spaces for indentation
        setIndentationCharacter("    ");
    }

    /**
     * Defines the field as an attribute
     *
     * @param cls   the target class
     * @param field the target field
     */
    public void asAttribute(final Class<?> cls, final String field) {
        if (!attributes.containsKey(cls)) {
            attributes.put(cls, new HashSet<String>());
        }
        attributes.get(cls).add(field);
    }

    /**
     * Converts XML reserved characters
     *
     * @param value the value to escape
     * @return CharSequence
     */
    protected CharSequence escapeAttribute(String value) {
        if (value != null) {
            value = value.replace("\"", "&quot;");
            value = value.replace("<", "&lt;");
            value = value.replace(">", "&gt;");

            // Removes the line separator
            value = value.replaceAll("\\r|\\n", "");
        }
        return value;
    }

    /**
     * Converts XML reserved characters
     *
     * @param value the value to escape
     * @return CharSequence
     */
    protected CharSequence escapeValue(String value) {
        if (value != null) {
            value = value.replace("<", "&lt;");
            value = value.replace(">", "&gt;");
        }
        return value;
    }

    /**
     * Returns the object attributes
     *
     * @param cls the class to parse
     * @return Set
     */
    public Set<String> getAttributes(final Class<?> cls) {
        return attributes.get(cls);
    }

    /**
     * Returns the encoding
     *
     * @return String
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Returns the node attributes
     *
     * @param cls the class to parse
     * @return Set
     */
    protected Set<Field> getNodeAttributes(final Class<?> cls) {
        final Set<Field> attr = new HashSet<Field>();
        final Set<Field> fields = getFields(cls);

        for (final Field field : fields) {
            if (attributes.containsKey(cls) && attributes.get(cls).contains(field.getName())) {
                attr.add(field);
            }
        }

        return attr;
    }

    /**
     * Returns the node children
     *
     * @param cls the class to parse
     * @return Set
     */
    protected Set<Field> getNodeChildren(final Class<?> cls) {
        final Set<Field> children = new HashSet<Field>();
        final Set<Field> fields = getFields(cls);

        for (final Field field : fields) {
            if (!attributes.containsKey(cls) || !attributes.get(cls).contains(field.getName())) {
                children.add(field);
            }
        }

        return children;
    }

    /**
     * Returns the node name
     *
     * @param cls the class
     * @return String
     */
    protected String getNodeName(final Class<?> cls) {
        return cls.getSimpleName();
    }

    /**
     * Returns the node namespace
     *
     * @param cls the class
     * @return String
     */
    protected String getNodeNamespace(final Class<?> cls) {
        return cls.getPackage().getName();
    }

    /**
     * Checks if the object is a value
     *
     * @param object the object to check
     * @return boolean
     */
    protected boolean isValue(final Object object) {
        final Class<?> cls = object.getClass();

        return (cls.isPrimitive()
                || cls.equals(String.class)
                || cls.equals(Character.class)
                || cls.equals(Character.TYPE)
                || cls.equals(Boolean.class)
                || cls.equals(Date.class)
                || cls.isEnum()
                || Number.class.isInstance(object)
        );
    }

    /**
     * Normalizes the name
     *
     * @param name the value to normalize
     * @return String
     */
    public String normalize(final String name) {
        return name.replaceAll("[^A-Za-z0-9]", "");
    }

    @Override
    public <T> T read(Reader reader, Class<T> cls) {
        // TODO implement read method
        return null;
    }

    /**
     * Sets the encoding
     *
     * @param encoding the encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public Writer write(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        // Add the XML header
        writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");

        // Add the root node
        return writeNode(object, writer);
    }

    /**
     * Writes the attribute
     *
     * @param name   the name of the attribute
     * @param value  the value of the attribute
     * @param writer the writer
     * @return Writer
     * @throws IOException
     */
    protected Writer writeAttribute(final String name, final Object value, final Writer writer) throws IOException {
        // Add the attribute name and open the value
        writer.write(" " + normalize(name) + "=\"");

        if (value != null) {
            // Escape the value
            writer.append(escapeAttribute(String.valueOf(value)));
        }

        // Close the attribute value
        writer.append("\"");

        return writer;
    }

    /**
     * Writes the collection
     *
     * @param nodeName the name of the node
     * @param object   the collection object
     * @param writer   the writer
     * @return Writer
     * @throws IOException
     * @throws IllegalAccessException
     */
    protected Writer writeCollection(final String nodeName, final Object object, final Writer writer) throws IOException, IllegalAccessException {
        // Open the node
        writeIndentation(writer);
        writer.write("<" + nodeName + ">\n");

        final Collection<?> collection = (Collection<?>) object;

        for (Object obj : collection) {
            // Add the element
            increaseIndentation();
            writeNode(obj, writer);
            decreaseIndentation();
        }

        // Close the node
        writeIndentation(writer);
        writer.write("</" + nodeName + ">\n");

        return writer;
    }

    /**
     * Writes the comment
     *
     * @param object the object that is the comment
     * @param writer the writer
     * @return Writer
     * @throws IOException
     */
    protected Writer writeComment(final Object object, final Writer writer) throws IOException {
        // Open the comment
        writeIndentation(writer);
        writer.append("<!-- ");

        // Add the value
        writer.append(String.valueOf(object));

        // Close the comment
        writer.append(" -->\n");

        return writer;
    }

    /**
     * Writes the map
     *
     * @param nodeName the name of the node
     * @param object   the object to write
     * @param writer   the writer
     * @return Writer
     * @throws IOException
     * @throws IllegalAccessException
     */
    protected Writer writeMap(final String nodeName, final Object object, final Writer writer) throws IOException, IllegalAccessException {
        // Open the node
        writeIndentation(writer);
        writer.write("<" + nodeName + ">\n");

        final Map<?, ?> map = (Map<?, ?>) object;

        for (Object key : map.keySet()) {
            // Add the element
            final Object element = map.get(key);
            increaseIndentation();
            writeNode(element, writer);
            decreaseIndentation();
        }

        // Close the node
        writeIndentation(writer);
        writer.write("</" + nodeName + ">\n");

        return writer;
    }

    /**
     * Writes the node
     *
     * @param object the object to write
     * @param writer the writer
     * @return Writer
     * @throws IOException
     * @throws IllegalAccessException
     */
    protected Writer writeNode(final Object object, final Writer writer) throws IOException, IllegalAccessException {
        final String name = object.getClass().getSimpleName();
        return writeNode(name, object, writer);
    }

    /**
     * Writes the node with the given name
     *
     * @param name   the nae of the node
     * @param object the object to write
     * @param writer the writer
     * @return Writer
     * @throws IOException
     * @throws IllegalAccessException
     */
    protected Writer writeNode(final String name, final Object object, final Writer writer) throws IOException, IllegalAccessException {
        if (object != null) {
            final Class<?> cls = object.getClass();
            final String nodeName = normalize("") + normalize(name);

            if (List.class.isInstance(object) || Set.class.isInstance(object)) {
                writeCollection(nodeName, object, writer);

            } else if (Map.class.isInstance(object)) {
                writeMap(nodeName, object, writer);

            } else if (cls.isArray()) {
                writeCollection(nodeName, getCollectionFromObject(object), writer);

            } else {
                // TODO get the namespace

                // Open the node
                writeIndentation(writer);
                writer.write("<" + nodeName);

                // Get the attributes
                final Set<Field> attributes = getNodeAttributes(cls);

                for (Field field : attributes) {
                    // Add the attribute
                    writeAttribute(field.getName(), field.get(object), writer);
                }

                // Get the children
                final Set<Field> children = getNodeChildren(cls);

                // Close the node
                writer.append(">");

                if (isValue(object)) {
                    writer.append(escapeValue(String.valueOf(object)));

                } else {
                    writer.append("\n");

                    for (Field field : children) {
                        increaseIndentation();
                        writeNode(field.getName(), field.get(object), writer);
                        decreaseIndentation();
                    }
                    writeIndentation(writer);
                }

                // Close the node
                writer.write("</" + nodeName + ">\n");
            }
        }
        return writer;
    }
}
