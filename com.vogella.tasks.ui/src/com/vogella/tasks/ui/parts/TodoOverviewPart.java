package com.vogella.tasks.ui.parts;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.vogella.tasks.model.TodoService;
import com.vogella.tasks.events.MyEvent;
import com.vogella.tasks.model.Todo;

public class TodoOverviewPart {

    @Inject
    private TodoService todoService;

    @Inject 
    private ESelectionService service;
    
    @Inject
    private MApplication application;
    
    @Inject
    private EModelService eModelService;
    
    private Button btnLoadData;
    private TableViewer viewer;

    private String searchString = "";
    private WritableList<Todo> writableList;

    @PostConstruct
    public void createControls(Composite parent, EMenuService menuService, Shell shell) {
        parent.setLayout(new GridLayout(1, false));

        btnLoadData = new Button(parent, SWT.PUSH);
        btnLoadData.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // pass in updateViewer method as Consumer
                todoService.getTodos(TodoOverviewPart.this::updateViewer);
            };
        });
        btnLoadData.setText("Load Data");

        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        Table table = viewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);

        column.getColumn().setWidth(100);
        column.getColumn().setText("Summary");
        column = new TableViewerColumn(viewer, SWT.NONE);

        column.getColumn().setWidth(100);
        column.getColumn().setText("Description");

        // after the viewer is instantiated
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
	            IStructuredSelection selection =  viewer.getStructuredSelection();
	            service.setSelection(selection.toList());
	            
	            List<MPart> parts = eModelService.findElements(application, null, MPart.class, null);
	            boolean isDirty = parts.stream().anyMatch(p -> p.isDirty());
	            if(isDirty) {
	            	boolean result = MessageDialog.openConfirm(shell, "Confirm", "Some unsaved changes have been detected. Do you want to discard them?");
	            	if(result) {
	            		parts.forEach(p -> p.setDirty(false));
	            	}
	            }
            }
        });
        
        searchStuff(parent, menuService);

        // use data binding to bind the viewer
        writableList = new WritableList<>();
        // fill the writable list, when Consumer callback is called. Databinding
        // will do the rest once the list is filled
        todoService.getTodos(writableList::addAll);
        ViewerSupport.bind(viewer, writableList,
                BeanProperties.values(new String[] { Todo.FIELD_SUMMARY, Todo.FIELD_DESCRIPTION }));

    }

    public void updateViewer(List<Todo> list) {
        if (viewer != null) {
            writableList.clear();
            writableList.addAll(list);
        }
    }

    @Focus
    private void setFocus() {
        btnLoadData.setFocus();
    }

    public void searchStuff(Composite parent, EMenuService menuService) {
        viewer.getTable().addKeyListener(KeyListener.keyPressedAdapter(e -> {
            if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
                viewer.setSelection(new StructuredSelection((List) viewer.getInput()));
            }
        }));

        Text search = new Text(parent, SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);

        // assuming that GridLayout is used
        search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        search.setMessage("Filter");

        // filter at every keystroke
        search.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Text source = (Text) e.getSource();
                searchString = source.getText();
                // trigger update in the viewer
                viewer.refresh();
            }
        });

        // SWT.SEARCH | SWT.CANCEL is not supported under Windows7 and
        // so the following SelectionListener will not work under Windows7
        search.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                if (e.detail == SWT.CANCEL) {
                    Text text = (Text) e.getSource();
                    text.setText("");
                    //
                }
            }
        });

        // add a filter which will search in the summary and description field
        viewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                Todo todo = (Todo) element;
                return todo.getSummary().contains(searchString) || todo.getDescription().contains(searchString);
            }
        });

        // sort according to due date, needs to adjusted for the exercise
        viewer.setComparator(new ViewerComparator() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                Todo t1 = (Todo) e1;
                Todo t2 = (Todo) e2;
                return t1.getSummary().compareTo(t2.getSummary());
            };
        });

        Label lblPleaseEnterA = new Label(parent, SWT.NONE);
        lblPleaseEnterA.setText("Please enter a value:");

        Text text = new Text(parent, SWT.BORDER);
        GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_text.horizontalIndent = 8;
        text.setLayoutData(gd_text);
        GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);

        // create the decoration for the text component
        // using an predefined image
        final ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
                .getImage();
        // set description and image
        deco.setDescriptionText("Use CTRL + SPACE to see possible values");
        deco.setImage(image);
        // always show decoration
        deco.setShowOnlyOnFocus(false);

        // hide the decoration if the text widget has content
        text.addModifyListener(e -> {
            Text source = (Text) e.getSource();
            if (!source.getText().isEmpty()) {
                deco.hide();
            } else {
                deco.show();
            }
        });

        // Define field assists for the text widget
        // use "." and " " activate the content proposals
        char[] autoActivationCharacters = new char[] { '.', ' ' };
        KeyStroke keyStroke;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
            new ContentProposalAdapter(text, new TextContentAdapter(),
                    new SimpleContentProposalProvider(new String[] { "ProposalOne", "ProposalTwo", "ProposalThree" }),
                    keyStroke, autoActivationCharacters);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        // register context menu on the table
        menuService.registerContextMenu(viewer.getControl(), "com.vogella.tasks.ui.popupmenu.table");
    }
    
    @Inject
    @Optional
    private void subscribeTopicTodoAllTopics
        (@UIEventTopic(MyEvent.TOPIC_TODO_ALLTOPICS) Map<String, String> event) {
        if (viewer != null) {
            // code if you use databinding for your viewer
            writableList.clear();
            todoService.getTodos(writableList::addAll);
            // if you do not use databinding, use the following snippet:
            // todoService.getTodos(viewer::setInput);
        }
    }
}
