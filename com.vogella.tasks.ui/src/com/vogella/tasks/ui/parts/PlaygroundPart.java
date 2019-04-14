package com.vogella.tasks.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.vogella.service.imageloader.BundleResourceLoader;
import com.vogella.tasks.model.Todo;
import com.vogella.tasks.ui.ownannotation.DirectTodo;

public class PlaygroundPart {
	
	public PlaygroundPart() {
		System.out.println("PlaygroundPart");
	}

	@PostConstruct
	public void createControls(Composite parent, BundleResourceLoader loader) {
        Label label = new Label(parent, SWT.NONE);

        // the following code assumes that you have a vogella.png file
        // in a folder called "images" in this plug-in
        ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), label);
        Image image = resourceManager.createImage(loader.getImageDescriptor(this.getClass(), "images/vogella.png"));
        label.setImage(image);
	}
	
	@Inject
	public void setTodo(@DirectTodo(id=1) java.util.Optional<Todo> todo) {
	    // Printing the received t to the console
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	    todo.ifPresent(t-> System.out.println(t.getSummary()));
	}
}
