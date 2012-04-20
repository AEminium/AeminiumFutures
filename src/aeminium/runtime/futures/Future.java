package aeminium.runtime.futures;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public abstract class Future<T> {
	
	public Task task;
	public T it;

	
	public Future() {
		RuntimeManager.submit(this, Runtime.NO_DEPS);
	}
	
	public Future(Future<?>... futures) {
		Collection<Task> c = new ArrayList<Task>();
		for(Future<?> f : futures) {
			c.add(f.task);
		}
		RuntimeManager.submit(this, Runtime.NO_DEPS);
	}
	
	abstract public T evaluate();
	
}
