package examples.montecarlo;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.HollowFuture;
import aeminium.runtime.futures.RuntimeManager;

public class PICalculation {
	
	public static int target = 10000000;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		RuntimeManager.init();
		
		Future<Void> master = new Future<Void>() {

			@Override
			public Void evaluate() {
				
				final int workers = 10000;
				final Collection<HollowFuture<?>> fs = new ArrayList<HollowFuture<?>>();
				for (int i=0; i< workers; i++) {
					fs.add( new Future<Double>() {

						@Override
						public Double evaluate() {
							MersenneTwisterFast random = new MersenneTwisterFast();
							int darts = target/workers;
							int c = 0;
							double x_coord, y_coord, r; 
							
							for (int i=0; i < darts; i++) {							
								r = random.nextDouble();
								x_coord = (2.0 * r) - 1.0;
								r = random.nextDouble();
								y_coord = (2.0 * r) - 1.0;

								/* if dart lands in circle, increment score */
								if ((x_coord*x_coord + y_coord*y_coord) <= 1.0)
									c++;
							}
							
							return 4.0 * (double) c/(double)darts;
						}
						
					} );
				}
				
				
				new Future<Void>(fs) {

					@SuppressWarnings("unchecked")
					@Override
					public Void evaluate() {
						double sum = 0;
						for (HollowFuture<?> f : fs) {
							double t = ((HollowFuture<Double>)f).it;
							sum += t;
						}
						double pi = sum/((double) fs.size());
						System.out.println("Final: " + pi);
						return null;
					}
					
				};
				
				
				return null;
			}
		};
		
		RuntimeManager.shutdown();
	}
}
