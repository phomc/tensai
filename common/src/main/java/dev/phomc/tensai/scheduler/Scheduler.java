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

package dev.phomc.tensai.scheduler;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a task scheduler.
 */
public class Scheduler {
	private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(
			5,
			// compare "next tick time": put tasks which will be executed soon first
			// compare "priority": put higher-prioritized tasks first
			Comparator.comparingLong(Task::getNextTickTime).thenComparing((o1, o2) -> Float.compare(o2.getPriority(), o1.getPriority()))
	);
	private final AtomicLong counter = new AtomicLong();

	protected void onTick() {
		long tick = counter.getAndUpdate(operand -> safeAddition(operand, 1));

		while (true) {
			Task task = queue.peek();
			if (task == null || task.getNextTickTime() > tick) break;
			queue.poll();
			if (task.isCancelled()) continue;

			if (task.isAsync()) {
				CompletableFuture.runAsync(() -> {
					task.getExecutor().run();
				});
			} else {
				task.getExecutor().run();
			}

			if (task.getRecurringCounter() < task.getRecurringTimes()) {
				task.increaseRecurringCounter();
				schedule(task, task.getInterval());
			}
		}
	}

	private long safeAddition(long x, long delta) {
		// Copy from Math#addExact
		long y = x + delta;

		if (((x ^ y) & (delta ^ y)) < 0) {
			y = delta;
		}

		return y;
	}

	public void schedule(Task task, long delay) {
		task.setNextTickTime(safeAddition(counter.get(), delay));
		queue.offer(task);
	}

	public void schedule(Task task) {
		schedule(task, 0);
	}

	public void runAsync(Runnable executor, long delay) {
		schedule(new Task.Builder().setExecutor(executor).async().build(), delay);
	}

	public void runSync(Runnable executor, long delay) {
		schedule(new Task.Builder().setExecutor(executor).build(), delay);
	}

	public void runAsync(Runnable executor) {
		runAsync(executor, 0);
	}

	public void runSync(Runnable executor) {
		runSync(executor, 0);
	}
}
