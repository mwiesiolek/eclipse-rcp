package com.vogella.tasks.ui.handlers;

import java.util.Date;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.vogella.tasks.model.Todo;
import com.vogella.tasks.model.TodoService;
import com.vogella.tasks.ui.wizards.TodoWizard;

public class NewTodoHandler {
	@Execute
	public void execute(Shell shell, TodoService todoService) {
        // use -1 to indicate a not existing id
        Todo todo = new Todo(-1);
        todo.setDueDate(new Date());
        WizardDialog dialog = new WizardDialog(shell, new TodoWizard(todo));
        if (dialog.open() == WizardDialog.OK) {
            // call service to save Todo object
            todoService.saveTodo(todo);
        }
	}
}
