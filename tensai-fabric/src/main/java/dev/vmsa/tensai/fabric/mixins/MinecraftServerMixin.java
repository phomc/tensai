/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 VMSA
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

package dev.vmsa.tensai.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import dev.vmsa.tensai.fabric.TensaiFabricEvents;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "runServer",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"))
	private void events$startingServer(CallbackInfo ci) {
		TensaiFabricEvents.SERVER_STARTING.emit((MinecraftServer) (Object) this);
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void events$stoppingServer(CallbackInfo ci) {
		TensaiFabricEvents.SERVER_STOPPING.emit((MinecraftServer) (Object) this);
	}

	@Inject(method = "shutdown", at = @At("TAIL"))
	private void events$stoppedServer(CallbackInfo ci) {
		TensaiFabricEvents.SERVER_STOPPED.emit((MinecraftServer) (Object) this);
	}
}
