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

package dev.phomc.tensai.fabric.client.networking.message;

import dev.phomc.tensai.networking.message.Message;

import io.netty.buffer.ByteBufUtil;

import io.netty.buffer.Unpooled;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.networking.message.Subscriber;

@Environment(EnvType.CLIENT)
public abstract class ClientSubscriber extends Subscriber<PacketSender> {
	private final Identifier identifier;

	public ClientSubscriber(Channel channel) {
		super(channel);
		identifier = new Identifier(channel.getNamespace());

		ClientPlayNetworking.registerGlobalReceiver(identifier, (client, handler, buf, responseSender) -> {
			byte[] bytes = ByteBufUtil.getBytes(buf);
			Callback<PacketSender> callback = subscription.get(bytes[0]);
			if (callback != null) {
				callback.call(bytes, responseSender);
			}
		});
	}

	public abstract void onInitialize();

	public Identifier getIdentifier() {
		return identifier;
	}

	public void publish(Message message, PacketSender consumer) {
		consumer.sendPacket(identifier, new PacketByteBuf(Unpooled.wrappedBuffer(message.pack())));
	}

	public void publish(Message message) {
		publish(message, ClientPlayNetworking.getSender());
	}
}
