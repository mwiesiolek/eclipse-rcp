package com.vogella.tasks.ui.ownannotation.internal;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.service.component.annotations.Component;

import com.vogella.tasks.model.Todo;
import com.vogella.tasks.model.TodoService;
import com.vogella.tasks.ui.ownannotation.DirectTodo;

@Component(service = ExtendedObjectSupplier.class, property = "dependency.injection.annotation=com.vogella.tasks.ui.ownannotation.DirectTodo")
public class DirectTodoObjectSupplier extends ExtendedObjectSupplier {

	@Inject
	private TodoService todoService;

	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		// get the DirectTodo from the IObjectDescriptor
		DirectTodo uniqueTodo = descriptor.getQualifier(DirectTodo.class);

		// get the id from the DirectTodo (default is 0)
		long id = uniqueTodo.id();

		// get the Todo, which has the given id or null
		Optional<Todo> todo = todoService.getTodo(id);

		return todo;
	}
}
