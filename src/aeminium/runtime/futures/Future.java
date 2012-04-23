package aeminium.runtime.futures;

import aeminium.runtime.Runtime;

public abstract class Future<T> extends HollowFuture<T>{
	
		
	public Future(Future<?>... futures) {
		RuntimeManager.submit(this, Runtime.NO_PARENT, prepareDependencies(futures));
	}
		
}
