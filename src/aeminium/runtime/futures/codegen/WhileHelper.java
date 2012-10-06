package aeminium.runtime.futures.codegen;

import aeminium.runtime.Task;
import aeminium.runtime.futures.FutureChild;

public class WhileHelper {

	// Recursive Implementation of While in Tasks
	public static void whileLoop(final Task currentTask, final Expression<Boolean> e,
			final Expression<Void> body) {
		if (e.evaluate(currentTask)) {
			
			final FutureChild<Void> b = new FutureChild<Void>(currentTask) {
				@Override
				public Void evaluate() {
					body.evaluate(this.task);
					return null;
				}
			};

			new FutureChild<Void>(currentTask, b) {
				@Override
				public Void evaluate() {
					WhileHelper.whileLoop(this.task, e, body);
					return null;
				}
			};
		}

	}
}
