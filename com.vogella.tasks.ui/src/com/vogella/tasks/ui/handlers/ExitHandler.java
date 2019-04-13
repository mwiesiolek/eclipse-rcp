package com.vogella.tasks.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ExitHandler {
	@Execute
	public void execute(IWorkbench workbench, Shell shell, EPartService partService) {
		if(!partService.getDirtyParts().isEmpty()) {
			boolean result = MessageDialog.openConfirm(shell, "Config", "Unsaved changes have been detected. Do you want to discard them?");
			if(result) {
				workbench.close();
				return;
			}
		}
		
        boolean result = MessageDialog.openConfirm(shell, "Close", "Close application?");
        if (result) {
            workbench.close();
        }
	}
}
