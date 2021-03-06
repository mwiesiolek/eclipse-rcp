package com.vogella.tasks.ui.handlers;

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.vogella.tasks.model.TodoService;
import com.vogella.tasks.model.Todo;

public class RemoveTodoHandler {
    @Execute
    public void execute(TodoService todoService,
            @Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Todo> todos,
            @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        if (todos != null) {
            todos.forEach(t -> todoService.deleteTodo(t.getId()));
        } else {
            MessageDialog.openInformation(shell, "Deletion not possible", "No todo selected");
        }
    }
    
    @CanExecute
    public boolean isAllowed(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Todo> todos) {
    	if(todos != null && !todos.isEmpty()) {
    		return true;
    	}
    	
    	return false;
    }
}