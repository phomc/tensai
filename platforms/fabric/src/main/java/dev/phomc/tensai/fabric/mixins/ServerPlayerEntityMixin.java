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

package dev.phomc.tensai.fabric.mixins;

import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.phomc.tensai.fabric.client.FabricClientHandle;
import dev.phomc.tensai.fabric.vfx.ClientVisualEffectsImpl;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.server.vfx.VisualEffects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements FabricClientHandle {
	@Unique
	private ClientVisualEffectsImpl vfx;

	@Override
	public void sendPluginMessage(Channel channel, byte[] bytes) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.wrappedBuffer(bytes));
		ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new Identifier(channel.getNamespace()), buf);
	}

	@Override
	public void transferTo(ServerPlayerEntity newPlayer) {
		FabricClientHandle newClientHandle = (FabricClientHandle) newPlayer;
		newClientHandle.setVfx(this.vfx);
		this.vfx = null;
	}

	@Override
	public VisualEffects getVfx() {
		if (vfx == null) vfx = new ClientVisualEffectsImpl((ServerPlayerEntity) (Object) this);
		return vfx;
	}

	@Override
	public void setVfx(ClientVisualEffectsImpl vfx) {
		this.vfx = vfx;
	}
}
