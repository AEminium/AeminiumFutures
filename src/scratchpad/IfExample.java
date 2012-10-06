package scratchpad;
import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.FutureChild;
import aeminium.runtime.futures.RuntimeManager;

@SuppressWarnings("unused")
public class IfExample {
	
	
	
	public static void seq() {
		int i = 0;
		int j = 0;
		if (i < 2) {
			i++;
		} else {
			j++;
		}
		System.out.println("j: " + j);
		System.out.println("i: " + i);
	}
	
	public static void par() {
        RuntimeManager.init();
		
        final Future<Integer> i_0 = new Future<Integer>() {
        	public Integer evaluate() {
        		return 0;
        	}
        };
        
        final Future<Integer> j_0 = new Future<Integer>() {
        	public Integer evaluate() {
        		return 0;
        	}
        };
        
        final Future<Void> if_0 = new Future<Void>(i_0, j_0){
			public Void evaluate() {
				
        		if ( i_0.it < j_0.it) {
        			i_0.replace(new FutureChild<Integer>(this.task, i_0){
						@Override
						public Integer evaluate() {
							int tmp = (int) i_0.it;
							tmp++;
							return tmp;
						}
        			});
        		} else {
        			j_0.replace(new FutureChild<Integer>(this.task, j_0){
						@Override
						public Integer evaluate() {
							int tmp = (int) j_0.it;
							tmp++;
							return tmp;
						}
        			});
        		}
        		return null;
        	}
        };
        
        final Future<Void> sysout_0 = new Future<Void>(if_0){
			public Void evaluate() {
				System.out.println("i: " + i_0.it);
				return null;
			}
        };

        final Future<Void> sysout_1 = new Future<Void>(if_0){
			public Void evaluate() {
				System.out.println("j: " + j_0.it);
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
