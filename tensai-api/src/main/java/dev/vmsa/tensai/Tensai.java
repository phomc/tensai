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

package dev.vmsa.tensai;

import dev.vmsa.tensai.vfx.animations.AnimationProperty;
import dev.vmsa.tensai.vfx.animations.AnimationsPlayer;

/**
 * <p>An entry point to all Tensai APIs.</p>
 * <p><b>For Spigot: </b>Use {@code TensaiSpigot.getInstance()}.</p>
 * <p><b>For Fabric: </b>Use {@code (Tensai) (Object) minecraftServer}.</p>
 *
 */
public interface Tensai extends AnimationsPlayer {
	/**
	 * <p>Play animation to all online players. Anti-lag measures, such as "only send animation to players
	 * near a specified location" won't works, since the position data could be part of animation properties.
	 * See {@link AnimationsPlayer#playAnimationOnce(String, double, double, AnimationProperty...)} for details.</p>
	 */
	@Override
	void playAnimationOnce(String type, double startSec, double durationSec, AnimationProperty<?>... properties);
}
