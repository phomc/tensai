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

package dev.vmsa.tensai.fabric.test.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.vmsa.tensai.fabric.test.TensaiFabricTestMod;
import dev.vmsa.tensai.networking.PluginMessage;

@Environment(EnvType.CLIENT)
public class TensaiFabricTestClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(TensaiFabricTestMod.MOD_ID + "-client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello client!");
		PluginMessagingChannelListener.listen(new Identifier(PluginMessage.CHANNEL_VFX));
	}
}
