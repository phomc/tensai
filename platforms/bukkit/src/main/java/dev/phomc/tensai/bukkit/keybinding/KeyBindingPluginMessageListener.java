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

package dev.phomc.tensai.bukkit.keybinding;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import dev.phomc.tensai.bukkit.TensaiBukkit;
import dev.phomc.tensai.bukkit.event.KeyPressEvent;
import dev.phomc.tensai.server.keybinding.KeyBindingPluginMessage;

public class KeyBindingPluginMessageListener implements PluginMessageListener {
	private final TensaiBukkit tensai;

	public KeyBindingPluginMessageListener(TensaiBukkit tensai) {
		this.tensai = tensai;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(KeyBindingPluginMessage.CHANNEL)) return;

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String key = in.readUTF();

			tensai.getServer().getPluginManager().callEvent(new KeyPressEvent(player, tensai.getKeyBindingManager().getKeyBindings().get(key)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
