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

package dev.vmsa.tensai.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import dev.vmsa.tensai.networking.Serializer;
import dev.vmsa.tensai.vfx.animations.AnimationProperty;

/**
 * <p>Simple implementation of 4-components vector. This class does not contains any calculation methods, such
 * as {@code add()} or {@code cross()}.</p>
 * <p>This class is mainly used for {@link AnimationProperty}.</p>
 *
 */
public class Vec4 {
	private double x, y, z, w;

	public Vec4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vec4(double scalar) {
		this(scalar, scalar, scalar, scalar);
	}

	public Vec4() {
		this(0);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getW() {
		return w;
	}

	@Override
	public String toString() {
		return "Vec4 [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
	}

	public static final Serializer<Vec4> SERIALIZER = new Serializer<Vec4>() {
		@Override
		public void serialize(Vec4 obj, DataOutput stream) throws IOException {
			stream.writeDouble(obj.x);
			stream.writeDouble(obj.y);
			stream.writeDouble(obj.z);
			stream.writeDouble(obj.w);
		}

		@Override
		public Vec4 deserialize(DataInput stream) throws IOException {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double z = stream.readDouble();
			double w = stream.readDouble();
			return new Vec4(x, y, z, w);
		}
	};
}
