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

package dev.vmsa.tensai.spigot.players;

import java.lang.ref.WeakReference;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import dev.vmsa.tensai.animations.AnimationProperty;
import dev.vmsa.tensai.players.PlayerHandle;
import dev.vmsa.tensai.spigot.TensaiSpigot;
import dev.vmsa.tensai.spigot.networking.AnimationPluginMessage;
import dev.vmsa.tensai.spigot.networking.PluginMessage;

public class PlayerHandleImpl implements PlayerHandle {
	private TensaiSpigot plugin;
	private WeakReference<Player> playerRef;

	public PlayerHandleImpl(TensaiSpigot plugin, Player player) {
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(player);
		this.plugin = plugin;
		this.playerRef = new WeakReference<Player>(player);
	}

	public Player getPlayer() {
		return playerRef.get();
	}

	public void sendPluginMessage(PluginMessage message) {
		Player p = getPlayer();
		if (p == null) return; // TODO: player is dereferenced, maybe throw an exception?
		p.sendPluginMessage(plugin, message.channel, message.createBytes());
	}

	@Override
	public void playAnimationOnce(String type, double startSec, double durationSec, AnimationProperty<?>... properties) {
		sendPluginMessage(new AnimationPluginMessage(type, AnimationPluginMessage.PLAY_ONCE, startSec, durationSec, properties));
	}
}
