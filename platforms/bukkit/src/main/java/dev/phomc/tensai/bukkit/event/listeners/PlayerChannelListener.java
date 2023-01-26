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

package dev.phomc.tensai.bukkit.event.listeners;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChannelEvent;

import dev.phomc.tensai.bukkit.TensaiBukkit;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;

public class PlayerChannelListener implements Listener {
	private final TensaiBukkit tensai;

	public PlayerChannelListener(TensaiBukkit tensai) {
		this.tensai = tensai;
	}

	@EventHandler
	public void regChannel(PlayerChannelEvent event) {
		Player player = event.getPlayer();

		if (event.getChannel().equals(Channel.KEYBINDING.getNamespace())) {
			Collection<KeyBinding> keyBindings = tensai.getKeyBindingManager().getKeyBindings().values();

			if (!keyBindings.isEmpty()) {
				TensaiBukkit.getClient(player).sendPluginMessage(Channel.KEYBINDING, new KeyBindingRegisterMessage(
						TensaiBukkit.getInstance().getKeyBindingManager().getInputDelay(),
						new ArrayList<>(keyBindings)
				).pack());
			}
		}
	}
}
