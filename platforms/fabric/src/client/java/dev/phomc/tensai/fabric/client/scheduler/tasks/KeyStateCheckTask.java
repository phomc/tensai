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

import java.util.Map;

import net.minecraft.client.MinecraftClient;

import dev.phomc.tensai.fabric.client.keybinding.KeyBindingManager;
import dev.phomc.tensai.fabric.client.networking.ClientPublisher;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.c2s.KeyBindingStateUpdate;
import dev.phomc.tensai.scheduler.Task;

public class KeyStateCheckTask implements Runnable {
	public static Task build() {
		return new Task.Builder()
				.setInterval(KeyBindingManager.getInstance().getInputDelay())
				.setPriority(10)
				.setInfiniteRecurrence()
				.setExecutor(new KeyStateCheckTask()).build();
	}

	@Override
	public void run() {
		if (MinecraftClient.getInstance().getNetworkHandler() == null) return;
		Map<Key, KeyState> states = KeyBindingManager.getInstance().fetchStates();

		if (!states.isEmpty()) {
			ClientPublisher.publish(Channel.KEYBINDING, new KeyBindingStateUpdate(states));
		}
	}
}
