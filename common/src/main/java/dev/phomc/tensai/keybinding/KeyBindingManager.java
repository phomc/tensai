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

	// This array stores the states of all listening keys managed by Tensai
	// This includes: normal & enforced key-bindings
	protected final KeyState[] keyStates = new KeyState[Key.values().length];

	public KeyBindingManager(@NotNull ClientHandle clientHandle) {
		this.clientHandle = clientHandle;
	}

	/**
	 * Registers the given key-bindings.<br>
	 * This operation will be done asynchronously and the response will be returned using the event system.<br>
	 * <br>
	 * <b>Already-registered ones are ignored implicitly. Please distinct this case with duplication:</b>
	 * <ul>
	 *    <li>"Already-registered" means there was a successful registration made by whatever server-side mod (plugin)
	 *    using Tensai. It is possible to listen to its state updates. However, the key-binding behaviour and
	 *    how the state update will return is defined by the very-first mod (plugin).</li>
	 *    <li>{@link KeyBinding.RegisterStatus#KEY_DUPLICATED} means the key-binding is conflicted with another one
	 * 	  registered by the Minecraft client or other client-side mods. Key-capturing is unavailable.</li>
	 *    <li>{@link KeyBinding.RegisterStatus#CAPTURE_ENFORCED} is the same as {@link KeyBinding.RegisterStatus#KEY_DUPLICATED}
	 *    except that key-capturing is forced to be operable.</li>
	 *    <li>To summarize, "already-registered" is a server-side error, while the rest is client-side.</li>
	 * </ul>
	 *
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
	 * Sets the state for the given key.<br>
	 * <b>INTERNAL METHOD. DO NOT USE.</b>
	 * @param key key
	 * @param keyState key state
	 */
	public void setKeyState(@NotNull Key key, @NotNull KeyState keyState) {
		if (isKeyRegistered(key)) throw new RuntimeException("attempted to reinitialize key state");

		keyStates[key.ordinal()] = keyState;
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
