package examples;
import aeminium.runtime.Task;
import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.FutureChildren;
import aeminium.runtime.futures.HollowFuture;
import aeminium.runtime.futures.RuntimeManager;
import aeminium.runtime.futures.codegen.Expression;
import aeminium.runtime.futures.codegen.WhileHelper;

@SuppressWarnings("unused")
public class WhileExample {
	
	static int N = 10;
	
	public static void seq() {
		int i = 0;
		while (i < N) {
			System.out.println("i:" + i++);
		}
		System.out.println("fi:" + i++);
	}
	
	public static void par() {
        RuntimeManager.init();
		
        final Future<Integer> i_0 = new Future<Integer>() {
        	public Integer evaluate() {
        		return 0;
        	}
        };
        
        final Future<Void> while_0 = new Future<Void>(i_0){
			public Void evaluate() {
				WhileHelper.whileLoop(this.task, new Expression<Boolean>() {
					public Boolean evaluate(Task t) {
						return i_0.it < N;
					}
				}, new Expression<Void>() {
					@Override
					public Void evaluate(Task t) {
						final FutureChildren<Integer> tmp1 = new FutureChildren<Integer>(t, i_0){
							public Integer evaluate() {
								int i = (int) i_0.it;
								System.out.println("i:" + i);
								i++;
								
								return i;
							}
	        			};
	        			final FutureChildren<Void> tmp2 = new FutureChildren<Void>(t, tmp1){
							public Void evaluate() {
								i_0.replace(tmp1);
								return null;
							}
	        			};
						
						return null;
					}
				});
        		return null;
        	}
        };
        
        final Future<Void> sysout_0 = new Future<Void>(while_0){
			public Void evaluate() {
				System.out.println("fi: " + i_0.it);
				return null;
			}
        };
        
        RuntimeManager.shutdown();
	}
	
	public static void main(String[] args) {
		seq();
		par();
	}
}
