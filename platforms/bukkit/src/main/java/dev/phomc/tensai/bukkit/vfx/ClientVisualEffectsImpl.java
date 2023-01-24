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

package dev.phomc.tensai.bukkit.vfx;

import dev.phomc.tensai.bukkit.client.ClientHandleImpl;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.server.vfx.VisualEffects;
import dev.phomc.tensai.server.vfx.animations.AnimationProperty;
import dev.phomc.tensai.networking.message.s2c.AnimationPlayMessage;

public class ClientVisualEffectsImpl implements VisualEffects {
	private ClientHandleImpl handle;

	public ClientVisualEffectsImpl(ClientHandleImpl handle) {
		this.handle = handle;
	}

	@Override
	public void playAnimationOnce(String type, double startSec, double durationSec, AnimationProperty<?>... properties) {
		handle.sendPluginMessage(Channel.VFX, new AnimationPlayMessage(type, AnimationPlayMessage.PLAY_ONCE, startSec, durationSec, properties).pack());
	}
}
