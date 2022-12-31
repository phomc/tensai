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

package dev.vmsa.tensai.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>Emit and listen for events. You can transform event outputs with {@link #then(Function)} method, or simply
 * listen for events with {@link #listen(Consumer)}.</p>
 * <p><b>Listeners chaining: </b> You can chain multiple listeners with {@link #then(Function)}: <pre>
 * {@code
 * // Initial event
 * public static final EventEmitter<String> MY_EVENT = new EventEmitter<>();
 * // 1st pass
 * public static final EventEmitter<Integer> MY_EVENT_INT = MY_EVENT.then(s -> Integer.parse(s));
 * public static final EventEmitter<Double> MY_EVENT_DOUBLE = MY_EVENT.then(s -> Double.parse(s));
 * // 2nd pass
 * public static final EventEmitter<Boolean> IS_ANSWER = MY_EVENT_INT.then(i -> i == 42);
 * }</pre></p>
 *
 * @param <T> Type of event object.
 */
public class EventEmitter<T> {
	public final List<Tuple<EventEmitter<?>, Function<T, ?>>> chains = new ArrayList<>();

	/**
	 * <p>Transform incoming event objects.</p>
	 * @param <R> New event type.
	 * @param callback Event object transforming function.
	 * @return A new {@link EventEmitter} for chaining.
	 */
	public <R> EventEmitter<R> then(Function<T, R> callback) {
		EventEmitter<R> next = new EventEmitter<>();
		chains.add(new Tuple<>(next, callback));
		return next;
	}

	/**
	 * <p>Listen for events.</p>
	 * @param callback Will be called when an event object emitted using {@link #emit(Object)}.
	 * @return A new {@link EventEmitter} for chaining.
	 */
	public EventEmitter<Void> listen(Consumer<T> callback) {
		return then(v -> {
			callback.accept(v);
			return null;
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void emit(T event) {
		for (Tuple<EventEmitter<?>, Function<T, ?>> chain : chains) {
			EventEmitter<?> emitter = chain.a;
			Function<T, ?> transformer = chain.b;
			((EventEmitter) emitter).emit(transformer.apply(event));
		}
	}
}
