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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

import dev.phomc.tensai.fabric.client.gui.PermissionTableGUI;
import dev.phomc.tensai.fabric.client.gui.TensaiScreen;

public class ClientAuthorizer {
	private final PermissionStorage persist = new PermissionStorage();
	private final PermissionStorage tempo = new PermissionStorage();

	public PermissionStorage getPersistentStorage() {
		return persist;
	}

	public boolean isGranted(@NotNull Permission permission, @Nullable String serverAddr) {
		if (tempo.isGranted(permission, serverAddr)) {
			return true;
		}

		return persist.isGranted(permission, serverAddr);
	}

	public PermissionData forceGrant(@NotNull Permission permission, @Nullable String serverAddr) {
		if (permission.getContext() == Permission.Context.SERVER && serverAddr == null) {
			throw new IllegalArgumentException("no server address specified for a server-level permission");
		}

		PermissionData data = (permission.isPersistent() ? persist : tempo).getPermits()
				.computeIfAbsent(permission.toString(), p -> new PermissionData());

		if (permission.getContext() == Permission.Context.SERVER) {
			data.getPermittedServers().add(serverAddr);
		}

		return data;
	}

	/**
	 * Try to grant the given permission.<br>
	 * <b>SYNCHRONOUS OPERATION</b>
	 *
	 * @param permission permission
	 * @param serverAddr server address (for server-level permission)
	 * @param callback callback
	 */
	public void tryGrant(@NotNull Permission permission, @Nullable String serverAddr, @NotNull BooleanConsumer callback) {
		if (isGranted(permission, serverAddr)) {
			callback.accept(true);
			return;
		}

		MinecraftClient.getInstance().setScreen(new ConfirmScreen(
				ok -> {
					if (ok) {
						forceGrant(permission, serverAddr);
					}

					MinecraftClient.getInstance().setScreen(null);
					callback.accept(ok);
				},
				Text.translatable("gui.permissionPrompt.title"),
				permission.getDescription(),
				Text.translatable("gui.permissionPrompt.accept"),
				Text.translatable("gui.permissionPrompt.decline")
		));
	}

	/**
	 * Try to grant multiple permissions.<br>
	 * <b>SYNCHRONOUS OPERATION</b>
	 *
	 * @param permissions permission set
	 * @param serverAddr server address (for server-level permission)
	 * @param callback callback
	 */
	public void tryGrantMulti(@NotNull Set<Permission> permissions, @Nullable String serverAddr, @NotNull Consumer<Map<Permission, Boolean>> callback) {
		Map<Permission, Boolean> map = new HashMap<>();
		int k = 0;

		for (Permission perm : permissions) {
			boolean granted = isGranted(perm, serverAddr);
			if (!granted) k++;
			map.put(perm, granted);
		}

		if (k == 0) {
			callback.accept(map);
			return;
		}

		MinecraftClient.getInstance().setScreen(new TensaiScreen(new PermissionTableGUI(map, true, true, () -> {
			for (Permission perm : permissions) {
				if (map.get(perm)) {
					forceGrant(perm, serverAddr);
				}
			}

			MinecraftClient.getInstance().setScreen(null);
			callback.accept(map);
		})));
	}
}
