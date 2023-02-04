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
 * <b>The key state is used on server-side only. No key state update will be sent to client.</b>
 */
public class KeyState {
	private int timesPressed;
	private boolean pressed;

	public KeyState(int timesPressed, boolean pressed) {
		this.timesPressed = Math.min(Math.max(timesPressed, 0), Short.MAX_VALUE);
		this.pressed = pressed;
	}

	/**
	 * Gets how many times the key press event was fired.<br>
	 * The value changes according to the following rules:
	 * <ul>
	 *     <li>It increases while the key is pressed down. The number of times is indeterminate.</li>
	 *     <li>It <b>only decreases</b> when {@link #wasPressed()} is called. {@code 0} is the lower bound.</li>
	 *     <li>It remains unchanged until there is a change to client's screen (client-side behaviour), and it will reset to {@code 0}.</li>
	 * </ul>
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
	 * For example:
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
	 * Copies data from another key state.<br>
	 * <b>INTERNAL METHOD. DO NOT USE.</b>
	 * @param another key state
	 */
	public void copyFrom(KeyState another) {
		this.pressed = another.pressed;
		this.timesPressed = another.timesPressed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyState keyState = (KeyState) o;
		return timesPressed == keyState.timesPressed && pressed == keyState.pressed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timesPressed, pressed);
	}
}
