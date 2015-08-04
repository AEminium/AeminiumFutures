package aeminium.runtime.futures.codegen;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.futures.FutureWrapper;
import aeminium.runtime.futures.HollowFuture;
import aeminium.runtime.futures.RuntimeManager;
import aeminium.runtime.helpers.loops.ForTask;

public class ForHelper {
	
	public static BiFunction<Integer, Integer, Integer> intSum = (Integer t1, Integer t2) -> t1 + t2;
	public static int PPS = ForTask.PPS;
	
	public static void forContinuousInt(int initial, int end, Function<Integer, Void> fun) {
		Task parent = RuntimeManager.getCurrentTask();
		Body b = forContinuousIntBody(initial, end, fun);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, parent, Runtime.NO_DEPS);
	}

	public static Body forContinuousIntBody(final int start, final int end,
			Function<Integer, Void> fun) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int bottom = start;
				int top = end;

				while (bottom < top) {
					boolean shouldCheck = (bottom % PPS == 0);
					if (shouldCheck && (top-bottom > PPS) && rt.parallelize(current)) {
						int half = (top - bottom)/2 + bottom;
						Task otherHalf = rt.createNonBlockingTask(forContinuousIntBody(half, top, fun), Hints.LOOPS);
						rt.schedule(otherHalf, current, Runtime.NO_DEPS);
						top = half;
					} else {
						fun.apply(bottom);
						bottom ++;
					}
				}
			}
		};
	}
	
	public static <T> HollowFuture<T> forContinuousIntReduce1(int initial, int end, Function<Integer, T> fun, BiFunction<T, T, T> reduce ) {
		Task parent = RuntimeManager.getCurrentTask();
		Body b = forContinuousIntReduce1Body(initial, end, fun, reduce);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, parent, Runtime.NO_DEPS);
		return new FutureWrapper<T>(current);
	}

	public static <T> Body forContinuousIntReduce1Body(final int start, final int end,
			Function<Integer, T> fun, BiFunction<T, T, T> reduce) {
		return new Body() {
			T field;
			ArrayList<Task> children = new ArrayList<Task>();
			@SuppressWarnings("unchecked")
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int bottom = start;
				int top = end;

				while (bottom < top) {
					boolean shouldCheck = (bottom % PPS == 0);
					if (shouldCheck && (top-bottom > PPS) && rt.parallelize(current)) {
						int half = (top - bottom)/2 + bottom;
						Body b = forContinuousIntReduce1Body(half, top, fun, reduce);
						Task otherHalf = rt.createNonBlockingTask(b, Hints.LOOPS);
						rt.schedule(otherHalf, current, Runtime.NO_DEPS);
						children.add(otherHalf);
						top = half;
					} else {
						T res = fun.apply(bottom);
						field = (field == null) ? res : reduce.apply(field, res);
						bottom ++;
					}
				}
				
				for (Task child : children) {
					field = reduce.apply(field, (T) child.getResult());
				}
				current.setResult(field);
			}
		};
	}
	

	
	
	public static void main(String[] args) {
		RuntimeManager.init();
		ForHelper.forContinuousInt(0, 10, (Integer t) -> {
			System.out.println(t);
			return null;
		});
		
		HollowFuture<Integer> t = ForHelper.forContinuousIntReduce1(0, 10, (Integer i) -> {
			return 1;
		}, ForHelper.intSum);
		System.out.println("Sum:" + t.get());
		RuntimeManager.shutdown();
		
	}
}
