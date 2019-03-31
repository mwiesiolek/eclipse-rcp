package com.vogella.tasks.ui.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

public class TodoDetailsPart {
	
	public TodoDetailsPart() {
		System.out.println("TodoDetailsPart");
	}
	
	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println("@PostConstruct TodoDetailsPart");
	}
}
