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

package dev.phomc.tensai.bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.phomc.tensai.bukkit.client.ClientHandleImpl;
import dev.phomc.tensai.bukkit.client.PlayerQuitEventsListener;
import dev.phomc.tensai.bukkit.keybinding.KeyBindingPluginMessageListener;
import dev.phomc.tensai.bukkit.listener.player.PlayerJoinListener;
import dev.phomc.tensai.bukkit.scheduler.ServerScheduler;
import dev.phomc.tensai.bukkit.vfx.GlobalVisualEffectsImpl;
import dev.phomc.tensai.scheduler.Scheduler;
import dev.phomc.tensai.server.TensaiServer;
import dev.phomc.tensai.server.client.ClientHandle;
import dev.phomc.tensai.server.keybinding.KeyBindingManager;
import dev.phomc.tensai.server.keybinding.KeyBindingPluginMessage;
import dev.phomc.tensai.server.keybinding.SimpleKeyBindingManager;
import dev.phomc.tensai.server.networking.PluginMessage;
import dev.phomc.tensai.server.vfx.VisualEffects;

public class TensaiBukkit extends JavaPlugin implements TensaiServer {
	// Wrappers
	private static final Map<UUID, ClientHandle> CLIENTS = new HashMap<>();
	private static TensaiBukkit INSTANCE;
	protected Logger logger;
	private GlobalVisualEffectsImpl globalVfx;
	private KeyBindingManager keyBindingManager;
	private Scheduler scheduler;

	public static TensaiBukkit getInstance() {
		return INSTANCE;
	}

	public static ClientHandle getClient(Player player) {
		UUID uuid = player.getUniqueId();
		if (!CLIENTS.containsKey(uuid)) CLIENTS.put(uuid, new ClientHandleImpl(INSTANCE, player));
		return CLIENTS.get(uuid);
	}

	public static void internalReset(Player player) {
		CLIENTS.remove(player.getUniqueId());
	}

	@Override
	public void onEnable() {
		logger = getLogger();
		INSTANCE = this;

		// Plugin messaging channels
		getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessage.CHANNEL_VFX);
		getServer().getMessenger().registerOutgoingPluginChannel(this, KeyBindingPluginMessage.CHANNEL);

		getServer().getMessenger().registerIncomingPluginChannel(this, KeyBindingPluginMessage.CHANNEL, new KeyBindingPluginMessageListener(this));

		// Events
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitEventsListener(), this);

		globalVfx = new GlobalVisualEffectsImpl(this);
		keyBindingManager = new SimpleKeyBindingManager();
		scheduler = new ServerScheduler(this);
	}

	@Override
	public void onDisable() {
	}

	public void sendPluginMessageToPlayer(Player player, PluginMessage message) {
		player.sendPluginMessage(this, message.channel, message.createBytes());
	}

	// APIs
	@Override
	public VisualEffects getGlobalVfx() {
		return globalVfx;
	}

	@Override
	public KeyBindingManager getKeyBindingManager() {
		return keyBindingManager;
	}

	@Override
	public Scheduler getTaskScheduler() {
		return scheduler;
	}

	@Override
	public boolean isPrimaryThread() {
		return getServer().isPrimaryThread();
	}
}
