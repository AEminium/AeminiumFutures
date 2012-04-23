package aeminium.runtime.futures;

import aeminium.runtime.Task;

public abstract class FutureChildren<T> extends HollowFuture<T> {
	Task parent;

	public FutureChildren(Task p, HollowFuture<?>... futures) {
		parent = p;
		RuntimeManager.submit(this, parent, prepareDependencies(futures));
	}
}
