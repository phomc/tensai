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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.phomc.tensai.keybinding.Key;

/**
 * A key-combo matcher is a utility class to store and match key-combo based on given inputs.
 */
public class KeyComboMatcher {
	private final Map<Long, Integer> chainHashes = new HashMap<>();
	private final Map<Long, KeyCombo> comboMapping = new HashMap<>();
	private final float delaySensitivity;

	/**
	 * Constructs a new key-combo.
	 * @param delaySensitivity the delay sensitivity
	 */
	public KeyComboMatcher(float delaySensitivity) {
		this.delaySensitivity = Math.max(1.0f, delaySensitivity);
	}

	private long hash(long previous, long key) {
		// https://math.stackexchange.com/a/882961
		long max = Math.max(previous, key);
		long min = Math.min(previous, key);
		return ((max * (max + 1)) >> 1) + min;
	}

	/**
	 * Offers a key combination.
	 * @param keyCombo {@link KeyCombo}
	 */
	public void offerCombo(@NotNull KeyCombo keyCombo) {
		long previous = 0;

		for (int i = 0; i < keyCombo.getKeyUnits().length; i++) {
			KeyUnit keyUnit = keyCombo.getKeyUnits()[i];
			long hash = hash(previous, keyUnit.getKey().getCode());
			chainHashes.put(hash, keyUnit.getRelativeDelayTime());
			previous = hash;
		}

		comboMapping.put(previous, keyCombo);
	}

	/**
	 * Looks up a key-combo given a {@link KeyComboState}.
	 * @param keyComboState key-combo state
	 * @return key-combo, or {@code null} if not found
	 */
	@Nullable
	public KeyCombo lookupCombo(@NotNull KeyComboState keyComboState) {
		return comboMapping.get(keyComboState.getLastHash());
	}

	/**
	 * Commits a key and updates the given {@link KeyComboState}.
	 * @param state the state to be referred and updated
	 * @param key the latest key from the input
	 * @return the commit result
	 */
	@NotNull
	public CommitResult commitKey(@NotNull KeyComboState state, @NotNull Key key) {
		long hash = hash(state.getLastHash(), key.getCode());
		Integer delay = chainHashes.get(hash);
		if (delay == null) return CommitResult.NOT_FOUND;

		long time = System.currentTimeMillis();
		long delta = state.getLastKeyPress() == 0 ? 0 : (time - state.getLastKeyPress());
		float ratio = delta / (delay * 50f);
		if (ratio < 1.0f || ratio > delaySensitivity) return CommitResult.TIMEOUT;

		state.setLastHash(hash);
		state.setLastKeyPress(time);
		return CommitResult.COMMITTED;
	}
}
