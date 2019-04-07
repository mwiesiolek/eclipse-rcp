package com.vogella.tasks.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.vogella.tasks.model.Todo;
import com.vogella.tasks.ui.parts.TodoDetailsPart;

public class TodoWizardPage1 extends WizardPage {

	private Todo todo;
	
	protected TodoWizardPage1(Todo todo) {
		super("First step");
		this.todo = todo;
	}

	@Override
	public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        setPageComplete(true);
        setControl(container);
		
		TodoDetailsPart part = new TodoDetailsPart(todo);
		part.createControls(container);
	}

}
