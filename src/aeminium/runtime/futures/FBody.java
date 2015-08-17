package aeminium.runtime.futures;

import aeminium.runtime.Body;
import aeminium.runtime.Task;

public abstract class FBody<T> implements Body{
	Task t;
	T ret;
	
	public T get() {
		t.getResult();
		return ret;
	}
	
	public void setTask(Task t) {
		this.t = t;
	}
	
	public void setResult(T res) {
		ret = res;
	}
}
