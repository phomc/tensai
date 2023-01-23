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

package dev.phomc.tensai.fabric.client.security;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

import java.util.Objects;

public class Permission {
	private final Identifier namespace;
	private final String key;
	private final Context context;

	public Permission(@NotNull Identifier namespace, @NotNull String key, @NotNull Context context) {
		if(!key.matches("[0-9A-Za-z-_]+")){
			throw new IllegalArgumentException("invalid permission key");
		}
		this.namespace = namespace;
		this.key = key;
		this.context = context;
	}

	public Permission(String namespace, String key, Context context) {
		this(new Identifier(namespace), key, context);
	}

	@NotNull
	public Identifier getNamespace() {
		return namespace;
	}

	@NotNull
	public String getKey() {
		return key;
	}

	@NotNull
	public Context getContext() {
		return context;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Permission that = (Permission) o;
		return namespace.equals(that.namespace) && key.equals(that.key) && context == that.context;
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, key, context);
	}

	@Override
	public String toString() {
		return String.format("%s:%s", namespace, key);
	}

	public enum Context {
		/**
		 * The permission has a global effect.<br>
		 * Once the permission is granted, it remains permanently.
		 */
		GLOBAL,

		/**
		 * The permission takes effect only on a specific server.<br>
		 * Once the permission is granted, it remains permanently (for that server only).
		 */
		SERVER,

		/**
		 * The permission takes effect only on a server.<br>
		 * Once the permission is granted, it remains until the player quits the server.
		 */
		SESSION
	}
}
