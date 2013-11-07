
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
 * This class is used to read/write JSON data
 *
 * @author Karl STEIN
 */
public class JsonSerializer extends Serializer {

    /**
     * Creates a JSON converter
     */
    public JsonSerializer() {
        // Use four spaces for indentation
        setIndentationCharacter("    ");
    }

    @Override
    protected boolean checkField(final Field field) {
        final Class<?> cls = field.getType();

        // Convert these field types only
        return super.checkField(field) && (cls.isPrimitive()
                || Number.class.isAssignableFrom(cls)
                || cls.equals(Boolean.class)
                || cls.equals(String.class)
                || cls.equals(Date.class)
                || cls.equals(Character.class)
                || Object.class.isAssignableFrom(cls));
    }

    /**
     * Escapes all quotes in the value
     *
     * @param value the value to escape
     * @return CharSequence
     */
    protected CharSequence escapeValue(String value) {
        if (value != null && value.indexOf('"') > 0) {
            value = value.replace("\"", "\\\"");
        }
        return value;
    }

    @Override
    public <T> T read(final Reader data, final Class<T> cls) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Writer write(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        if (object == null) {
            writer.append("null");

        } else {
            final Class<?> cls = object.getClass();

            if (cls.equals(String.class) || cls.equals(Date.class) || cls.isEnum()
                    || cls.equals(Character.class) || cls.equals(Character.TYPE)) {
                // Escape quotes when the object is a string
                writer.write("\"" + escapeValue(String.valueOf(object)) + "\"");

            } else if (cls.isPrimitive() || cls.equals(Boolean.class) || Number.class.isInstance(object)) {
                writer.append(String.valueOf(object));

            } else if (List.class.isInstance(object) || Set.class.isInstance(object)) {
                writeCollection((Collection<?>) object, writer);

            } else if (Map.class.isInstance(object)) {
                writeMap((Map<?, ?>) object, writer);

            } else if (cls.isArray()) {
                writeCollection(getCollectionFromObject(object), writer);

            } else {
                writeObject(object, writer);
            }
        }

        return writer;
    }

    /**
     * Writes the collection
     *
     * @param collection the collection to write
     * @param writer     the writer
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    protected void writeCollection(final Collection<?> collection, final Writer writer) throws IllegalArgumentException, IllegalAccessException, IOException {
        // Open the collection
        writer.append("[\n");
        increaseIndentation();

        final Iterator<?> iterator = collection.iterator();

        while (iterator.hasNext()) {
            final Object element = iterator.next();

            // Add the element
            writeIndentation(writer);
            write(element, writer);

            if (iterator.hasNext()) {
                writer.append(",");
            }
            writer.append("\n");
        }

        // Close the collection
        decreaseIndentation();
        writeIndentation(writer);
        writer.append("]");
    }

    /**
     * Writes the indentation character
     *
     * @param writer the writer
     * @throws IOException
     */
    protected void writeIndentation(final Writer writer) throws IOException {
        for (int i = 0; i < getIndentationLevel(); i++) {
            writer.write(getIndentationCharacter());
        }
    }

    /**
     * Writes the map values
     *
     * @param map    the map to write
     * @param writer the writer
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    protected void writeMap(final Map<?, ?> map, final Writer writer) throws IllegalArgumentException, IllegalAccessException, IOException {
        // Open the object
        writer.append("{\n");
        increaseIndentation();

        final Iterator<?> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            // Add the field name
            final Object key = iterator.next();
            writeIndentation(writer);
            writer.write("\"" + key + "\" : ");

            // Add the field value
            write(map.get(key), writer);

            if (iterator.hasNext()) {
                writer.append(',');
            }
            writer.append('\n');
        }

        // Close the object
        decreaseIndentation();
        writeIndentation(writer);
        writer.append("}");
    }

    /**
     * Writes the object value
     *
     * @param object the object to write
     * @param writer the writer
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected void writeObject(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        // Open the object
        writer.append("{\n");
        increaseIndentation();

        // Get the object fields
        final Set<Field> fields = getFields(object.getClass());
        final Iterator<Field> iterator = fields.iterator();

        while (iterator.hasNext()) {
            final Field field = iterator.next();

            // Add the field name
            writeIndentation(writer);
            writer.write("\"" + field.getName() + "\" : ");

            // Add the field value
            write(field.get(object), writer);

            if (iterator.hasNext()) {
                writer.append(',');
            }
            writer.append('\n');
        }

        // Close the object
        decreaseIndentation();
        writeIndentation(writer);
        writer.append("}");
    }
}
