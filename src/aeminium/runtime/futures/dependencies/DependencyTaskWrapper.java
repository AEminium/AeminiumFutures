package aeminium.runtime.futures.dependencies;

import java.util.Collection;

import aeminium.runtime.Task;

public class DependencyTaskWrapper implements Dependency {

	Task task;
	
	public DependencyTaskWrapper(Task t) {
		task = t;
	}
	
	@Override
	public void mergeDependencies(Collection<Task> col) {
		col.add(task);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("=Simple=\n");
		s.append("T: " + task);
		s.append("=====\n");
		return s.toString();
	}

	
}
