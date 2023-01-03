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

package dev.phomc.tensai;

import dev.phomc.tensai.vfx.VisualEffects;
import dev.phomc.tensai.vfx.animations.AnimationProperty;

/**
 * <p>An entry point to all Tensai APIs.</p>
 * <p><b>For Spigot: </b>Use {@code TensaiSpigot.getInstance()}.</p>
 * <p><b>For Fabric: </b>Use {@code (Tensai) (Object) minecraftServer}.</p>
 */
public interface Tensai {
	/**
	 * <p>Get the global visual effects API. This global VFX will applies visual effects to all online players. Please
	 * note that methods like {@link VisualEffects#playAnimationOnce(String, AnimationProperty...)} might not takes
	 * player's position into account, which leads to wasted bandwidth.</p>
	 *
	 * @return Global visual effects API.
	 */
	VisualEffects getGlobalVfx();
}
