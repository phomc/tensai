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

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyState;

import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

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

	@FunctionalInterface
	public interface KeyRegisterResultEvent {
		void respond(ServerPlayerEntity player, KeyBindingRegisterResponse response);
	}

	@FunctionalInterface
	public interface KeyStateUpdateEvent {
		void updateKeyState(ServerPlayerEntity player, Map<Key, KeyState> keyStates);
	}
}
