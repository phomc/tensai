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

import java.io.DataOutput;
import java.io.IOException;

import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.server.TensaiServer;
import dev.phomc.tensai.server.networking.PluginMessage;

public class KeyBindingPluginMessage extends PluginMessage {
	public static final String CHANNEL = "tensai:keybinding";

	private final TensaiServer tensai;

	public KeyBindingPluginMessage(TensaiServer tensai) {
		super(CHANNEL, "keybinding");
		this.tensai = tensai;
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		stream.writeInt(tensai.getKeyBindingManager().getKeyBindings().size());

		for (KeyBinding keyBinding : tensai.getKeyBindingManager().getKeyBindings().values()) {
			stream.writeUTF(keyBinding.getName());
			stream.writeInt(keyBinding.getKey().getCode());
		}
	}
}
