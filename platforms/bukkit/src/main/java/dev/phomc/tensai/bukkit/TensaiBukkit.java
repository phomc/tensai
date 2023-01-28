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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.phomc.tensai.bukkit.client.ClientHandleImpl;
import dev.phomc.tensai.bukkit.event.listeners.PlayerChannelListener;
import dev.phomc.tensai.bukkit.event.listeners.PlayerJoinListener;
import dev.phomc.tensai.bukkit.event.listeners.PlayerQuitListener;
import dev.phomc.tensai.bukkit.keybinding.KeyBindingMessageSubscriber;
import dev.phomc.tensai.bukkit.networking.ServerSubscriber;
import dev.phomc.tensai.bukkit.scheduler.ServerScheduler;
import dev.phomc.tensai.bukkit.vfx.GlobalVisualEffectsImpl;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.scheduler.Scheduler;
import dev.phomc.tensai.server.TensaiServer;
import dev.phomc.tensai.server.client.ClientHandle;
import dev.phomc.tensai.server.keybinding.KeyBindingManager;
import dev.phomc.tensai.server.keybinding.SimpleKeyBindingManager;
import dev.phomc.tensai.server.vfx.VisualEffects;

public class TensaiBukkit extends JavaPlugin implements TensaiServer {
	// Wrappers
	private static final Map<UUID, ClientHandle> CLIENTS = new HashMap<>();
	private static TensaiBukkit INSTANCE;
	public static final Logger LOGGER = LoggerFactory.getLogger("tensai");
	private GlobalVisualEffectsImpl globalVfx;
	private KeyBindingManager keyBindingManager;
	private Scheduler scheduler;

	public static TensaiBukkit getInstance() {
		return INSTANCE;
	}

	public static ClientHandle getClient(Player player) {
		return CLIENTS.computeIfAbsent(player.getUniqueId(), u -> new ClientHandleImpl(INSTANCE, player));
	}

	public static void internalReset(Player player) {
		CLIENTS.remove(player.getUniqueId());
	}

	@Override
	public void onEnable() {
		INSTANCE = this;

		// Plugin messaging channels
		for (Channel channel : Channel.values()) {
			getServer().getMessenger().registerOutgoingPluginChannel(this, channel.getNamespace());
		}

		registerIncomingMessenger(new KeyBindingMessageSubscriber(Channel.KEYBINDING));

		// Events
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerChannelListener(this), this);

		globalVfx = new GlobalVisualEffectsImpl(this);
		keyBindingManager = new SimpleKeyBindingManager();
		scheduler = new ServerScheduler(this);
	}

	@Override
	public void onDisable() {
	}

	private void registerIncomingMessenger(ServerSubscriber subscriber) {
		getServer().getMessenger().registerIncomingPluginChannel(this, subscriber.getChannel().getNamespace(), subscriber);
		subscriber.onInitialize();
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
