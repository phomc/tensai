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

package dev.phomc.tensai.server;

import dev.phomc.tensai.scheduler.Scheduler;
import dev.phomc.tensai.server.vfx.VisualEffects;
import dev.phomc.tensai.server.vfx.animations.AnimationProperty;

/**
 * <p>An entry point to all Tensai APIs.</p>
 * <p><b>For Spigot: </b>Use {@code TensaiSpigot.getInstance()}.</p>
 * <p><b>For Fabric: </b>Use {@code (Tensai) (Object) minecraftServer}.</p>
 */
public interface TensaiServer {
	/**
	 * <p>Get the global visual effects API. This global VFX will applies visual effects to all online players. Please
	 * note that methods like {@link VisualEffects#playAnimationOnce(String, AnimationProperty...)} might not takes
	 * player's position into account, which leads to wasted bandwidth.</p>
	 *
	 * @return Global visual effects API.
	 */
	VisualEffects getGlobalVfx();

	/**
	 * Gets Tensai's internal task scheduler.
	 *
	 * @return {@link Scheduler}
	 */
	Scheduler getTaskScheduler();

	/**
	 * Checks whether the current thread is the primary one.<br>
	 * The primary thread is used to execute most activities on such as: ticking, event handling, etc. Besides, there
	 * are asynchronous operations which are executed on auxiliary threads, e.g: networking, I/O, etc.<br>
	 * It is <b>important</b> to distinct and utilize them in safe manners.
	 *
	 * @return {@code true} or {@code false}
	 */
	boolean isPrimaryThread();
}
