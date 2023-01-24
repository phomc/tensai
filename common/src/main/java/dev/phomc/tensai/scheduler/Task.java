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

import org.jetbrains.annotations.NotNull;

public class Task {
	private float priority;
	private int recurringTimes;
	private int recurringCounter;
	private long interval;
	private Runnable executor;
	private long nextTickTime;
	private boolean cancelled;

	Task() {
		//
	}

	public float getPriority() {
		return priority;
	}

	public int getRecurringTimes() {
		return recurringTimes;
	}

	public int getRecurringCounter() {
		return recurringCounter;
	}

	void increaseRecurringCounter() {
		if (recurringTimes == Integer.MAX_VALUE) return;
		recurringCounter++;
	}

	public long getInterval() {
		return interval;
	}

	@NotNull
	public Runnable getExecutor() {
		return executor;
	}

	public long getNextTickTime() {
		return nextTickTime;
	}

	void setNextTickTime(long nextTickTime) {
		this.nextTickTime = nextTickTime;
	}

	public boolean isCancelled() {
		return cancelled || recurringCounter >= recurringTimes;
	}

	public void cancel() {
		cancelled = true;
	}

	public static class Builder {
		private float priority;
		private int recurringTimes;
		private long interval;
		private Runnable executor;

		public Builder setPriority(float priority) {
			this.priority = priority;
			return this;
		}

		public Builder setRecurringTimes(int recurringTimes) {
			this.recurringTimes = recurringTimes;
			return this;
		}

		public Builder setInfiniteRecurrence() {
			this.recurringTimes = Integer.MAX_VALUE;
			return this;
		}

		public Builder setInterval(long interval) {
			this.interval = interval;
			return this;
		}

		public Builder setExecutor(@NotNull Runnable executor) {
			this.executor = executor;
			return this;
		}

		@NotNull
		public Task build() {
			if (executor == null) {
				throw new RuntimeException();
			}

			Task task = new Task();
			task.priority = Math.max(0, priority);
			task.recurringTimes = Math.max(0, recurringTimes);
			task.interval = Math.max(1, interval);
			task.executor = executor;
			return task;
		}
	}
}
