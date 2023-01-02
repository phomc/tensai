/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 VMSA
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

package dev.vmsa.tensai.vfx.animations;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import dev.vmsa.tensai.networking.Serializer;

public class AnimationProperty<T> {
	private String name;
	private T value;

	public AnimationProperty(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static final Serializer<AnimationProperty<?>> SERIALIZER = new Serializer<AnimationProperty<?>>() {
		// Primitives
		private static final int TYPE_INT = 0x00; // byte + short + int + long -> int64
		private static final int TYPE_FLOAT = 0x01; // float + double -> float64
		private static final int TYPE_STRING = 0x02;

		@Override
		public void serialize(AnimationProperty<?> obj, DataOutput stream) throws IOException {
			stream.writeUTF(obj.name);

			if (obj.value instanceof Byte || obj.value instanceof Short || obj.value instanceof Integer || obj.value instanceof Long) {
				stream.writeByte(TYPE_INT);
				stream.writeLong(((Number) obj.value).longValue());
			} else if (obj.value instanceof Float || obj.value instanceof Double) {
				stream.writeByte(TYPE_FLOAT);
				stream.writeDouble(((Number) obj.value).doubleValue());
			} else if (obj.value instanceof String) {
				stream.writeByte(TYPE_STRING);
				stream.writeUTF((String) obj.value);
			}
		}

		@Override
		public AnimationProperty<?> deserialize(DataInput stream) throws IOException {
			String name = stream.readUTF();
			int type = stream.readByte();

			switch (type) {
				case TYPE_INT: return new AnimationProperty<>(name, stream.readLong());
				case TYPE_FLOAT: return new AnimationProperty<>(name, stream.readDouble());
				case TYPE_STRING: return new AnimationProperty<>(name, stream.readUTF());
				default: return null;
			}
		}
	};
}
