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

package dev.phomc.tensai.keybinding.combo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import dev.phomc.tensai.keybinding.Key;

/**
 * Represents a key combination which consists of multiple {@link KeyUnit}.<br>
 * Key combination is a server-side feature, the underlying foundation is {@link dev.phomc.tensai.keybinding.KeyBinding}.<br>
 */
public class KeyCombo {
	private final KeyUnit[] keyUnits;

	private final String name;

	/**
	 * Parses the given stringified key-combo.
	 * @param str key-combo string
	 * @return {@link KeyCombo.Builder}
	 */
	@NotNull
	public static KeyCombo.Builder parse(@NotNull String str) {
		String[] units = str.split("\\s+");
		KeyCombo.Builder builder = new KeyCombo.Builder();

		for (String unit : units) {
			if (unit.isEmpty()) continue;

			if (unit.charAt(0) == '>') {
				builder.then(Key.valueOf(unit.substring(1).toUpperCase()), 0);
				continue;
			}

			String[] args = unit.split(">");

			if (args.length == 1) {
				builder.then(Key.valueOf(args[0].toUpperCase()), 0);
			} else if (args.length == 2) {
				builder.then(Key.valueOf(args[1].toUpperCase()), Integer.parseInt(args[0]));
			}
		}

		return builder;
	}

	public KeyCombo(@NotNull KeyUnit[] keyUnits, @NotNull String name) {
		this.keyUnits = keyUnits;
		this.name = name;
		validate(keyUnits);
	}

	private void validate(KeyUnit[] keyUnits) {
		if (keyUnits.length == 0) {
			throw new IllegalArgumentException("Key units must not be empty");
		}

		if (keyUnits.length > 20) {
			throw new IllegalArgumentException("Key-combo is too long. People only have 10 fingers and 10 toes");
		}

		if (keyUnits[0].getAbsoluteDelayTime() != 0) {
			throw new IllegalArgumentException("Key unit [0] must have zero absolute delay time");
		}

		Set<Key> set = EnumSet.noneOf(Key.class);

		for (int i = 1; i < keyUnits.length; i++) {
			if (!set.add(keyUnits[i].getKey())) {
				throw new IllegalArgumentException(String.format("Cyclic key-combo detected at [%d]", i));
			}

			if (keyUnits[i].getAbsoluteDelayTime() < keyUnits[i - 1].getAbsoluteDelayTime()) {
				throw new IllegalArgumentException(String.format("Key unit [%d] has smaller absolute delay time than the previous one", i));
			}
		}
	}

	@NotNull
	public KeyUnit[] getKeyUnits() {
		return keyUnits;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyCombo keyCombo = (KeyCombo) o;
		return Arrays.equals(keyUnits, keyCombo.keyUnits) && name.equals(keyCombo.name);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name);
		result = 31 * result + Arrays.hashCode(keyUnits);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(keyUnits[0].getKey());

		for (int i = 1; i < keyUnits.length; i++) {
			sb.append(' ');
			sb.append(keyUnits[i].getRelativeDelayTime());
			sb.append('>');
			sb.append(keyUnits[i].getKey());
		}

		return sb.toString();
	}

	/**
	 * A convenient way to build a key combination.
	 */
	public static class Builder {
		private final List<KeyUnit> keyUnits;
		private String name = "Unnamed Combo";
		private int absoluteDelayTime;

		Builder() {
			keyUnits = new ArrayList<>();
		}

		public Builder(@NotNull Key key) {
			this();
			then(key, 0);
		}

		/**
		 * Appends a key unit.
		 * @param keyUnit key unit
		 * @return this builder
		 */
		public Builder then(@NotNull KeyUnit keyUnit) {
			keyUnits.add(keyUnit);
			return this;
		}

		/**
		 * Appends a key unit.
		 * @param key key
		 * @param relativeDelayTime relative delay time
		 * @return this builder
		 */
		public Builder then(@NotNull Key key, int relativeDelayTime) {
			if (keyUnits.isEmpty()) relativeDelayTime = 0;
			absoluteDelayTime += relativeDelayTime;
			then(new KeyUnit(key, relativeDelayTime, absoluteDelayTime));
			return this;
		}

		/**
		 * Sets the key-combo name.
		 * @param name combo name
		 * @return this builder
		 */
		public Builder named(@NotNull String name) {
			this.name = name;
			return this;
		}

		/**
		 * Builds a new key combination.
		 * @return {@link KeyCombo}
		 */
		@NotNull
		public KeyCombo build() {
			return new KeyCombo(keyUnits.toArray(KeyUnit[]::new), name);
		}
	}
}
