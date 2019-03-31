package com.vogella.tasks.ui.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import com.vogella.tasks.model.TodoService;

public class TodoOverviewPart {
	public TodoOverviewPart() {
		System.out.println("TodoOverviewPart");
	}
	
	@PostConstruct
	public void createControls(Composite parent, TodoService service) {
	    service.getTodos(todos -> {
	        System.out.println("Number of Todo objects " + todos.size());
	    });
		System.out.println("@PostConstruct TodoOverviewPart");
	}
}
