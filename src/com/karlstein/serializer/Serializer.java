
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
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class is used to read and write objects to text format
 *
 * @author Karl STEIN
 */
public abstract class Serializer {

    /**
     * The excluded fields
     */
    private Map<Class<?>, Set<String>> excludeList = new HashMap<Class<?>, Set<String>>();
    /**
     * The included fields
     */
    private Map<Class<?>, Set<String>> includeList = new HashMap<Class<?>, Set<String>>();
    /**
     * The indentation character
     */
    private String indentationCharacter = "  ";
    /**
     * The indentation level
     */
    private int indentationLevel = 0;

    /**
     * Default constructor
     */
    public Serializer() {
    }

    /**
     * Creates a converter with custom indentation character
     *
     * @param indentationCharacter
     */
    public Serializer(final String indentationCharacter) {
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
     * @param cls
     * @param field
     */
    public final void exclude(final Class<?> cls, final String field) {
        if (!excludeList.containsKey(cls)) {
            excludeList.put(cls, new HashSet<String>());
        }
        excludeList.get(cls).add(field);
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
     * Returns the fields that are excluded when reading or writing
     *
     * @return Map
     */
    public final Map<Class<?>, Set<String>> getExcludeList() {
        return excludeList;
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
            if (!includeList.isEmpty()
                    && (!includeList.containsKey(cls)
                    || includeList.get(cls).isEmpty()
                    || !includeList.get(cls).contains(field.getName()))) {
                continue;
            }

            // Check if the field is excluded
            if (!excludeList.isEmpty()
                    && (excludeList.containsKey(cls)
                    && !excludeList.get(cls).isEmpty()
                    && excludeList.get(cls).contains(field.getName()))) {
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
     * Returns the fields that are included when reading or writing
     *
     * @return Map
     */
    public final Map<Class<?>, Set<String>> getIncludeList() {
        return includeList;
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
     * Sets the indentation character
     *
     * @param indentationCharacter
     */
    public final void setIndentationCharacter(final String indentationCharacter) {
        this.indentationCharacter = indentationCharacter;
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
     * Adds the field to the inclusion list
     *
     * @param cls
     * @param field
     */
    public final void include(final Class<?> cls, final String field) {
        if (!includeList.containsKey(cls)) {
            includeList.put(cls, new HashSet<String>());
        }
        includeList.get(cls).add(field);
    }

    /**
     * Increases the indentation level
     */
    protected final void increaseIndentation() {
        indentationLevel++;
    }

    /**
     * Converts the input to an object of the specified class
     *
     * @param reader
     * @param cls
     * @return Object
     */
    public abstract <T> T read(Reader reader, Class<T> cls);

    /**
     * Converts the object to string and write it to the output
     *
     * @param object
     * @param writer
     * @return Writer
     * @throws IOException
     */
    public abstract Writer write(final Object object, final Writer writer) throws IOException, IllegalArgumentException, IllegalAccessException;

    /**
     * Writes the indentation character
     *
     * @param writer
     * @throws IOException
     */
    protected void writeIndentation(final Writer writer) throws IOException {
        for (int i = 0; i < indentationLevel; i++) {
            writer.write(indentationCharacter);
        }
    }
}
