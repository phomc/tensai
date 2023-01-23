/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 PhoMC
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

package dev.phomc.tensai.tests.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dev.phomc.tensai.utils.Vec4;
import dev.phomc.tensai.vfx.animations.AnimationProperty;

class SerializationTest {
	@Test
	void testVec4() throws IOException {
		Vec4 vec = new Vec4(1, 2, 3, 4);
		byte[] vecBinary = Utils.createBytes(stream -> Vec4.SERIALIZER.serialize(vec, stream));
		assertEquals(vec, Vec4.SERIALIZER.deserialize(Utils.createStream(vecBinary)));
	}

	void checkAnimationProperty(Class<?> expectedType, AnimationProperty<?> expected, AnimationProperty<?> actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getValue(), actual.getValue());
		assertEquals(expectedType, actual.getValue().getClass());
	}

	// Exact
	void animationPropExact(Class<?> type, AnimationProperty<?> prop) throws IOException {
		byte[] propBinary = Utils.createBytes(stream -> AnimationProperty.SERIALIZER.serialize(prop, stream));
		AnimationProperty<?> deserialized = AnimationProperty.SERIALIZER.deserialize(Utils.createStream(propBinary));
		assertEquals(prop.getName(), deserialized.getName());
		assertEquals(prop.getValue(), deserialized.getValue());
		assertEquals(type, deserialized.getValue().getClass());
	}

	@Test
	void testAnimationPropertyLong() throws IOException {
		animationPropExact(Long.class, new AnimationProperty<>("prop", 0xFFFFFFFFFFFFFFFFL));
	}

	@Test
	void testAnimationPropertyDouble() throws IOException {
		animationPropExact(Double.class, new AnimationProperty<>("prop", 3.1415926535898D));
	}

	@Test
	void testAnimationPropertyString() throws IOException {
		animationPropExact(String.class, new AnimationProperty<>("prop", "Hello world"));
	}

	@Test
	void testAnimationPropertyVec4() throws IOException {
		animationPropExact(Vec4.class, new AnimationProperty<>("prop", new Vec4(1, 2, 3, 4)));
	}

	// Casted
	void animationPropCasted(Class<?> type, AnimationProperty<?> prop, Object value) throws IOException {
		byte[] propBinary = Utils.createBytes(stream -> AnimationProperty.SERIALIZER.serialize(prop, stream));
		AnimationProperty<?> deserialized = AnimationProperty.SERIALIZER.deserialize(Utils.createStream(propBinary));
		assertEquals(prop.getName(), deserialized.getName());
		assertEquals(type, deserialized.getValue().getClass());
		assertEquals(value, deserialized.getValue());
	}

	@Test
	void testAnimationPropertyByte() throws IOException {
		animationPropCasted(Long.class, new AnimationProperty<>("prop", (byte) 127), 127L);
	}

	@Test
	void testAnimationPropertyShort() throws IOException {
		animationPropCasted(Long.class, new AnimationProperty<>("prop", (short) 32767), 32767L);
	}

	@Test
	void testAnimationPropertyInt() throws IOException {
		animationPropCasted(Long.class, new AnimationProperty<>("prop", -1), -1L);
	}

	@Test
	void testAnimationPropertyFloat() throws IOException {
		AnimationProperty<Float> prop = new AnimationProperty<>("prop", 3.1415926535898F);
		byte[] propBinary = Utils.createBytes(stream -> AnimationProperty.SERIALIZER.serialize(prop, stream));
		AnimationProperty<?> deserialized = AnimationProperty.SERIALIZER.deserialize(Utils.createStream(propBinary));

		assertEquals(prop.getName(), deserialized.getName());
		assertEquals(Double.class, deserialized.getValue().getClass());
	}
}
