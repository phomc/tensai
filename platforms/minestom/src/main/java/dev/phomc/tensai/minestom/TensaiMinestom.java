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

package dev.phomc.tensai.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import dev.phomc.tensai.minestom.scheduler.ServerScheduler;
import dev.phomc.tensai.server.TensaiServer;
import dev.phomc.tensai.server.vfx.VisualEffects;

public class TensaiMinestom extends Extension implements TensaiServer {
	private final ServerScheduler scheduler = new ServerScheduler();

	@Override
	public void initialize() {
		scheduler.register();
	}

	@Override
	public void terminate() {
		scheduler.unregister();
	}

	@Override
	public VisualEffects getGlobalVfx() {
		return null;
	}

	@Override
	public ServerScheduler getTaskScheduler() {
		return scheduler;
	}

	@Override
	public boolean isPrimaryThread() {
		return Thread.currentThread().getName().equals(MinecraftServer.THREAD_NAME_TICK_SCHEDULER);
	}
}
