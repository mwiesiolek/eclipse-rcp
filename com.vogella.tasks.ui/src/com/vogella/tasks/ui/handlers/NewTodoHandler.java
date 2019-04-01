package com.vogella.tasks.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class NewTodoHandler {
	@Execute
	public void execute() {
		System.out.println((this.getClass().getSimpleName() + " called"));
	}
}
