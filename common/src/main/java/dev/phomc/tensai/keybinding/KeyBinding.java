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
 * Represents a key binding which is under control by the Tensai client mod.<br>
 * A keybinding is listened by the client and its state updates will be broadcast back to the server.<br>
 * <br>
 * Note: Key-binding instances may have different name and flags. However, when it comes to play in the client-side,
 * the only property for distinction is {@link Key}. {@code Key} is used to check for duplication. If there are two
 * key-binding instances with different names but share the same key, then they are duplicated. Only one of them can be
 * registered successfully. To learn more about registration, have a look at {@link KeyBindingManager#registerKeyBindings(KeyBinding...)}
 */
public class KeyBinding {
	/**
	 * When {@code FLAG_CAPTURE_ENFORCEMENT} is enabled, if registration fails due to duplication
	 * ({@link RegisterStatus#KEY_DUPLICATED}), Tensai forcibly listens to the key without the need of registering
	 * it to the client. In this case, the key will not appear in the settings menu, and thus is not editable.<br>
	 * An enforced key-binding registration returns {@link RegisterStatus#CAPTURE_ENFORCED}.<br>
	 * The flag is disabled by default.
	 */
	public static final byte FLAG_CAPTURE_ENFORCEMENT = 1;

	/**
	 * For normal key-bindings which were registered successfully without duplication or the use of
	 * {@link #FLAG_CAPTURE_ENFORCEMENT}, the {@code FLAG_KEY_EDITABLE} flag permits binding to a different key.<br>
	 * For example: We have a key-binding {@link Key#KEY_Z} to "Launch a rocket". If this flag is enabled, players can
	 * bind to a different key such as {@link Key#KEY_X} to do the same operation.<br>
	 * This is the intended feature in Minecraft, thus the flag is <b>enabled</b> by default.
	 */
	public static final byte FLAG_KEY_EDITABLE = 2;

	/**
	 * When {@code FLAG_OPTIMIZED_STATE_UPDATE} is enabled, key state update is only sent to the server if and
	 * only if the {@code pressed} property changes. The property can be obtained using {@link KeyState#isPressed()}.<br>
	 * The flag helps to reduce the workload of exchanging information between client and server. However, the trade-off
	 * is that the {@code timesPressed} property (obtained using {@link KeyState#getTimesPressed()}) and its relevant
	 * method {@link KeyState#wasPressed()} will not work.<br>
	 * The flag is recommended for non-continuous key press. A click, for example, requires a key press (up) and a key
	 * release while the amount of press times does not matter.<br>
	 * The flag is disabled by default.
	 */
	public static final byte FLAG_OPTIMIZED_STATE_UPDATE = 4;

	/**
	 * The default flag.
	 */
	public static final byte DEFAULT_FLAG = FLAG_KEY_EDITABLE;

	private final Key key;
	private final String name;
	private final byte flags;

	/**
	 * Constructs a keybinding.
	 *
	 * @param key  A {@link Key}
	 * @param name Keybinding name
	 */
	public KeyBinding(@NotNull Key key, @NotNull String name) {
		this(key, name, DEFAULT_FLAG);
	}

	/**
	 * Constructs a keybinding with flags.
	 * <br>
	 * Example usage:
	 * <pre>
	 *     KeyBinding keyBind = new KeyBinding(
	 *         Key.KEY_X, "Launch rocket",
	 *         KeyBinding.FLAG_KEY_EDITABLE | KeyBinding.FLAG_OPTIMIZED_STATE_UPDATE
	 *     );
	 * </pre>
	 * @param key  A {@link Key}
	 * @param name Keybinding name
	 * @param flags Keybinding flags
	 */
	public KeyBinding(@NotNull Key key, @NotNull String name, byte flags) {
		this.key = key;
		this.name = name;
		this.flags = flags;
	}

	@NotNull
	public Key getKey() {
		return key;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public byte getFlags() {
		return flags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyBinding that = (KeyBinding) o;
		return key == that.key && name.equals(that.name) && flags == that.flags;
	}

	@Override
	public int hashCode() {
		// flags is metadata which should not be included
		return Objects.hash(key, name);
	}

	public enum RegisterStatus {
		/**
		 * The client denied key-recording.
		 */
		CLIENT_REJECTED,

		/**
		 * The keybinding was duplicated with another one registered by Minecraft client or other client-side mods<br>
		 * A side note: Please distinct this case with "already-registered" error.
		 * @see KeyBindingManager#registerKeyBindings(KeyBinding...)
		 */
		KEY_DUPLICATED,

		/**
		 * The keybinding was duplicated with another one registered by Minecraft client or other client-side mods<br>
		 * However, {@link KeyBinding#FLAG_CAPTURE_ENFORCEMENT} was set and the client approves key-recording.<br>
		 * A side note: Please distinct this case with "already-registered" error.
		 * @see KeyBindingManager#registerKeyBindings(KeyBinding...)
		 */
		CAPTURE_ENFORCED,

		/**
		 * The keybinding registration was successful.
		 * <ol>
		 *     <li>There was no key duplication</li>
		 *     <li>The client approves key-recording</li>
		 * </ol>
		 */
		SUCCESS
	}
}
