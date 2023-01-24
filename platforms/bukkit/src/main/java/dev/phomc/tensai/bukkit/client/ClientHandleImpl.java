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

package dev.phomc.tensai.bukkit.client;

import java.lang.ref.WeakReference;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import dev.phomc.tensai.bukkit.TensaiBukkit;
import dev.phomc.tensai.bukkit.vfx.ClientVisualEffectsImpl;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.server.client.ClientHandle;
import dev.phomc.tensai.server.vfx.VisualEffects;

public class ClientHandleImpl implements ClientHandle {
	private TensaiBukkit plugin;
	private WeakReference<Player> playerRef;

	private ClientVisualEffectsImpl vfx;

	public ClientHandleImpl(TensaiBukkit plugin, Player player) {
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(player);
		this.plugin = plugin;
		this.playerRef = new WeakReference<Player>(player);

		this.vfx = new ClientVisualEffectsImpl(this);
	}

	public Player getPlayer() {
		return playerRef.get();
	}

	@Override
	public void sendPluginMessage(Channel channel, byte[] bytes) {
		Player p = getPlayer();
		if (p == null) return; // TODO: player is dereferenced, maybe throw an exception?
		p.sendPluginMessage(plugin, channel.getNamespace(), bytes);
	}

	@Override
	public VisualEffects getVfx() {
		return vfx;
	}
}
