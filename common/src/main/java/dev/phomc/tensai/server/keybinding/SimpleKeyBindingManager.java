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

package dev.phomc.tensai.server.keybinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;

import dev.phomc.tensai.keybinding.KeyState;

import org.jetbrains.annotations.NotNull;

public class SimpleKeyBindingManager implements KeyBindingManager {
	private final Map<Key, KeyBinding> keyBindings = new HashMap<>();
	private final Map<Key, KeyState> keyStates = new HashMap<>();
	private int inputDelay;

	@Override
	public void registerKeyBinding(@NotNull KeyBinding keyBinding) {
		if (keyBindings.containsKey(keyBinding.getKey())) {
			throw new IllegalArgumentException("KeyBinding with key " + keyBinding.getKey() + " already exists!");
		}

		keyBindings.put(keyBinding.getKey(), keyBinding);
		keyStates.put(keyBinding.getKey(), new KeyState(0));
	}

	@Override
	public @NotNull Map<Key, KeyBinding> getKeyBindings() {
		return Collections.unmodifiableMap(keyBindings);
	}

	@Override
	public boolean hasKeyBinding(Key key) {
		return keyBindings.containsKey(key);
	}

	@Override
	public KeyBinding getKeyBinding(Key key) {
		return keyBindings.get(key);
	}

	@Override
	public @NotNull Map<Key, KeyState> getKeyStates() {
		return Collections.unmodifiableMap(keyStates);
	}

	@Override
	public KeyState getKeyState(Key key) {
		return keyStates.get(key);
	}

	@Override
	public int getInputDelay() {
		return inputDelay;
	}

	@Override
	public void setInputDelay(int inputDelay) {
		this.inputDelay = inputDelay;
	}
}
