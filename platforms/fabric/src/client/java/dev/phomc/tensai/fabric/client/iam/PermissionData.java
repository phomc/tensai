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

package dev.phomc.tensai.fabric.client.iam;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import dev.phomc.tensai.util.Serializer;

public class PermissionData {
	public static final Serializer<PermissionData> SERIALIZER = new Serializer<>() {
		@Override
		public void serialize(PermissionData obj, DataOutput stream) throws IOException {
			stream.writeInt(obj.permittedServers.size());

			for (String str : obj.permittedServers) {
				stream.writeUTF(str);
			}
		}

		@Override
		public PermissionData deserialize(DataInput stream) throws IOException {
			int size = stream.readInt();
			Set<String> permittedServers = new HashSet<>(size);

			for (int i = 0; i < size; i++) {
				permittedServers.add(stream.readUTF());
			}

			return new PermissionData(permittedServers);
		}
	};

	private final Set<String> permittedServers;

	public PermissionData(@NotNull Set<String> permittedServers) {
		this.permittedServers = permittedServers;
	}

	public PermissionData() {
		this(new HashSet<>());
	}

	@NotNull
	public Set<String> getPermittedServers() {
		return permittedServers;
	}
}
