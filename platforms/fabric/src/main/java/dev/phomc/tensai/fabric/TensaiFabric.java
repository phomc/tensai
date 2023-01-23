/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 PhoMC
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

package dev.phomc.tensai.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.phomc.tensai.fabric.clients.FabricClientHandle;
import dev.phomc.tensai.fabric.event.KeyPressCallback;
import dev.phomc.tensai.server.TensaiServer;
import dev.phomc.tensai.server.keybinding.KeyBindingPluginMessage;

public class TensaiFabric implements ModInitializer {
	public static final String MOD_ID = "tensai";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			((FabricClientHandle) oldPlayer).transferTo(newPlayer);
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			((FabricClientHandle) handler.player).sendPluginMessage(new KeyBindingPluginMessage((TensaiServer) server));
		});

		ServerPlayNetworking.registerGlobalReceiver(new Identifier(KeyBindingPluginMessage.CHANNEL), (server, player, handler, buf, responseSender) -> {
			KeyPressCallback.EVENT.invoker().handle(player, ((TensaiServer) server).getKeyBindingManager().getKeyBindings().get(buf.readString()));
		});
	}
}
