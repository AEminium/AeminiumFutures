package aeminium.runtime.futures;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.futures.dependencies.DependencyTaskWrapper;
import aeminium.runtime.implementations.Factory;

public class RuntimeManager {
	static int rtcalls = 0;
	final static Runtime rt = Factory.getRuntime();
	
	public static void init() {
		if (rtcalls++ == 0)
			rt.init();
	}

	public static void shutdown() {
		rtcalls--;
		if (rtcalls < 1) {
			rt.shutdown();
			rtcalls = 0;
		}
	}
	
	public static <T> void submit(final HollowFuture<T> f, Collection<Task> deps) {
		RuntimeManager.submit(f, Runtime.NO_PARENT, deps);
	}
	
	public static <T> void submit(final HollowFuture<T> f, Task parent, Collection<Task> deps) {
		Body b = new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				f.it = f.evaluate();
			}
			@Override
			public String toString() {
				return f.toString();
			}
		};
			
		Task t;
		if (f.dg != null) {
			t = rt.createAtomicTask(b, f.dg, Runtime.NO_HINTS);
		} else {
			t = rt.createNonBlockingTask(b, Runtime.NO_HINTS);
		}
		f.dep = new DependencyTaskWrapper(t);
		f.task = t;
		rt.schedule(t, parent, deps);
	}
	
	public static DataGroup getNewDataGroup() {
		return rt.createDataGroup();
	}
	
}