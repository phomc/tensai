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

package dev.phomc.tensai.bukkit.event;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyState;

/**
 * This event is triggered whenever one or more key states is updated.<br>
 * <b>Note:</b>
 * <ul>
 *     <li>The timing may be different from client-side due to connection latency.</li>
 *     <li>This event is called asynchronously.</li>
 *     <li>No key state update will be sent back to the client.</li>
 *     <li>The server-sided key state is shared across plugins.</li>
 * </ul>
 */
public final class KeyStateUpdateEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private final Map<Key, KeyState> keyStates;

	public KeyStateUpdateEvent(Player player, Map<Key, KeyState> keyStates) {
		super(player);
		this.keyStates = keyStates;
	}

	/**
	 * Returns the current key states.
	 *
	 * @return an <b>unmodifiable</b> map representing the key states
	 */
	public Map<Key, KeyState> getKeyStates() {
		return this.keyStates;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
