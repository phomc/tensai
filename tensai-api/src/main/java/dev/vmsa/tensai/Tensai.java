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

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Tensai implements Closeable {
	private static Tensai INSTANCE;
	private static Function<Object, Tensai> INSTANCE_GETTER;

	/**
	 * <p>Obtain the singleton instance of {@link Tensai}, which allows you to use most of Tensai features.</p>
	 * <p>On Spigot, you can obtain this in your plugin's <code>onEnable</code> method (assuming you have
	 * {@code depends: [Tensai]} in your plugin.yml.</p>
	 * <p>On Fabric, you can obtain this by listening to {@code TensaiFabricEvents.INSTANCE_CREATE} event
	 * callback.</p>
	 * @return
	 */
	public static Tensai getInstance() {
		if (INSTANCE == null && INSTANCE_GETTER != null) return INSTANCE_GETTER.apply(null);
		return INSTANCE;
	}

	public static Tensai getInstance(Object minecraftServer) {
		if (INSTANCE_GETTER == null) {
			if (INSTANCE != null) return INSTANCE;
			return null;
		}

		return INSTANCE_GETTER.apply(minecraftServer);
	}

	public static void createInstance(Supplier<Tensai> supplier) {
		if (INSTANCE != null) throw new IllegalStateException("Tensai instance is already initialized");
		INSTANCE = supplier.get();
	}

	public static boolean isInstancePresent() {
		return INSTANCE != null;
	}

	public static void setInstanceGetter(Function<Object, Tensai> getter) {
		INSTANCE_GETTER = getter;
	}

	@Override
	public void close() throws IOException {
		if (INSTANCE == this) INSTANCE = null;
	}
}
