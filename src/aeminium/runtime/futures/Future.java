package aeminium.runtime.futures;

import java.util.Arrays;
import java.util.Collection;

import aeminium.runtime.Runtime;

public abstract class Future<T> extends HollowFuture<T>{
	
	public Future(HollowFuture<?>... futures) {
		RuntimeManager.submit(this, Runtime.NO_PARENT, prepareDependencies(Arrays.asList(futures)));
	}
	
	public Future(Collection<HollowFuture<?>> futures) {
		RuntimeManager.submit(this, Runtime.NO_PARENT, prepareDependencies(futures));
	}
		
}
