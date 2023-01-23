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

package dev.phomc.tensai.networking.message;

import dev.phomc.tensai.networking.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * A subscriber is one who listens to incoming messages.
 * @param <T> Represents the sender. It is different per platforms.
 */
public abstract class Subscriber<T> {
	protected final Channel channel;
	protected final Map<Byte, Callback<T>> callbackMap = new HashMap<>();

	public Subscriber(Channel channel) {
		this.channel = channel;
	}

	/**
	 * This method is called after this subscriber was initialized successfully.
	 */
	public abstract void onInitialize();

	/**
	 * Captures a specific type of message.
	 * @param id message type
	 * @param callback callback
	 */
	public void capture(byte id, Callback<T> callback) {
		callbackMap.put(id, callback);
	}

	public interface Callback<T> {
		void call(byte[] data, T sender);
	}
}
