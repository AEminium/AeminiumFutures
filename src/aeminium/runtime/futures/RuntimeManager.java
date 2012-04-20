package aeminium.runtime.futures;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class RuntimeManager {
	final static Runtime rt = Factory.getRuntime();
	
	public static void init() {
		rt.init();
	}

	public static void shutdown() {
		rt.shutdown();
	}
	
	public static <T> void submit(final Future<T> f, Collection<Task> deps) {
		Body b = new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				f.it = f.evaluate();
			}};
		Task t = rt.createNonBlockingTask(b, Runtime.NO_HINTS);
		f.task = t;
		rt.schedule(t, Runtime.NO_PARENT, deps);
	}
	
}