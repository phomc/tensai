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

package dev.phomc.tensai.fabric.client.keybinding;

import java.util.Map;

import dev.phomc.tensai.fabric.client.networking.ClientSubscriber;

import dev.phomc.tensai.fabric.client.security.PermissionManager;

import dev.phomc.tensai.networking.Channel;

import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.message.MessageType;
import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;

public class KeyBindingMessageSubscriber extends ClientSubscriber {
	public KeyBindingMessageSubscriber(Channel channel) {
		super(channel);
	}

	@Override
	public void onInitialize() {
		subscribe(MessageType.KEYBINDING_REGISTER, (data, sender) -> {
			KeyBindingRegisterMessage msg = new KeyBindingRegisterMessage();
			msg.unpack(data);

			PermissionManager.getInstance().tryGrant(KeyBindingManager.KEY_RECORD_PERMISSION, ok -> {
				byte result = ok ? KeyBinding.RegisterStatus.UNKNOWN : KeyBinding.RegisterStatus.CLIENT_REJECTED;

				if(ok) {
					if(KeyBindingManager.getInstance().testBulkAvailability(msg.getKeymap())) {
						KeyBindingManager.getInstance().registerBulk(msg.getKeymap());
						KeyBindingManager.getInstance().setInputDelay(msg.getInputDelay());
						result = KeyBinding.RegisterStatus.SUCCESS;
					} else {
						result = KeyBinding.RegisterStatus.KEY_DUPLICATED;
					}
				}

				publish(new KeyBindingRegisterResponse(result), sender);
			});
		});
	}
}
