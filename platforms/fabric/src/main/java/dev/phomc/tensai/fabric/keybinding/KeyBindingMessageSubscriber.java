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

package dev.phomc.tensai.fabric.keybinding;

import java.util.Collections;
import java.util.Map;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import dev.phomc.tensai.fabric.client.FabricClientHandle;
import dev.phomc.tensai.fabric.event.ServerKeybindingEvents;
import dev.phomc.tensai.fabric.mixins.ServerPlayNetworkAddonMixin;
import dev.phomc.tensai.fabric.networking.ServerSubscriber;
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
			ServerKeybindingEvents.REGISTER_RESULT.invoker().respond(((ServerPlayNetworkAddonMixin) sender).getHandler().player, Collections.unmodifiableMap(msg.getStatus()));
		});

		subscribe(MessageType.KEYBINDING_STATE_UPDATE, (data, sender) -> {
			ServerPlayNetworkHandler handler = ((ServerPlayNetworkAddonMixin) sender).getHandler();
			FabricClientHandle clientHandle = (FabricClientHandle) handler.player;
			KeyBindingStateUpdate msg = new KeyBindingStateUpdate();
			msg.unpack(data);

			// keep original key state objects
			for (Map.Entry<Key, KeyState> ent : msg.getStates().entrySet()) {
				KeyState ref = clientHandle.getKeyBindingManager().getKeyState(ent.getKey());
				if (ref == null) throw new RuntimeException("unexpected panic");
				ref.copyFrom(ent.getValue());
				ent.setValue(ref);
			}

			ServerKeybindingEvents.STATE_UPDATE.invoker().updateKeyState(handler.player, msg.getStates());
		});
	}
}
