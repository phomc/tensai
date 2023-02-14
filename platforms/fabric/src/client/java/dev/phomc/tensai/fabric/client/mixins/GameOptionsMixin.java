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

package dev.phomc.tensai.fabric.client.mixins;

import java.util.List;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;

import dev.phomc.tensai.fabric.client.GameOptionProcessor;
import dev.phomc.tensai.fabric.client.keybinding.KeyBindingManager;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements GameOptionProcessor {
	@Mutable
	@Shadow
	@Final
	public KeyBinding[] allKeys;

	@Override
	public void reprocessKeys() {
		List<KeyBinding> newKeysAll = Lists.newArrayList(KeyBindingRegistryImpl.process(allKeys));
		newKeysAll.removeAll(KeyBindingManager.getInstance().getNonEditableKeyBindings());
		allKeys = newKeysAll.toArray(new KeyBinding[0]);
	}

	@Override
	public void resetKeys(List<KeyBinding> keys) {
		List<KeyBinding> newKeysAll = Lists.newArrayList(allKeys);
		newKeysAll.removeAll(keys);
		allKeys = newKeysAll.toArray(new KeyBinding[0]);
	}
}
