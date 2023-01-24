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

import java.util.Objects;

import net.minecraft.client.MinecraftClient;

import dev.phomc.tensai.fabric.TensaiFabric;
import dev.phomc.tensai.fabric.client.TensaiFabricClient;
import dev.phomc.tensai.fabric.client.mixins.ClientPlayNetworkAddonMixin;
import dev.phomc.tensai.fabric.client.networking.ClientSubscriber;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.MessageType;
import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;
import dev.phomc.tensai.server.Tensai;

public class KeyBindingMessageSubscriber extends ClientSubscriber {
	public KeyBindingMessageSubscriber(Channel channel) {
		super(channel);
	}

	@Override
	public void onInitialize() {
		subscribe(MessageType.KEYBINDING_REGISTER, (data, sender) -> {
			TensaiFabric.LOGGER.info("Registering keybinding...");
			KeyBindingRegisterMessage msg = new KeyBindingRegisterMessage();
			msg.unpack(data);

			// TODO Show registering-keys explicitly
			String server = Objects.requireNonNull(((ClientPlayNetworkAddonMixin) sender).getHandler().getServerInfo()).address;

			((Tensai) MinecraftClient.getInstance()).getTaskScheduler().runSync(() ->
					TensaiFabricClient.getInstance().getPermissionManager().tryGrant(KeyBindingManager.KEY_RECORD_PERMISSION, server, ok -> {
						byte result = ok ? KeyBinding.RegisterStatus.UNKNOWN : KeyBinding.RegisterStatus.CLIENT_REJECTED;

						if (ok) {
							TensaiFabric.LOGGER.info("Keybinding registration allowed");

							if (KeyBindingManager.getInstance().testBulkAvailability(msg.getKeymap())) {
								KeyBindingManager.getInstance().registerBulk(msg.getKeymap());
								KeyBindingManager.getInstance().setInputDelay(msg.getInputDelay());
								result = KeyBinding.RegisterStatus.SUCCESS;
								TensaiFabric.LOGGER.info("Keybinding registered ({} keys)", msg.getKeymap().size());
							} else {
								result = KeyBinding.RegisterStatus.KEY_DUPLICATED;
								TensaiFabric.LOGGER.info("Keybinding collision detected");
							}
						} else {
							TensaiFabric.LOGGER.info("Keybinding registration declined");
						}

						publish(new KeyBindingRegisterResponse(result), sender);
					})
			);
		});
	}
}
