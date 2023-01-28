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

package dev.phomc.tensai.fabric.client;

import java.io.File;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.ClientModInitializer;

import dev.phomc.tensai.fabric.client.iam.ClientAuthorizer;
import dev.phomc.tensai.fabric.client.scheduler.tasks.PermissionLoadTask;
import dev.phomc.tensai.fabric.client.scheduler.tasks.PermissionSaveTask;
import dev.phomc.tensai.scheduler.Scheduler;
import dev.phomc.tensai.server.TensaiServer;

public class TensaiFabricClient implements ClientModInitializer {
	private static TensaiFabricClient INSTANCE;

	public static TensaiFabricClient getInstance() {
		return INSTANCE;
	}

	private File tensaiDir;
	private ClientAuthorizer clientAuthorizer;

	public File getTensaiDirectory() {
		return tensaiDir;
	}

	public ClientAuthorizer getClientAuthorizer() {
		return clientAuthorizer;
	}

	@Override
	public void onInitializeClient() {
		INSTANCE = this;
		tensaiDir = new File(MinecraftClient.getInstance().runDirectory, ".tensai");
		tensaiDir.mkdir();
		clientAuthorizer = new ClientAuthorizer();

		Scheduler taskScheduler = ((TensaiServer) MinecraftClient.getInstance()).getTaskScheduler();
		taskScheduler.schedule(PermissionLoadTask.build());
		taskScheduler.schedule(PermissionSaveTask.build(), 100);
	}
}
