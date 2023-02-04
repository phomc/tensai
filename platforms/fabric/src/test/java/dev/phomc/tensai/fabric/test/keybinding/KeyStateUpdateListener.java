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

package dev.phomc.tensai.fabric.test.keybinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import dev.phomc.tensai.fabric.event.ServerKeybindingEvents;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBindingManager;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.keybinding.combo.KeyCombo;
import dev.phomc.tensai.keybinding.combo.KeyComboMatcher;
import dev.phomc.tensai.keybinding.combo.KeyComboState;
import dev.phomc.tensai.server.client.ClientHandle;

public class KeyStateUpdateListener implements ServerKeybindingEvents.KeyStateUpdateEvent {
	private final KeyComboMatcher matcher = new KeyComboMatcher(0.5f);
	private final Map<ServerPlayerEntity, KeyComboState> keyComboStates = new WeakHashMap<>();

	public KeyStateUpdateListener() {
		matcher.offerCombo(new KeyCombo.Builder(Key.KEY_V).then(Key.KEY_B, 10).build());
	}

	@Override
	public void updateKeyState(ServerPlayerEntity player, Map<Key, KeyState> keyStates) {
		KeyBindingManager kbm = ((ClientHandle) player).getKeyBindingManager();
		List<String> list = new ArrayList<>();

		for (Key key : kbm.getRegisteredKeys()) {
			KeyState state = kbm.getKeyState(key);
			if (state == null) continue;
			String n = key.name()
					.replace("KEY_", "")
					.replace("MOUSE_BUTTON_", "M-");
			list.add("[" + (state.isPressed() ? n : n.toLowerCase()) + "] " + state.getTimesPressed());
		}

		player.sendMessageToClient(Text.of(String.join(" | ", list)), true);

		for (Map.Entry<Key, KeyState> ent : keyStates.entrySet()) {
			KeyComboState state = keyComboStates.computeIfAbsent(player, k -> new KeyComboState());

			switch (matcher.commitKey(state, ent.getKey())) {
				case COMMITTED: {
					player.sendMessageToClient(Text.of("Committed: " + ent.getKey().name()), false);
					KeyCombo kb = matcher.lookupCombo(state);

					if (kb != null) {
						player.sendMessageToClient(Text.of("Completed combo: " + kb.getName()), false);
						keyComboStates.remove(player);
					}

					break;
				}
				case TIMEOUT: {
					keyComboStates.remove(player);
					break;
				}
			}
		}
	}
}
