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

package dev.vmsa.tensai.spigot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.vmsa.tensai.Tensai;
import dev.vmsa.tensai.clients.ClientHandle;
import dev.vmsa.tensai.spigot.clients.ClientHandleImpl;
import dev.vmsa.tensai.spigot.clients.PlayerQuitEventsListener;
import dev.vmsa.tensai.spigot.networking.AnimationPluginMessage;
import dev.vmsa.tensai.spigot.networking.PluginMessage;
import dev.vmsa.tensai.vfx.animations.AnimationProperty;

public class TensaiSpigot extends JavaPlugin implements Tensai {
	private static TensaiSpigot INSTANCE;

	public static TensaiSpigot getInstance() {
		return INSTANCE;
	}

	protected Logger logger;

	@Override
	public void onEnable() {
		logger = getLogger();
		INSTANCE = this;

		// Plugin messaging channels
		getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessage.VFX_CHANNEL);

		// Events
		getServer().getPluginManager().registerEvents(new PlayerQuitEventsListener(), this);
	}

	@Override
	public void onDisable() {
	}

	// Wrappers
	private static final Map<UUID, ClientHandle> CLIENTS = new HashMap<>();

	public static ClientHandle of(Player player) {
		UUID uuid = player.getUniqueId();
		if (!CLIENTS.containsKey(uuid)) CLIENTS.put(uuid, new ClientHandleImpl(INSTANCE, player));
		return CLIENTS.get(uuid);
	}

	public static void internalReset(Player player) {
		CLIENTS.remove(player.getUniqueId());
	}

	// APIs
	@Override
	public void playAnimationOnce(String type, double startSec, double durationSec, AnimationProperty<?>... properties) {
		AnimationPluginMessage message = new AnimationPluginMessage(type, AnimationPluginMessage.PLAY_ONCE, startSec, durationSec, properties);
		getServer().sendPluginMessage(INSTANCE, message.channel, message.createBytes());
	}
}
