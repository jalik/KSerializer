/*
 * Copyright 2014 Karl STEIN
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

package com.karlstein.tools.serializer;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is used to read/write JSON data
 *
 * @author Karl STEIN
 */
public class JsonSerializer extends KSerializer {

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
            value = value.replace("\r", "\\r");
            value = value.replace("\n", "\\n");
        }
        return value;
    }

    /**
     * Writes a collection
     *
     * @param collection the collection to write
     * @param writer     the writer
     * @return Writer
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Writer write(final Collection<?> collection, final Writer writer) throws IllegalArgumentException, IllegalAccessException, IOException {
        // Open the collection
        writer.append('[');
        writeLineFeed(writer);
        increaseIndentation();

        final Iterator<?> iterator = collection.iterator();

        while (iterator.hasNext()) {
            final Object element = iterator.next();

            // Check if the element should be ignored
            if (element != null && ignoredObjects.contains(element)) {
                continue;
            }

            // Add the element
            writeIndentation(writer);
            write(element, writer);

            if (iterator.hasNext()) {
                writer.append(",");
            }
            writeLineFeed(writer);
        }

        // Close the collection
        decreaseIndentation();
        writeIndentation(writer);
        writer.append(']');

        return writer;
    }

    /**
     * Writes a map
     *
     * @param map    the map to write
     * @param writer the writer
     * @return Writer
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Writer write(final Map<?, ?> map, final Writer writer) throws IllegalArgumentException, IllegalAccessException, IOException {
        // Open the object
        writer.append('{');
        writeLineFeed(writer);
        increaseIndentation();

        final Iterator<?> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            final Object key = iterator.next();
            final Object value = map.get(key);

            // Check if the value should be ignored
            if (value != null && ignoredObjects.contains(value)) {
                continue;
            }

            // Add the field name
            writeIndentation(writer);
            writer.write("\"" + key + "\"");
            writeSpace(writer);
            writer.write(':');
            writeSpace(writer);

            // Add the field value
            write(value, writer);

            if (iterator.hasNext()) {
                writer.append(',');
            }
            writeLineFeed(writer);
        }

        // Close the object
        decreaseIndentation();
        writeIndentation(writer);
        writer.append('}');

        return writer;
    }

    @Override
    protected Writer writeLineFeed(Writer writer) throws IOException {
        return compressOutput ? writer : super.writeLineFeed(writer);
    }

    @Override
    public Writer write(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        if (object == null) {
            writer.append("null");

        } else {
            final Class<?> cls = object.getClass();

            if (cls.equals(String.class) || cls.equals(Date.class) || cls.isEnum() || cls.equals(Character.class) || cls.equals(Character.TYPE)) {
                // Escape quotes when the object is a string
                writer.write("\"" + escapeValue(String.valueOf(object)) + "\"");

            } else if (cls.isPrimitive() || cls.equals(Boolean.class) || Number.class.isInstance(object)) {
                writer.append(String.valueOf(object));

            } else if (List.class.isInstance(object) || Set.class.isInstance(object)) {
                write((Collection<?>) object, writer);

            } else if (Map.class.isInstance(object)) {
                write((Map<?, ?>) object, writer);

            } else if (cls.isArray()) {
                write(getCollectionFromObject(object), writer);

            } else {
                // Ignore this class next time
                ignoredObjects.add(object);

                // Open the object
                writer.append('{');
                writeLineFeed(writer);
                increaseIndentation();

                // Get the object fields
                final Set<Field> fields = getFields(object.getClass());
                final Iterator<Field> iterator = fields.iterator();

                while (iterator.hasNext()) {
                    final Field field = iterator.next();
                    final Object value = field.get(object);

                    // Check if the value should be ignored
                    if (value != null && ignoredObjects.contains(value)) {
                        continue;
                    }

                    // Add the field name
                    writeIndentation(writer);
                    writer.write("\"" + field.getName() + "\"");
                    writeSpace(writer);
                    writer.write(':');
                    writeSpace(writer);

                    // Add the field value
                    write(value, writer);

                    if (iterator.hasNext()) {
                        writer.append(',');
                    }
                    writeLineFeed(writer);
                }
//                ignoredObjects.remove(object);

                // Close the object
                decreaseIndentation();
                writeIndentation(writer);
                writer.append('}');
            }
        }
        return writer;
    }

    @Override
    protected Writer writeSpace(Writer writer) throws IOException {
        return compressOutput ? writer : super.writeSpace(writer);
    }
}
