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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

import dev.phomc.tensai.fabric.TensaiFabric;
import dev.phomc.tensai.fabric.client.TensaiFabricClient;
import dev.phomc.tensai.fabric.client.iam.Permission;
import dev.phomc.tensai.fabric.client.mixins.ClientPlayNetworkAddonMixin;
import dev.phomc.tensai.fabric.client.networking.ClientSubscriber;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.MessageType;
import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;
import dev.phomc.tensai.server.TensaiServer;

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

			((TensaiServer) MinecraftClient.getInstance()).getTaskScheduler().runSync(() -> prompt(msg, sender));
		});
	}

	private void prompt(KeyBindingRegisterMessage msg, PacketSender sender) {
		String server = Objects.requireNonNull(((ClientPlayNetworkAddonMixin) sender).getHandler().getServerInfo()).address;
		Map<Permission, KeyBinding> mapping = new HashMap<>();
		Set<Permission> set = msg.getKeymap().stream()
				.map(keyBinding -> {
					Permission perm = new Permission(
							KeyBindingManager.KEYBINDING_NAMESPACE, keyBinding.getKey().name(),
							Text.translatable("gui.permissionTable.keybinding", keyBinding.getKey().name(), keyBinding.getName()),
							Permission.Context.SERVER,
							true
					);
					mapping.put(perm, keyBinding);
					return perm;
				}).collect(Collectors.toSet());

		TensaiFabricClient.getInstance().getClientAuthorizer().tryGrantMulti(set, server, table -> {
			Map<Key, KeyBinding.RegisterStatus> status = new HashMap<>();
			List<KeyBinding> keylist = new ArrayList<>();

			for (Map.Entry<Permission, Boolean> entry : table.entrySet()) {
				KeyBinding kb = mapping.get(entry.getKey());

				if (!entry.getValue()) {
					status.put(kb.getKey(), KeyBinding.RegisterStatus.CLIENT_REJECTED);
					continue;
				}

				if (!KeyBindingManager.getInstance().testAvailability(kb.getKey())) {
					status.put(kb.getKey(), KeyBinding.RegisterStatus.KEY_DUPLICATED);
					continue;
				}

				status.put(kb.getKey(), KeyBinding.RegisterStatus.SUCCESS);
				keylist.add(kb);
			}

			if (!keylist.isEmpty()) {
				KeyBindingManager.getInstance().initialize(keylist, msg.getInputDelay());
				TensaiFabric.LOGGER.info("Keybinding registered ({} keys)", msg.getKeymap().size());
			}

			publish(new KeyBindingRegisterResponse(status), sender);
		});
	}
}
