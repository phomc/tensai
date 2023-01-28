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
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.phomc.tensai.util.Serializer;

public class PermissionStorage {
	public static final Serializer<PermissionStorage> SERIALIZER = new Serializer<>() {
		@Override
		public void serialize(PermissionStorage obj, DataOutput stream) throws IOException {
			stream.writeInt(obj.permits.size());

			for (Map.Entry<String, PermissionData> ent : obj.permits.entrySet()) {
				stream.writeUTF(ent.getKey());
				PermissionData.SERIALIZER.serialize(ent.getValue(), stream);
			}
		}

		@Override
		public PermissionStorage deserialize(DataInput stream) throws IOException {
			int size = stream.readInt();
			Map<String, PermissionData> permits = new HashMap<>(size);

			for (int i = 0; i < size; i++) {
				permits.put(stream.readUTF(), PermissionData.SERIALIZER.deserialize(stream));
			}

			return new PermissionStorage(permits);
		}
	};

	private final Map<String, PermissionData> permits;

	public PermissionStorage(@NotNull Map<String, PermissionData> permits) {
		this.permits = permits;
	}

	public PermissionStorage() {
		this(new HashMap<>());
	}

	@NotNull
	public Map<String, PermissionData> getPermits() {
		return permits;
	}

	public boolean isGranted(@NotNull Permission permission, @Nullable String serverAddr) {
		if (permission.getContext() == Permission.Context.SERVER && serverAddr == null) {
			throw new IllegalArgumentException("no server address specified for a server-level permission");
		}

		PermissionData data = permits.get(permission.toString());
		if (data == null) return false;
		if (permission.getContext() == Permission.Context.GLOBAL) return true;
		return data.getPermittedServers().contains(serverAddr);
	}
}
