package com.vogella.tasks.ui.addons;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;

//@PostConstruct does not work as the workbench gets
//instantiated after the processing of the add-ons
//hence this approach uses method injection

public class WindowCloseListenerAddon {

	@SuppressWarnings("restriction")
	@Inject
	@Optional
	private void adjustWindowCloseHandler(
			@UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) MApplication application, IWorkbench workbench,
			ECommandService commandService, EHandlerService handlerService) {
		MWindow mainWindow = findMainWindow(application);

		mainWindow.getContext().set(IWindowCloseHandler.class, window -> {
			if (window.getTags().contains("TopLevel")) {
				ParameterizedCommand cmd = commandService.createCommand("org.eclipse.ui.file.exit", null);
				handlerService.executeHandler(cmd, mainWindow.getContext());
				return true;
			}
			return true;
		});
	}

	private static MWindow findMainWindow(MApplication application) {
		// instead of using the index you could also use a tag on the MWindow to
		// mark the main window
		return application.getChildren().get(0);
	}
}
