package com.vogella.tasks.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.vogella.tasks.model.Todo;
import com.vogella.tasks.model.TodoService;
import com.vogella.tasks.ui.wizards.TodoWizard;

public class NewTodoHandler {
    @Execute
    public void execute(Shell shell, TodoService todoService, IEventBroker broker) {
        // use -1 to indicate a not existing id
        Todo todo = new Todo(-1);
        WizardDialog dialog = new WizardDialog(shell, new TodoWizard(todo));
        if (dialog.open() == WizardDialog.OK) {
            todoService.saveTodo(todo);
        }
    }
}