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

	public KeyState(int timesPressed) {
		this.timesPressed = timesPressed;
	}

	public int getTimesPressed() {
		return timesPressed;
	}

	public void setTimesPressed(int timesPressed) {
		this.timesPressed = timesPressed;
	}

	/**
	 * Checks whether the key was pressed, and <b>decreases</b> the press counter by {@code 1}.<br>
	 * This behaviour is preserved to be the same as in Fabric environment.<br>
	 * To avoid the decrement, uses {@link #getTimesPressed()}.
	 * <br>
	 * For example:
	 * <pre>{@code
	 * 	while (keyState.wasPressed()) {
	 * 		player.sendMessage("Key pressed");
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
	 * Clears the state.<br>
	 * <b>Note:</b> No key state update will be sent to the client.
	 */
	public void flush() {
		timesPressed = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyState keyState = (KeyState) o;
		return timesPressed == keyState.timesPressed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timesPressed);
	}
}
