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

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a key binding.<br>
 * A keybinding is listened by the client and its state updates will be broadcast back to the server.
 */
public class KeyBinding {
	private final Key key;
	private final String name;
	private final boolean enforced;

	/**
	 * Constructs a keybinding.
	 *
	 * @param key  A {@link Key}
	 * @param name Keybinding name
	 */
	public KeyBinding(@NotNull Key key, @NotNull String name) {
		this(key, name, false);
	}

	/**
	 * Constructs a keybinding.
	 *
	 * @param key  A {@link Key}
	 * @param name Keybinding name
	 * @param enforced Whether the keybinding is enforced to be accepted
	 */
	public KeyBinding(@NotNull Key key, @NotNull String name, boolean enforced) {
		this.key = key;
		this.name = name;
		this.enforced = enforced;
	}

	@NotNull
	public Key getKey() {
		return key;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public boolean isEnforced() {
		return enforced;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyBinding that = (KeyBinding) o;
		return key == that.key && name.equals(that.name) && enforced == that.enforced;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, name);
	}

	public enum RegisterStatus {
		/**
		 * The client denied the keybinding registration.
		 */
		CLIENT_REJECTED,

		/**
		 * The keybinding was duplicated (collided) with another one.
		 */
		KEY_DUPLICATED,

		/**
		 * The keybinding registration was successful.
		 */
		SUCCESS;
	}
}
