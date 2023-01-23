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

package dev.phomc.tensai.server.vfx.animations;

import java.io.DataOutput;
import java.io.IOException;

import dev.phomc.tensai.server.networking.PluginMessage;

public class AnimationPluginMessage extends PluginMessage {
	public static final int PLAY_ONCE = 0x00;

	public final String animationType;
	public final int playMode;
	public final double startSec;
	public final double durationSec;
	public final AnimationProperty<?>[] properties;

	public AnimationPluginMessage(String animationType, int playMode, double startSec, double durationSec, AnimationProperty<?>... properties) {
		super(PluginMessage.CHANNEL_VFX, "animation");
		this.animationType = animationType;
		this.playMode = playMode;
		this.startSec = startSec;
		this.durationSec = durationSec;
		this.properties = properties;
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		stream.writeUTF(animationType);
		stream.writeByte(playMode);
		stream.writeDouble(startSec);
		stream.writeDouble(durationSec);

		stream.writeInt(properties.length);
		for (AnimationProperty<?> prop : properties) AnimationProperty.SERIALIZER.serialize(prop, stream);
	}
}
