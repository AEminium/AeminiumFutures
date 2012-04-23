package aeminium.runtime.futures.dependencies;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.Task;

public class DependencyCollection implements Dependency {

	Collection<Task> tasks = new ArrayList<Task>();
	
	public void addDependency(Dependency t) {
		t.mergeDependencies(tasks);
	}
	
	@Override
	public void mergeDependencies(Collection<Task> col) {
		col.addAll(tasks);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("=====\n");
		for (Task t : tasks) {
			s.append("T: " + t);
		}
		s.append("=====\n");
		return s.toString();
	}
	
}
