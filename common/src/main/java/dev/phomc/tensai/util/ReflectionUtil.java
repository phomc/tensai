/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 PhoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.phomc.tensai.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class provides utility methods for interacting with Java Reflection.
 */
public class ReflectionUtil {
	/**
	 * Returns the value of a field.<br>
	 * This method explores all "public" fields in the given class's hierarchy including all fields
	 * (regardless of accessibility) of the given class.<br>
	 * If the {@code object} argument is {@code null}, the field must be static.
	 *
	 * @param clazz  the class represents the object
	 * @param object the object which contains the value of the field
	 * @param field  the name of the field
	 * @return the value of the field
	 */
	@Nullable
	public static Object getFieldValue(@NotNull Class<?> clazz, @Nullable Object object, @NotNull String field) {
		try {
			Field f = clazz.getField(field);
			f.setAccessible(true);
			return f.get(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns the value of a declared field.<br>
	 * This method explores all fields (regardless of accessibility) of the given class.<br>
	 * If the {@code object} argument is {@code null}, the field must be static.
	 *
	 * @param clazz  the class represents the object
	 * @param object the object which contains the value of the field
	 * @param field  the name of the field
	 * @return the value of the field
	 */
	@Nullable
	public static Object getDeclaredFieldValue(@NotNull Class<?> clazz, @Nullable Object object, @NotNull String field) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			return f.get(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Sets a field's value.<br>
	 * This method explores all "public" fields in the given class's hierarchy including all fields
	 * (regardless of accessibility) of the given class.<br>
	 * If the {@code object} argument is {@code null}, the field must be static.
	 *
	 * @param clazz  the class represents the object
	 * @param object the object which contains the value of the field
	 * @param field  the name of the field
	 * @param value  the new value
	 */
	public static void setFieldValue(@NotNull Class<?> clazz, @Nullable Object object, @NotNull String field, @Nullable Object value) {
		try {
			Field f = clazz.getField(field);
			f.setAccessible(true);
			f.set(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a declared-field's value.<br>
	 * This method explores all fields (regardless of accessibility) of the given class.<br>
	 * If the {@code object} argument is {@code null}, the field must be static.
	 *
	 * @param clazz  the class represents the object
	 * @param object the object which contains the value of the field
	 * @param field  the name of the field
	 * @param value  the new value
	 */
	public static void setDeclaredField(@NotNull Class<?> clazz, @Nullable Object object, @NotNull String field, @Nullable Object value) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Explores all fields (regardless of accessibility) in the given class's hierarchy.<br>
	 * The search only performs on superclasses.
	 *
	 * @param clazz the class in the class hierarchy
	 * @return list of fields
	 */
	@NotNull
	public static List<Field> exploreFields(@NotNull Class<?> clazz) {
		List<Field> fields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
		Class<?> x;

		while ((x = clazz.getDeclaringClass()) != null && !(clazz = x.getSuperclass()).equals(Object.class)) {
			fields.addAll(List.of(clazz.getDeclaredFields()));
		}

		return fields;
	}
}
