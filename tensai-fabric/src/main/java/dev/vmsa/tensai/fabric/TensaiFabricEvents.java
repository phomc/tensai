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

package dev.vmsa.tensai.fabric;

import java.io.IOException;

import net.minecraft.server.MinecraftServer;

import dev.vmsa.tensai.Tensai;
import dev.vmsa.tensai.utils.EventEmitter;

public class TensaiFabricEvents {
	public static final EventEmitter<MinecraftServer> SERVER_STARTING = new EventEmitter<>();
	public static final EventEmitter<Tensai> INSTANCE_CREATE = SERVER_STARTING.then(server -> {
		TensaiFabricInstance instance = new TensaiFabricInstance(server);
		TensaiFabric.INSTANCES.put(server, instance);
		if (!Tensai.isInstancePresent()) Tensai.createInstance(() -> instance);
		return instance;
	});

	public static final EventEmitter<MinecraftServer> SERVER_STOPPING = new EventEmitter<>();
	public static final EventEmitter<Tensai> INSTANCE_STOPPING = SERVER_STOPPING.then(server -> {
		Tensai instance = TensaiFabric.INSTANCES.get(server);
		return instance;
	});

	public static final EventEmitter<MinecraftServer> SERVER_STOPPED = new EventEmitter<>();

	static {
		SERVER_STOPPED.listen(server -> {
			try {
				Tensai instance = TensaiFabric.INSTANCES.get(server);
				instance.close();
			} catch (IOException e) {
				TensaiFabric.LOGGER.error("Failed to close TensaiFabricInstance: An exception thrown");
				e.printStackTrace();
				TensaiFabric.LOGGER.info("Please create a new issue in https://github.com/vmsa-dev/tensai/issues");
				TensaiFabric.LOGGER.info("Additionally, if you are using /reload, please restart the server to avoid memory leaks.");
			} finally {
				TensaiFabric.INSTANCES.remove(server);
			}
		});
	}
}
