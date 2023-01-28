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

package dev.phomc.tensai.fabric.client.scheduler.tasks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import dev.phomc.tensai.fabric.client.TensaiFabricClient;
import dev.phomc.tensai.fabric.client.iam.PermissionStorage;
import dev.phomc.tensai.scheduler.Task;

public class PermissionLoadTask implements Runnable {
	public static Task build() {
		return new Task.Builder().async().setExecutor(new PermissionLoadTask()).build();
	}

	@Override
	public void run() {
		File file = new File(TensaiFabricClient.getInstance().getTensaiDirectory(), "permit.bin");
		if (!file.exists()) return;

		try {
			byte[] data = FileUtils.readFileToByteArray(file);
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			PermissionStorage st = PermissionStorage.SERIALIZER.deserialize(new DataInputStream(stream));
			TensaiFabricClient.getInstance().getClientAuthorizer().getPersistentStorage().getPermits().putAll(st.getPermits());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
