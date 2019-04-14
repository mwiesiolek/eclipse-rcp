package com.vogella.tasks.ui.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.vogella.tasks.model.Todo;
import com.vogella.tasks.model.TodoService;

public class EditorPart {

    private Text txtSummary;
    private Text txtDescription;
    private Button btnDone;
    private DateTime dateTime;

    // define an initially empty todo as field
    private java.util.Optional<Todo> todo = java.util.Optional.empty();

    // observable placeholder for a todo
    private WritableValue<Todo> observableTodo = new WritableValue<>();

    private DataBindingContext dbc;

    @Inject
    private MPart part;
    
    @Inject
    private TodoService service;
    
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;
    
    // pause dirty listener when new Todo selection is set
    private boolean pauseDirtyListener;
    
    public EditorPart() {

    }
    
    public EditorPart(Todo todo) {
    	this.todo = java.util.Optional.ofNullable(todo);
    }
    
    @PostConstruct
    public void createControls(Composite parent) {

    	String currentId = part.getPersistedState().get(Todo.FIELD_ID);
    	this.todo = service.getTodo(Long.valueOf(currentId));
    	
        Label lblSummary = new Label(parent, SWT.NONE);
        lblSummary.setText("Summary");

        txtSummary = new Text(parent, SWT.BORDER);

        Label lblDescription = new Label(parent, SWT.NONE);
        lblDescription.setText("Description");

        txtDescription = new Text(parent, SWT.BORDER | SWT.MULTI);

        Label lblDueDate = new Label(parent, SWT.NONE);
        lblDueDate.setText("Due Date");

        dateTime = new DateTime(parent, SWT.BORDER);
        new Label(parent, SWT.NONE);

        btnDone = new Button(parent, SWT.CHECK);
        btnDone.setText("Done");

        bindData();

        updateUserInterface(todo);

        GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(parent);
        
        // at the end of the creation of the UI
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Save");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ParameterizedCommand command = commandService.createCommand("org.eclipse.ui.file.saveAll", null);
                handlerService.executeHandler(command);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void bindData() {
        // this assumes that widget field is called "summary"
        if (txtSummary != null && !txtSummary.isDisposed()) {

            dbc = new DataBindingContext();

            Map<String, IObservableValue<?>> fields = new HashMap<>();
            fields.put(Todo.FIELD_SUMMARY, WidgetProperties.text(SWT.Modify).observe(txtSummary));
            fields.put(Todo.FIELD_DESCRIPTION, WidgetProperties.text(SWT.Modify).observe(txtDescription));
            fields.put(Todo.FIELD_DUEDATE, WidgetProperties.selection().observe(dateTime));
            fields.put(Todo.FIELD_DONE, WidgetProperties.selection().observe(btnDone));
            fields.forEach((k, v) -> dbc.bindValue(v, BeanProperties.value(k).observeDetail(observableTodo)));
            
            // set a dirty state if one of the bindings is changed
            dbc.getBindings().forEach(item -> {
                Binding binding = (Binding) item;
                binding.getTarget().addChangeListener(e -> {
                    if(!pauseDirtyListener && part != null) {  // 
                        part.setDirty(true);
                    }
                });
            });
        }
    }

    // allows to disable/ enable the user interface fields
    // if no todo is set
    private void enableUserInterface(boolean enabled) {
        if (txtSummary != null && !txtSummary.isDisposed()) {
            txtSummary.setEnabled(enabled);
            txtDescription.setEnabled(enabled);
            dateTime.setEnabled(enabled);
            btnDone.setEnabled(enabled);
        }
    }

    private void updateUserInterface(java.util.Optional<Todo> todo) {

        // check if Todo is present
        if (todo.isPresent()) {
            enableUserInterface(true);
        } else {
            enableUserInterface(false);
            return;
        }

        // Check if the user interface is available
        // assume you have a field called "summary"
        // for a widget
        if (txtSummary != null && !txtSummary.isDisposed()) {
            pauseDirtyListener = true; // 
            this.observableTodo.setValue(todo.get());
            pauseDirtyListener = false; // 
        }
    }

    @Focus
    public void onFocus() {
        txtSummary.setFocus();
    }

    @PreDestroy
    public void dispose() {
        dbc.dispose();
    }
    
    @Persist 
    public void save(TodoService todoService) {
        todo.ifPresent(t -> {
            todoService.saveTodo(t);
        });
        if (part!=null) {
            part.setDirty(false);
        }
    }
}
