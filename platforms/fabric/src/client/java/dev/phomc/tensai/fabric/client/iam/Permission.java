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

package dev.phomc.tensai.fabric.client.iam;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Permission {
	private final Identifier namespace;
	private final String key;
	private final Text description;
	private final Context context;
	private final boolean persistent;

	public Permission(@NotNull Identifier namespace, @NotNull String key, @NotNull Text description, @NotNull Context context, boolean persistent) {
		if (!key.matches("[0-9A-Za-z-_]+")) {
			throw new IllegalArgumentException("invalid permission key");
		}

		this.namespace = namespace;
		this.key = key;
		this.description = description;
		this.context = context;
		this.persistent = persistent;
	}

	public Permission(String namespace, String key, Text description, Context context, boolean persistent) {
		this(new Identifier(namespace), key, description, context, persistent);
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
	public Text getDescription() {
		return description;
	}

	@NotNull
	public Context getContext() {
		return context;
	}

	public boolean isPersistent() {
		return persistent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Permission that = (Permission) o;
		return namespace.equals(that.namespace) && key.equals(that.key) && description.equals(that.description) && context == that.context && persistent == that.persistent;
	}

	@Override
	public int hashCode() {
		// context & persistent are metadata, should not be allowed in hashcode generation
		return Objects.hash(namespace, key);
	}

	@Override
	public String toString() {
		return String.format("%s:%s", namespace, key);
	}

	public enum Context {
		/**
		 * The permission has a global effect (for all servers).
		 */
		GLOBAL,

		/**
		 * The permission takes effect only on whitelisted server.<br>
		 * The server-level permission can override global-level permission if a collision occurs.
		 */
		SERVER
	}
}
