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

package dev.phomc.tensai.fabric.event;

import java.util.Map;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.keybinding.KeyState;

public class ServerKeybindingEvents {
	public static final Event<ServerKeybindingEvents.KeyRegisterResultEvent> REGISTER_RESULT = EventFactory.createArrayBacked(ServerKeybindingEvents.KeyRegisterResultEvent.class, (listeners) -> (player, result) -> {
		for (KeyRegisterResultEvent event : listeners) {
			event.respond(player, result);
		}
	});

	public static final Event<ServerKeybindingEvents.KeyStateUpdateEvent> STATE_UPDATE = EventFactory.createArrayBacked(ServerKeybindingEvents.KeyStateUpdateEvent.class, (listeners) -> (player, states) -> {
		for (KeyStateUpdateEvent event : listeners) {
			event.updateKeyState(player, states);
		}
	});

	/**
	 * This event returns the keybinding registration result.
	 * <b>Note:</b> This event is called asynchronously.
	 */
	@FunctionalInterface
	public interface KeyRegisterResultEvent {
		/**
		 * Called when the status of registered keys is returned.
		 * @param player the relevant player
		 * @param status an <b>unmodifiable</b> status map
		 */
		void respond(ServerPlayerEntity player, Map<Key, KeyBinding.RegisterStatus> status);
	}

	/**
	 * This event is triggered whenever one or more key states is updated.<br>
	 * <b>Note:</b>
	 * <ul>
	 *     <li>The timing may be different from client-side due to connection latency.</li>
	 *     <li>This event is called asynchronously.</li>
	 *     <li>No key state update will be sent back to the client.</li>
	 *     <li>The server-sided key state is shared across mods.</li>
	 * </ul>
	 */
	@FunctionalInterface
	public interface KeyStateUpdateEvent {
		/**
		 * Called when the key states are updated.
		 * @param player the relevant player
		 * @param keyStates a modifiable, shared map
		 */
		void updateKeyState(ServerPlayerEntity player, Map<Key, KeyState> keyStates);
	}
}
