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

import java.util.Map;

import org.bukkit.Bukkit;

import dev.phomc.tensai.bukkit.TensaiBukkit;
import dev.phomc.tensai.bukkit.client.ClientHandleImpl;
import dev.phomc.tensai.bukkit.event.KeyRegisterResultEvent;
import dev.phomc.tensai.bukkit.event.KeyStateUpdateEvent;
import dev.phomc.tensai.bukkit.networking.ServerSubscriber;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.MessageType;
import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;
import dev.phomc.tensai.networking.message.c2s.KeyBindingStateUpdate;

public class KeyBindingMessageSubscriber extends ServerSubscriber {
	public KeyBindingMessageSubscriber(Channel channel) {
		super(channel);
	}

	@Override
	public void onInitialize() {
		subscribe(MessageType.KEYBINDING_REGISTER_RESPONSE, (data, sender) -> {
			KeyBindingRegisterResponse msg = new KeyBindingRegisterResponse();
			msg.unpack(data);
			Bukkit.getPluginManager().callEvent(new KeyRegisterResultEvent(((ClientHandleImpl) sender).getPlayer(), msg));
		});

		subscribe(MessageType.KEYBINDING_STATE_UPDATE, (data, sender) -> {
			Map<Key, KeyState> states = TensaiBukkit.getInstance().getKeyBindingManager().getKeyStates();
			// we want to pass key state references rather than cloning them
			// So key state is only updated if its corresponding key is registered
			KeyBindingStateUpdate msg = new KeyBindingStateUpdate(states);
			msg.unpack(data);
			Bukkit.getPluginManager().callEvent(new KeyStateUpdateEvent(((ClientHandleImpl) sender).getPlayer(), states));
		});
	}
}
