package aeminium.runtime.futures;

import java.util.Arrays;

import aeminium.runtime.Task;

/* A Future Task with Parent. */
public abstract class FutureChild<T> extends HollowFuture<T> {
	Task parent;

	public FutureChild(Task p, HollowFuture<?>... futures) {
		parent = p;
		RuntimeManager.submit(this, parent, prepareDependencies(Arrays.asList(futures)));
	}
}
