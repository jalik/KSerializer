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
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class is used to read and write objects to text format
 *
 * @author Karl STEIN
 */
public abstract class KSerializer {

    /**
     * The excluded fields
     */
    protected final Map<Class<?>, Set<String>> excludedFields = new HashMap<Class<?>, Set<String>>();
    /**
     * The excluded types
     */
    protected final Map<Class<?>, Set<Class<?>>> excludedTypes = new HashMap<Class<?>, Set<Class<?>>>();
    /**
     * The compress output option
     */
    protected boolean compressOutput = false;
    /**
     * The included fields
     */
    protected final Map<Class<?>, Set<String>> includeFields = new HashMap<Class<?>, Set<String>>();
    /**
     * The included types
     */
    protected final Map<Class<?>, Set<Class<?>>> includedTypes = new HashMap<Class<?>, Set<Class<?>>>();
    /**
     * The indentation character
     */
    protected String indentationCharacter = "  ";
    /**
     * The indentation level
     */
    protected int indentationLevel = 0;
    /**
     * The line separator
     */
    protected String lineSeparator = System.getProperty("line.separator");

    /**
     * Default constructor
     */
    public KSerializer() {
    }

    /**
     * Creates a converter with custom indentation character
     *
     * @param indentationCharacter
     */
    public KSerializer(final String indentationCharacter) {
        this.indentationCharacter = indentationCharacter;
    }

    /**
     * Checks if the field should be converted
     *
     * @param field
     * @return boolean
     */
    protected boolean checkField(final Field field) {
        final int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers);
    }

    /**
     * Decreases the indentation level
     */
    protected final void decreaseIndentation() {
        indentationLevel--;
    }

    /**
     * Adds the field to the exclusion list
     *
     * @param field
     * @param cls
     */
    public final void excludeField(final String field, final Class<?> cls) {
        if (!excludedFields.containsKey(cls)) {
            excludedFields.put(cls, new HashSet<String>());
        }
        excludedFields.get(cls).add(field);
    }

    /**
     * Adds the type to the exclusion list
     *
     * @param type
     * @param cls
     */
    public final void excludeType(final Class<?> type, final Class<?> cls) {
        if (!excludedTypes.containsKey(cls)) {
            excludedTypes.put(cls, new HashSet<Class<?>>());
        }
        excludedTypes.get(cls).add(type);
    }

    /**
     * Converts the array to a collection
     *
     * @param object
     * @return Collection
     */
    protected Collection<?> getCollectionFromObject(final Object object) {
        final Class<?> cls = object.getClass();
        final Collection<Object> list = new ArrayList<Object>();

        if (cls.getSimpleName().equals("boolean[]")) {
            final boolean[] array = (boolean[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("byte[]")) {
            final byte[] array = (byte[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("char[]")) {
            final char[] array = (char[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("double[]")) {
            final double[] array = (double[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("float[]")) {
            final float[] array = (float[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("int[]")) {
            final int[] array = (int[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("long[]")) {
            final long[] array = (long[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else if (cls.getSimpleName().equals("short[]")) {
            final short[] array = (short[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        } else {
            final Object[] array = (Object[]) object;
            for (final Object element : array) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * Returns the excluded fields
     *
     * @return Map
     */
    public final Map<Class<?>, Set<String>> getExcludedFields() {
        return excludedFields;
    }

    /**
     * Returns the excluded types
     *
     * @return Map
     */
    public Map<Class<?>, Set<Class<?>>> getExcludedTypes() {
        return excludedTypes;
    }

    /**
     * Returns the object fields that can be converted
     *
     * @param cls
     * @return Set
     * @throws SecurityException
     */
    protected final Set<Field> getFields(final Class<?> cls) throws SecurityException {
        final Set<Field> fields = new HashSet<Field>();
        final Class<?> parent = cls.getSuperclass();
        final Field[] declaredFields = cls.getDeclaredFields();

        for (final Field field : declaredFields) {
            // Check if the field is included
            if (includeFields.containsKey(cls) && (includeFields.get(cls).isEmpty()
                    || !includeFields.get(cls).contains(field.getName()))) {
                continue;
            }

            // Check if the field is excluded
            if (excludedFields.containsKey(cls) && excludedFields.get(cls).contains(field.getName())) {
                continue;
            }

            if (checkField(field)) {
                // If the field can be converted,
                // then add it to the list
                field.setAccessible(true);
                fields.add(field);
            }
        }

        // Get inherited fields
        if (parent != null && !parent.equals(Object.class)) {
            fields.addAll(getFields(parent));
        }

        return fields;
    }

    /**
     * Returns the included fields
     *
     * @return Map
     */
    public final Map<Class<?>, Set<String>> getIncludeFields() {
        return includeFields;
    }

    /**
     * Returns the included types
     *
     * @return Map
     */
    public Map<Class<?>, Set<Class<?>>> getIncludedTypes() {
        return includedTypes;
    }

    /**
     * Returns the indentation character
     *
     * @return String
     */
    protected final String getIndentationCharacter() {
        return indentationCharacter;
    }

    /**
     * Returns the indentation level
     *
     * @return String
     */
    protected final int getIndentationLevel() {
        return indentationLevel;
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
     * Adds the field to the inclusion list
     *
     * @param field
     * @param cls
     */
    public final void includeField(final String field, final Class<?> cls) {
        if (!includeFields.containsKey(cls)) {
            includeFields.put(cls, new HashSet<String>());
        }
        includeFields.get(cls).add(field);
    }

    /**
     * Adds the type to the inclusion list
     *
     * @param type
     * @param cls
     */
    public final void includeType(final Class<?> type, final Class<?> cls) {
        if (!includedTypes.containsKey(cls)) {
            includedTypes.put(cls, new HashSet<Class<?>>());
        }
        includedTypes.get(cls).add(type);
    }

    /**
     * Increases the indentation level
     */
    protected final void increaseIndentation() {
        indentationLevel++;
    }

    /**
     * Returns the compress output option
     *
     * @return boolean
     */
    public boolean isCompressOutput() {
        return compressOutput;
    }

    /**
     * Sets the compress output option
     *
     * @param compressOutput
     */
    public void setCompressOutput(boolean compressOutput) {
        this.compressOutput = compressOutput;
    }

    /**
     * Sets the indentation character
     *
     * @param indentationCharacter
     */
    public final void setIndentationCharacter(final String indentationCharacter) {
        this.indentationCharacter = indentationCharacter;
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
     * Converts the object to string and write it to the output
     *
     * @param object
     * @param writer
     * @return Writer
     * @throws IOException
     */
    public abstract Writer write(Object object, Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException;

    /**
     * Writes the indentation character
     *
     * @param writer
     * @return Writer
     * @throws IOException
     */
    protected Writer writeIndentation(final Writer writer) throws IOException {
        if (!compressOutput) {
            for (int i = 0; i < indentationLevel; i++) {
                writer.write(indentationCharacter);
            }
        }
        return writer;
    }

    /**
     * Writes a new line character
     *
     * @param writer
     * @return Writer
     * @throws IOException
     */
    protected Writer writeLineFeed(final Writer writer) throws IOException {
        writer.write("\n");
        return writer;
    }

    /**
     * Writes a single space character
     *
     * @param writer
     * @return Writer
     * @throws IOException
     */
    protected Writer writeSpace(final Writer writer) throws IOException {
        writer.write(" ");
        return writer;
    }
}
