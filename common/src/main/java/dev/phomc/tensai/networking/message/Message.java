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

package dev.phomc.tensai.networking.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.phomc.tensai.networking.Channel;

/**
 * Represents a message.<br>
 * A message is basically a packet which is delivered between client and server.
 */
public abstract class Message {
	protected final byte id;

	/**
	 * Constructs a message.
	 * @param id message id
	 */
	public Message(byte id) {
		this.id = id;
	}

	public void write(DataOutput stream) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void read(DataInput stream) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unpacks the given data and write out to this message.
	 * @param bytes data
	 */
	public void unpack(byte[] bytes) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		DataInputStream wrapped = new DataInputStream(stream);

		try {
			if (wrapped.readByte() != id) {
				throw new IllegalAccessException("unexpected message id");
			}

			read(wrapped);
		} catch (IOException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Packs the current message into a byte array.
	 * @return data
	 */
	public byte[] pack() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream wrapped = new DataOutputStream(stream);

		try {
			wrapped.writeByte(id);
			write(wrapped);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return stream.toByteArray();
	}
}
