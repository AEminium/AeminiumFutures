package aeminium.runtime.futures;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.DataGroup;
import aeminium.runtime.Task;
import aeminium.runtime.futures.dependencies.Dependency;


/* The Base Class for all Futures, containing the task and the dependencies. */
public abstract class HollowFuture<T> {
	
	public Dependency dep;
	public T it;
	public Task task;
	public DataGroup dg;
	
	abstract public T evaluate();
	
	protected Collection<Task> prepareDependencies(Collection<HollowFuture<?>> futures) {
		Collection<Task> c = new ArrayList<Task>();
		for(HollowFuture<?> f : futures) {
			f.dep.mergeDependencies(c);
		}
		return c;
	}

	
	public void replace(HollowFuture<T> f) {
		dep = f.dep;
		task = f.task;
		it = f.it;
	}
	
	
}
