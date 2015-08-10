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
	
	public static BiFunction<Integer, Integer, Integer> intSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Long, Long, Long> longSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Float, Float, Float> floatSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Double, Double, Double> doubleSum = (t1, t2) -> t1 + t2;
	
	public static void forContinuousInt(int start, int end, Function<Integer, Void> fun, short hint) {
		if (start == end) return;
		if (end-start < 10000 && Hints.check(hint, Hints.SMALL)) {
			for (int i=start; i<end; i++)
				fun.apply(i);
			return;
		}
		
		Task parent = RuntimeManager.getCurrentTask();
		Body b = forContinuousIntBody(start, end, fun, hint);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, parent, Runtime.NO_DEPS);
		current.getResult();
	}

	public static Body forContinuousIntBody(final int start, final int end,
			Function<Integer, Void> fun, short hint) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if (start == end) return;
				if (end-start < 10000 && Hints.check(hint, Hints.SMALL)) {
					for (int i=start; i<end; i++) {
						fun.apply(i);
					}
					return;
				}
				
				ArrayList<Task> children = new ArrayList<Task>();
				
				int bottom = start;
				int top = end;
				int pps = ForTask.PPS;
				if (Hints.check(hint, Hints.SMALL))
					pps *= 100;
				
				while (bottom < top) {
					boolean checkPar = bottom % pps == 0;
					if (checkPar && top-bottom > 1 && rt.parallelize(current)) {
						int half = (top - bottom)/2 + bottom;
						Task otherHalf = rt.createNonBlockingTask(forContinuousIntBody(half, top, fun, hint), Hints.LOOPS);
						rt.schedule(otherHalf, current, Runtime.NO_DEPS);
						children.add(otherHalf);
						top = half;
					} else {						
						fun.apply(bottom++);
					}
				}
				for (Task child : children) 
					child.getResult();
			}
		};
	}
	
	public static <T> HollowFuture<T> forContinuousIntReduce1(int start, int end, Function<Integer, T> fun, BiFunction<T, T, T> reduce ) {
		Task parent = RuntimeManager.getCurrentTask();
		Body b = forContinuousIntReduce1Body(start, end, fun, reduce);
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
				if (start == end) return;
				int bottom = start;
				int top = end;
				
				int pps = ForTask.PPS;
				
				while (bottom < top) {
					boolean shouldCheck = (bottom % pps == 0);
					if (shouldCheck && (top-bottom > 1) && rt.parallelize(current)) {
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
		}, Hints.NO_HINTS);
		
		HollowFuture<Integer> t = ForHelper.forContinuousIntReduce1(0, 10, (Integer i) -> {
			return 1;
		}, ForHelper.intSum);
		System.out.println("Sum:" + t.get());
		RuntimeManager.shutdown();
		
	}
}
