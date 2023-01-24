/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 PhoMC
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

package dev.phomc.tensai.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.server.MinecraftServer;

import dev.phomc.tensai.fabric.scheduler.ServerScheduler;
import dev.phomc.tensai.fabric.vfx.GlobalVisualEffectsImpl;
import dev.phomc.tensai.scheduler.Scheduler;
import dev.phomc.tensai.server.Tensai;
import dev.phomc.tensai.server.keybinding.KeyBindingManager;
import dev.phomc.tensai.server.keybinding.SimpleKeyBindingManager;
import dev.phomc.tensai.server.vfx.VisualEffects;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements Tensai {
	@Unique
	private GlobalVisualEffectsImpl globalVfx;

	@Unique
	private KeyBindingManager keyBindingManager;

	@Unique
	private ServerScheduler serverScheduler;

	@Override
	public VisualEffects getGlobalVfx() {
		if (globalVfx == null) globalVfx = new GlobalVisualEffectsImpl((MinecraftServer) (Object) this);
		return globalVfx;
	}

	@Override
	public KeyBindingManager getKeyBindingManager() {
		if (keyBindingManager == null) keyBindingManager = new SimpleKeyBindingManager();
		return keyBindingManager;
	}

	@Override
	public Scheduler getTaskScheduler() {
		if (serverScheduler == null) serverScheduler = new ServerScheduler();
		return serverScheduler;
	}
}
