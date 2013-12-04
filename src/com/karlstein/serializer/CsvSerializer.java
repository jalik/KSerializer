
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
public class CsvSerializer extends Serializer {

    /**
     * The line separator
     */
    private String lineSeparator = System.getProperty("line.separator");
    /**
     * The value delimiter
     */
    private String valueDelimiter = "";
    /**
     * The value separator
     */
    private String valueSeparator = "|";

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
        String line = null;

        do {
            // Get the line
            line = reader.readLine();

            if (line != null) {
                // Remove the first value delimiter
                if (line.indexOf(valueDelimiter) == 0) {
                    line = line.substring(valueDelimiter.length());
                }

                // Remove the last value delimiter
                if (line.lastIndexOf(valueDelimiter) == line.length()) {
                    line = line.substring(0, line.length() - valueDelimiter.length());
                }

                // Split the values
                final String[] values = line.split(valueDelimiter + "\\" + valueSeparator + valueDelimiter);

                try {
                    final T object = read(cls, values);

                    if (object != null) {
                        objects.add(object);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        } while (line != null);

        return objects;
    }

    /**
     * Returns an instance of the class using the values
     *
     * @param cls
     * @param values
     * @param <T>
     * @return Object
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected <T> T read(final Class<T> cls, final String[] values) throws IllegalAccessException, InstantiationException {
        final Set<Field> fields = getFields(cls);
        T object = null;
        int i = -1;

        if (values != null && values.length == fields.size()) {
            object = cls.newInstance();

            for (final Field field : fields) {
                final Class<?> type = field.getType();
                i += 1;

                if (type.isPrimitive()) {
                    field.set(object, values[i]);

                } else if (type.equals(Date.class)) {
//                    field.set(object, new Date(values[i]));
                }
            }
        }
        return object;
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
        writer.append(lineSeparator);

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
        writer.append(lineSeparator);

        return writer;
    }

    /**
     * Writes a new line
     *
     * @param writer the writer
     * @return Writer
     * @throws IOException
     */
    public Writer writeNewLine(final Writer writer) throws IOException {
        return writer.append(lineSeparator);
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
