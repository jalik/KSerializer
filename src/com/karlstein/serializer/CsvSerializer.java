
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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is used to read/write CSV data
 *
 * @author Karl STEIN
 */
public class CsvSerializer extends Serializer {

    /**
     * The line separator
     */
    private String lineSeparator = System.getProperty("line.separator");
    /**
     * The value delimiter
     */
    private String valueDelimiter = "\"";
    /**
     * The value separator
     */
    private String valueSeparator = ",";

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
    public CsvSerializer(final String delimiter, final String separator) {
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
            if (value.indexOf(valueDelimiter) > 0) {
                value = value.replace(valueDelimiter, valueDelimiter + valueDelimiter);
            }
            // Removes the line separator
            value = value.replaceAll("\\r|\\n", "");
        }
        return value;
    }

    /**
     * Returns the line separator
     *
     * @return String
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Returns the value delimiter
     *
     * @return String
     */
    public String getValueDelimiter() {
        return valueDelimiter;
    }

    /**
     * Returns the value separator
     *
     * @return String
     */
    public String getValueSeparator() {
        return valueSeparator;
    }

    @Override
    public <T> T read(final Reader data, final Class<T> cls) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Sets the line separator
     *
     * @param lineSeparator the line separator
     */
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Sets the value delimiter
     *
     * @param valueDelimiter the value delimiter
     */
    public void setValueDelimiter(final String valueDelimiter) {
        this.valueDelimiter = valueDelimiter;
    }

    /**
     * Sets the value separator
     *
     * @param valueSeparator the value separator
     */
    public void setValueSeparator(final String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    /**
     * Writes the collection
     *
     * @param objects the objects to write
     * @param writer  the writer
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void write(final Collection<?> objects, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException {
        boolean writeHeaders = true;

        for (final Object object : objects) {
            if (writeHeaders) {
                // Add the column headers
                writeHeaders(object, writer);
                writeHeaders = false;
            }
            write(object, writer);
        }
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
        writer.append(lineSeparator);

        return writer;
    }

    /**
     * Writes the column headers
     *
     * @param object the object to use to get headers
     * @param writer the writer
     * @throws IOException
     */
    public void writeHeaders(final Object object, final Writer writer) throws IOException {
        final Set<Field> fields = getFields(object.getClass());
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
        writer.append(lineSeparator);
    }

    /**
     * Writes the value
     *
     * @param value  the value to write
     * @param writer the writer
     * @throws IOException
     */
    protected void writeValue(final Object value, final Writer writer) throws IOException {
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
    }
}
