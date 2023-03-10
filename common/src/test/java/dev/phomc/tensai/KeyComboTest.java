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

package dev.phomc.tensai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.combo.CommitResult;
import dev.phomc.tensai.keybinding.combo.KeyCombo;
import dev.phomc.tensai.keybinding.combo.KeyComboMatcher;
import dev.phomc.tensai.keybinding.combo.KeyComboState;

public class KeyComboTest {
	@Test
	public void matchTest() throws InterruptedException {
		KeyComboMatcher matcher = new KeyComboMatcher(0.2f);
		matcher.offerCombo(
				new KeyCombo.Builder(Key.KEY_1)
						.then(Key.KEY_3, 5)
						.then(Key.KEY_4, 5)
						.then(Key.KEY_5, 0)
						.build()
		);
		KeyComboState state = new KeyComboState();
		Assertions.assertEquals(CommitResult.COMMITTED, matcher.commitKey(state, Key.KEY_1));
		Assertions.assertEquals(CommitResult.NOT_FOUND, matcher.commitKey(state, Key.KEY_2));
		Thread.sleep(5 * 50);
		Assertions.assertEquals(CommitResult.COMMITTED, matcher.commitKey(state, Key.KEY_3));
		Thread.sleep(6 * 50);
		Assertions.assertEquals(CommitResult.COMMITTED, matcher.commitKey(state, Key.KEY_4));
		Thread.sleep(50);
		Assertions.assertEquals(CommitResult.TIMEOUT, matcher.commitKey(state, Key.KEY_5));
	}

	@Test
	public void parseTest() {
		Assertions.assertEquals(new KeyCombo.Builder(Key.KEY_V).build(), KeyCombo.parse("").then(Key.KEY_V, 7).build());

		KeyCombo keyCombo = new KeyCombo.Builder(Key.KEY_1)
				.then(Key.KEY_3, 5)
				.then(Key.KEY_4, 5)
				.then(Key.KEY_5, 5)
				.build();
		Assertions.assertEquals("KEY_1 5>KEY_3 5>KEY_4 5>KEY_5", keyCombo.toString());
		Assertions.assertEquals(keyCombo, KeyCombo.parse("KEY_1 5>KEY_3 5>KEY_4 5>KEY_5").build());
		Assertions.assertEquals(keyCombo, KeyCombo.parse("KEY_1  5>KEY_3  5>KEY_4  5>KEY_5").build());
		Assertions.assertEquals(keyCombo, KeyCombo.parse("		KEY_1		5>KEY_3		    5>KEY_4			5>KEY_5   ").build());
		Assertions.assertEquals(keyCombo, KeyCombo.parse(keyCombo.toString()).build());

		keyCombo = new KeyCombo.Builder(Key.MOUSE_BUTTON_1).build();
		Assertions.assertEquals("MOUSE_BUTTON_1", keyCombo.toString());
		Assertions.assertEquals(keyCombo, KeyCombo.parse(keyCombo.toString()).build());

		keyCombo = new KeyCombo.Builder(Key.KEY_A).named("Test Key-combo").build();
		Assertions.assertEquals("KEY_A", keyCombo.toString());
		Assertions.assertEquals(keyCombo, KeyCombo.parse(keyCombo.toString()).named("Test Key-combo").build());
	}
}
