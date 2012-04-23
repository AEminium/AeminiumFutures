package examples;
import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.RuntimeManager;

public class PlusExample {

	
	public static void seq() {
		int r = getFirst() + getSecond();	
		System.out.println("Result:" + r);
	}
	
	
	public static void par() {
        RuntimeManager.init();
		
        final Future<Integer> getFirst_ret = new Future<Integer>() {
        	public Integer evaluate() {
        		return getFirst();
        	}
        };
        
        final Future<Integer> getSecond_ret = new Future<Integer>() {
        	public Integer evaluate() {
        		return getSecond();
        	}
        };
        final Future<Integer> future_r = new Future<Integer>(getFirst_ret, getSecond_ret){
        	public Integer evaluate() {
        		return getFirst_ret.it + getSecond_ret.it;
        	}
        };
        new Future<Void>(future_r){
        	public Void evaluate() {
        		int r = (int) future_r.it;
        		System.out.println("Result:" + r);
				return null;
        	}
        };
        RuntimeManager.shutdown();
	}
	
	public static void main(String[] args) {
		seq();
		par();        
	}

	private static int getSecond() {
		return 1;
	}

	private static int getFirst() {
		return 2;
	}
	
}
