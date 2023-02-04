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

package dev.phomc.tensai.keybinding;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;
import dev.phomc.tensai.server.client.ClientHandle;

/**
 * Represents the server-side keybinding manager of a client.
 */
public class KeyBindingManager {
	protected final ClientHandle clientHandle;
	protected final KeyState[] keyStates = new KeyState[Key.values().length];

	public KeyBindingManager(@NotNull ClientHandle clientHandle) {
		this.clientHandle = clientHandle;
	}

	/**
	 * Registers the given key-bindings.<br>
	 * Already-registered ones are ignored implicitly. This operation will be done asynchronously and the response will
	 * be returned using the event system.
	 * @param keyBindings an array of key-bindings
	 */
	public void registerKeyBindings(@NotNull KeyBinding... keyBindings) {
		if (keyBindings.length == 0) return;

		clientHandle.sendPluginMessage(Channel.KEYBINDING, new KeyBindingRegisterMessage(
				Arrays.stream(keyBindings).filter(k -> !isKeyRegistered(k.getKey())).collect(Collectors.toUnmodifiableList())
		).pack());
	}

	/**
	 * Gets all registered keys.
	 * @return a newly-created, modifiable set of keys
	 */
	public @NotNull Set<Key> getRegisteredKeys() {
		Set<Key> keys = EnumSet.noneOf(Key.class);

		for (int i = 0; i < keyStates.length; i++) {
			if (keyStates[i] != null) {
				keys.add(Key.values()[i]);
			}
		}

		return keys;
	}

	/**
	 * Gets the key state of the given key.
	 * @param key the key
	 * @return {@link KeyState}, or {@code null} if not found
	 */
	@Nullable
	public KeyState getKeyState(@NotNull Key key) {
		return keyStates[key.ordinal()];
	}

	/**
	 * Checks whether the given key is registered.
	 * @param key the key to be checked
	 * @return {@code true} if registered, otherwise {@code false}
	 */
	public boolean isKeyRegistered(@NotNull Key key) {
		return getKeyState(key) != null;
	}
}
