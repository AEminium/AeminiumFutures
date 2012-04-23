package aeminium.runtime.futures.dependencies;

import java.util.Collection;

import aeminium.runtime.Task;

public interface Dependency {
	public void mergeDependencies(Collection<Task> col);
}
