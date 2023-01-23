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

package dev.phomc.tensai.networking.message.c2s;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.Message;
import dev.phomc.tensai.networking.message.MessageType;

public class KeyBindingRegisterResponse extends Message {
	private byte result;

	public KeyBindingRegisterResponse() {
		this(KeyBinding.RegisterStatus.UNKNOWN);
	}

	public KeyBindingRegisterResponse(byte result) {
		super(Channel.KEYBINDING, MessageType.KEYBINDING_REGISTER_RESPONSE);
		this.result = result;
	}

	public boolean isClientRejected() {
		return result == KeyBinding.RegisterStatus.CLIENT_REJECTED;
	}

	public boolean isKeyDuplicated() {
		return result == KeyBinding.RegisterStatus.KEY_DUPLICATED;
	}

	public boolean isSuccess() {
		return result == KeyBinding.RegisterStatus.SUCCESS;
	}

	public byte getResult() {
		return result;
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		stream.writeByte(result);
	}

	@Override
	public void read(DataInput stream) throws IOException {
		result = stream.readByte();
	}
}
