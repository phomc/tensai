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

package dev.phomc.tensai.fabric.client.keybinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;

import dev.phomc.tensai.fabric.client.GameOptionProcessor;
import dev.phomc.tensai.fabric.client.i18n.CustomTranslationStorage;
import dev.phomc.tensai.fabric.client.mixins.KeyBindingMixin;
import dev.phomc.tensai.fabric.client.security.Permission;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.networking.Channel;
import dev.phomc.tensai.util.ReflectionUtil;

public class KeyBindingManager {
	public static final Identifier KEYBINDING_NAMESPACE = new Identifier(Channel.KEYBINDING.getNamespace());
	public static final Permission KEY_RECORD_PERMISSION = new Permission(
			KEYBINDING_NAMESPACE, "record",
			"gui.permissionPrompt.message.keybinding",
			Permission.Context.SERVER, true
	);
	private static final int DEFAULT_INPUT_DELAY = 5;
	private static final KeyBindingManager INSTANCE = new KeyBindingManager();
	private List<net.minecraft.client.option.KeyBinding> registeredKeys = new ArrayList<>();
	private Map<Key, Integer> stateTable = new HashMap<>();
	private int inputDelay = DEFAULT_INPUT_DELAY;

	public static KeyBindingManager getInstance() {
		return INSTANCE;
	}

	public static InputUtil.Key getInputKey(@NotNull Key key) {
		return key.isMouse() ? InputUtil.Type.MOUSE.createFromCode(key.getGLFWCode()) : InputUtil.Type.KEYSYM.createFromCode(key.getGLFWCode());
	}

	public static Key lookupKey(@NotNull InputUtil.Key key) {
		return Key.lookupGLFW(key.getCode(), key.getCategory() == InputUtil.Type.MOUSE);
	}

	public int getInputDelay() {
		return inputDelay;
	}

	public void setInputDelay(int inputDelay) {
		this.inputDelay = Math.max(inputDelay, DEFAULT_INPUT_DELAY);
	}

	public boolean testBulkAvailability(@NotNull List<KeyBinding> keymap) {
		for (KeyBinding keyBinding : keymap) {
			InputUtil.Key key = getInputKey(keyBinding.getKey());

			if (KeyBindingMixin.getKeyCodeMapping().containsKey(key)) {
				return false;
			}
		}

		return true;
	}

	public void registerBulk(@NotNull List<KeyBinding> keymap) {
		unregisterAll();

		for (KeyBinding keyBinding : keymap) {
			net.minecraft.client.option.KeyBinding v = new net.minecraft.client.option.KeyBinding(
					"key.tensai." + keyBinding.getKey(),
					keyBinding.getKey().isMouse() ? InputUtil.Type.MOUSE : InputUtil.Type.KEYSYM,
					keyBinding.getKey().getGLFWCode(),
					"key.categories.tensai"
			);
			CustomTranslationStorage.getInstance().put(v.getTranslationKey(), keyBinding.getName());
			KeyBindingHelper.registerKeyBinding(v);
			registeredKeys.add(v);
		}

		((GameOptionProcessor) MinecraftClient.getInstance().options).reprocessKeys();
	}

	public void unregisterAll() {
		for (net.minecraft.client.option.KeyBinding keyBinding : registeredKeys) {
			CustomTranslationStorage.getInstance().remove(keyBinding.getTranslationKey());
			KeyBindingMixin.getId2KeyMapping().remove(keyBinding.getTranslationKey());
			KeyBindingMixin.getKeyCodeMapping().remove(keyBinding.getDefaultKey());
		}

		//noinspection unchecked
		((List<net.minecraft.client.option.KeyBinding>) Objects.requireNonNull(ReflectionUtil.getDeclaredFieldValue(KeyBindingRegistryImpl.class, null, "MODDED_KEY_BINDINGS"))).removeAll(registeredKeys);

		stateTable = new HashMap<>();
		registeredKeys = new ArrayList<>();
		inputDelay = DEFAULT_INPUT_DELAY;
	}

	public Map<Key, KeyState> fetchStates() {
		Map<Key, KeyState> states = new HashMap<>();

		for (net.minecraft.client.option.KeyBinding key : registeredKeys) {
			int n = ((KeyBindingMixin) key).getTimesPressed();
			Key k = lookupKey(key.getDefaultKey());
			Integer old = stateTable.put(k, n);

			if (old == null || old != n) {
				states.put(k, new KeyState(n));
			}
		}

		return states;
	}
}
