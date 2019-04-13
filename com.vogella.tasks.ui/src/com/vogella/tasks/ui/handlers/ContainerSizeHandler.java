package com.vogella.tasks.ui.handlers;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ContainerSizeHandler {
	@Execute
	public void execute(MWindow window, EModelService modelService) {
        List<MPartSashContainer> parts = modelService.findElements(window, "com.vogella.tasks.ui.partsashcontainer.right", MPartSashContainer.class, null);
        if(parts.isEmpty()) {
        	return;
        }
        
        MPartSashContainer part = parts.get(0);
        part.setContainerData("70");
	}
}
