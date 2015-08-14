package aeminium.runtime.futures;

import java.util.Collection;

import aeminium.runtime.DataGroup;
import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class RuntimeManager {
	static ThreadLocal<Task> currentTask = new ThreadLocal<Task>();

	static int rtcalls = 0;
	public final static Runtime rt = Factory.getRuntime();
	
	public static void init() {
		if (rtcalls++ == 0) {
			rt.init();
			rt.addErrorHandler(new ErrorHandler() {

				@Override
				public void handleTaskException(Task task, Throwable t) {
					t.printStackTrace();
				}

				@Override
				public void handleLockingDeadlock() {
					System.err.println("Deadlocked");
				}

				@Override
				public void handleDependencyCycle(Task task) {
					System.err.println("Cyclic dependency " + task);
				}

				@Override
				public void handleTaskDuplicatedSchedule(Task task) {
					System.err.println("Duplicated task " + task);
				}

				@Override
				public void handleInternalError(Error err) {
					err.printStackTrace();
				}
				
			});
		}
	}

	public static void shutdown() {
		rtcalls--;
		if (rtcalls < 1) {
			rt.shutdown();
			rtcalls = 0;
		}
	}	
	
	public static boolean shouldSeq() {
		return !rt.parallelize(currentTask.get());
	}
	
	public static <T> void submit(final HollowFuture<T> f, Collection<Task> deps) {
		RuntimeManager.submit(f, Runtime.NO_PARENT, deps);
	}
	
	public static <T> void submit(final HollowFuture<T> f, Task parent, Collection<Task> deps) {
		/*
		 * Currently being done by the compiler at the declaration site
 		if (!rt.parallelize(parent)) {
		 
			f.it = f.body.evaluate(parent);
			return;
		}
		*/
		Task t;
		if (f.dg != null) {
			t = rt.createAtomicTask(f, f.dg, Runtime.NO_HINTS);
		} else {
			t = rt.createNonBlockingTask(f, Runtime.NO_HINTS);
		}
		f.task = t;
		rt.schedule(t, parent, deps);
	}
	
	public static DataGroup getNewDataGroup() {
		return rt.createDataGroup();
	}
	
}