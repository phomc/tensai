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

package dev.phomc.tensai.bukkit.networking.message;

import dev.phomc.tensai.bukkit.TensaiBukkit;
import dev.phomc.tensai.server.client.ClientHandle;

import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.Subscriber;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.jetbrains.annotations.NotNull;

public abstract class ServerSubscriber extends Subscriber<ClientHandle> implements PluginMessageListener {
	public ServerSubscriber(Channel channel) {
		super(channel);
	}

	public abstract void onInitialize();

	@Override
	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
		if (!channel.equals(getChannel().getNamespace())) return;

		Callback<ClientHandle> callback = subscription.get(message[0]);
		if (callback != null) {
			callback.call(message, TensaiBukkit.getClient(player));
		}
	}
}
