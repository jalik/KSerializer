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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is used to read/write CSV data
 *
 * @author Karl STEIN
 */
public class CsvSerializer extends KSerializer {
    /**
     * The value delimiter
     */
    private char valueDelimiter = '"';
    /**
     * The value separator
     */
    private char valueSeparator = ',';

    /**
     * Creates a CSV converter
     */
    public CsvSerializer() {
    }

    /**
     * Creates a CSV converter with custom params
     *
     * @param delimiter the value delimiter
     * @param separator the value separator
     */
    public CsvSerializer(final char delimiter, final char separator) {
        valueDelimiter = delimiter;
        valueSeparator = separator;
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
                || cls.equals(Character.class));
    }

    /**
     * Escapes all delimiters in the value
     *
     * @param value the value to escape
     * @return CharSequence
     */
    protected CharSequence escapeValue(String value) {
        if (value != null) {
            int pos;

            while ((pos = value.indexOf(valueDelimiter)) >= 0) {
                value = value.substring(0, pos) + valueDelimiter + value.substring(pos);
            }
        }
        return value;
    }

    /**
     * Returns the value delimiter
     *
     * @return char
     */
    public char getValueDelimiter() {
        return valueDelimiter;
    }

    /**
     * Returns the value separator
     *
     * @return char
     */
    public char getValueSeparator() {
        return valueSeparator;
    }

    /**
     * Returns the objects from the reader
     *
     * @param data
     * @param cls
     * @param <T>
     * @return Collection
     * @throws IOException
     */
    public <T> Collection<T> read(final Reader data, final Class<T> cls) throws IOException {
        final BufferedReader reader = new BufferedReader(data);
        final Collection<T> objects = new ArrayList<T>(10);
        int charCode;

        do {
            // Read the character
            charCode = data.read();

            if (charCode == valueDelimiter) {

            }

        } while (charCode != -1);

        return objects;
    }

    /**
     * Returns an instance of the class using the values
     *
     * @param data
     * @param cls
     * @param <T>
     * @return Object
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected <T> T read(final char[] data, final Class<T> cls) throws IllegalAccessException, InstantiationException {
        final Set<Field> fields = getFields(cls);
        T object = null;

        return object;
    }

    /**
     * Sets the value delimiter
     *
     * @param valueDelimiter the value delimiter
     */
    public void setValueDelimiter(final char valueDelimiter) {
        this.valueDelimiter = valueDelimiter;
    }

    /**
     * Sets the value separator
     *
     * @param valueSeparator the value separator
     */
    public void setValueSeparator(final char valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    /**
     * Writes the collection
     *
     * @param objects the objects to write
     * @param writer  the writer
     * @return Writer
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public Writer write(final Collection<?> objects, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        for (final Object object : objects) {
            write(object, writer);
        }
        return writer;
    }

    @Override
    public Writer write(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        final Class<?> type = object.getClass();

        if (type.isArray()) {
            write(getCollectionFromObject(object), writer);

        } else {
            final Set<Field> fields = getFields(type);
            final Iterator<Field> iterator = fields.iterator();

            while (iterator.hasNext()) {
                final Field field = iterator.next();

                // Add the field value
                writeValue(field.get(object), writer);

                if (iterator.hasNext()) {
                    writer.append(valueSeparator);
                }
            }
        }

        // Add the line separator
        writeLineFeed(writer);

        return writer;
    }

    /**
     * Writes the column headers
     *
     * @param cls    the class to use to get headers
     * @param writer the writer
     * @return Writer
     * @throws IOException
     */
    public Writer writeHeaders(final Class<?> cls, final Writer writer) throws IOException {
        final Set<Field> fields = getFields(cls);
        final Iterator<Field> iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = iterator.next();

            // Add the field name
            writer.append(valueDelimiter);
            writer.append(escapeValue(field.getName()));
            writer.append(valueDelimiter);

            if (iterator.hasNext()) {
                writer.append(valueSeparator);
            }
        }

        // Add the line separator
        writeLineFeed(writer);

        return writer;
    }

    /**
     * Writes a value
     *
     * @param value  the value to write
     * @param writer the writer
     * @return writer
     * @throws IOException
     */
    protected Writer writeValue(final Object value, final Writer writer) throws IOException {
        if (value != null) {
            final Class<?> cls = value.getClass();

            if (cls.equals(String.class) || cls.equals(Date.class)) {
                writer.append(valueDelimiter);
                writer.append(escapeValue(String.valueOf(value)));
                writer.append(valueDelimiter);
            } else {
                writer.append(escapeValue(String.valueOf(value)));
            }
        }
        return writer;
    }
}
