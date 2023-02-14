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

/**
 * Represents a key state.<br>
 * The key state is used on <b>server-side only</b>. No key state update will be sent to client.<br>
 * Note: <b>Thread safety is not guaranteed</b> due to performance cost increase. Please ensure that reading and writing
 * with {@code KeyState} is done in the same thread as one involved in the event system.
 */
public class KeyState {
	public static byte DIRTY_TIME_PRESSED = 1;
	public static byte DIRTY_PRESSED = 2;

	private int timesPressed;
	private boolean pressed;
	private byte dirty;

	public KeyState(int timesPressed, boolean pressed, byte dirty) {
		this.timesPressed = Math.min(Math.max(timesPressed, 0), Short.MAX_VALUE);
		this.pressed = pressed;
		this.dirty = dirty;
	}

	/**
	 * Gets how many times the key press event was fired.<br>
	 * The value changes according to the following rules:
	 * <ul>
	 *     <li>It increases while the key is pressed down. The number of times is indeterminate.</li>
	 *     <li>It <b>only decreases</b> when {@link #wasPressed()} is called.</li>
	 *     <li>It remains unchanged until there is a change to client's screen (client-side behaviour) and will be reset to {@code 0}.</li>
	 * </ul>
	 * The range of {@code timesPressed} is {@code [0, Short.MAX_VALUE]}
	 *
	 * @return press times
	 */
	public int getTimesPressed() {
		return timesPressed;
	}

	/**
	 * Checks whether the key press event was fired and decreases the counter by {@code 1}.<br>
	 * This behaviour is preserved to be the same as in Fabric environment.<br>
	 * To avoid the decrement, uses {@link #getTimesPressed()}.<br>
	 * Another worthy notice is that this method is different from {@link #isPressed()}
	 * <ul>
	 *     <li>{@code wasPressed()} denotes whether a key press event was fired and how many times it was.</li>
	 *     <li>{@link #isPressed()} denotes whether the key is currently being pressed (down).</li>
	 *     <li>When {@link #isPressed()} returns {@code false}, the key is released. However, {@link #getTimesPressed()} may still not be reset, so {@code #wasPressed()} possibly returns {@code true}.</li>
	 * </ul>
	 * Example usage:
	 * <pre>{@code
	 * 	while (keyState.wasPressed()) {
	 * 		player.sendMessage("Key-press event called");
	 *  }
	 * }</pre>
	 *
	 * @return {@code true} or {@code false}
	 */
	public boolean wasPressed() {
		if (timesPressed == 0) {
			return false;
		}

		timesPressed--;
		return true;
	}

	/**
	 * Checks whether the key is pressed (down).
	 * @return {@code true} if it is, or {@code false} if the key is released (press up)
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * Gets the dirty value representing which state property was recently changed.
	 * <ul>
	 *     <li>If {@code (dirty & KeyState.DIRTY_TIME_PRESSED) > 0} then {@code timesPressed} changed.
	 *     {@link #getTimesPressed()} returns the new result.</li>
	 *     <li>If {@code (dirty & KeyState.DIRTY_PRESSED) > 0} then {@code pressed} changed.
	 *     {@link #isPressed()} returns the new result.</li>
	 * </ul>
	 * @return dirty value
	 */
	public byte getDirty() {
		return dirty;
	}

	/**
	 * Clean dirty.<br>
	 * <b>INTERNAL METHOD. DO NOT USE.</b>
	 */
	public void sweep() {
		dirty = 0;
	}

	/**
	 * Copies data from another key state.<br>
	 * <b>INTERNAL METHOD. DO NOT USE.</b>
	 * @param another key state
	 */
	public void copyFrom(KeyState another) {
		this.pressed = another.pressed;
		this.timesPressed = another.timesPressed;
		this.dirty = another.dirty;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyState keyState = (KeyState) o;
		return timesPressed == keyState.timesPressed && pressed == keyState.pressed && dirty == keyState.dirty;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timesPressed, pressed, dirty);
	}
}
