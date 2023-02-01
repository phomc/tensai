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

package dev.phomc.tensai.networking.message.s2c;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.message.Message;
import dev.phomc.tensai.networking.message.MessageType;

public class KeyBindingRegisterMessage extends Message {
	private final List<KeyBinding> keymap;

	public KeyBindingRegisterMessage() {
		this(new ArrayList<>());
	}

	public KeyBindingRegisterMessage(List<KeyBinding> keymap) {
		super(MessageType.KEYBINDING_REGISTER);
		this.keymap = keymap;
	}

	public List<KeyBinding> getKeymap() {
		return keymap;
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		stream.writeInt(keymap.size());

		for (KeyBinding entry : keymap) {
			stream.writeInt(entry.getKey().getCode());
			stream.writeUTF(entry.getName());
		}
	}

	@Override
	public void read(DataInput stream) throws IOException {
		int size = stream.readInt();

		for (int i = 0; i < size; i++) {
			Key key = Key.lookup(stream.readInt());
			String str = stream.readUTF();

			if (key != null) {
				keymap.add(new KeyBinding(key, str));
			}
		}
	}
}
