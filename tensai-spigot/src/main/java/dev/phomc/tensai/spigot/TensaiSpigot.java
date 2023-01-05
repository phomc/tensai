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

package dev.phomc.tensai.spigot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.phomc.tensai.Tensai;
import dev.phomc.tensai.clients.ClientHandle;
import dev.phomc.tensai.keybinding.KeyBindingManager;
import dev.phomc.tensai.keybinding.KeyBindingPluginMessage;
import dev.phomc.tensai.keybinding.SimpleKeyBindingManager;
import dev.phomc.tensai.networking.PluginMessage;
import dev.phomc.tensai.spigot.clients.ClientHandleImpl;
import dev.phomc.tensai.spigot.clients.PlayerQuitEventsListener;
import dev.phomc.tensai.spigot.keybinding.KeyBindingPluginMessageListener;
import dev.phomc.tensai.spigot.listener.player.PlayerJoinListener;
import dev.phomc.tensai.spigot.vfx.GlobalVisualEffectsImpl;
import dev.phomc.tensai.vfx.VisualEffects;

public class TensaiSpigot extends JavaPlugin implements Tensai {
	// Wrappers
	private static final Map<UUID, ClientHandle> CLIENTS = new HashMap<>();
	private static TensaiSpigot INSTANCE;
	protected Logger logger;
	private GlobalVisualEffectsImpl globalVfx;
	private KeyBindingManager keyBindingManager;

	public static TensaiSpigot getInstance() {
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
}
