package com.vogella.tasks.ui.lifecycle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.vogella.tasks.ui.dialog.PasswordDialog;

public class Manager {

	private String user;

	@PostContextCreate
	public void postContextCreate(IApplicationContext appContext, Display display, IEclipseContext context) throws IllegalStateException, IOException {

		final Shell shell = new Shell(SWT.SHELL_TRIM);
		PasswordDialog dialog = new PasswordDialog(shell);
		if (user != null) {
			dialog.setUser(user);
		}

		// close the static splash screen
		appContext.applicationRunning();

		// position the shell
		setLocation(display, shell);

		String cssURI = "platform:/plugin/com.vogella.eclipse.css/css/rainbow.css";
		context.set(E4Workbench.CSS_URI_ARG, cssURI);
		PartRenderingEngine.initializeStyling(shell.getDisplay(), context);
		// open the dialog
		if (dialog.open() != Window.OK) {
			// close the application
			System.exit(-1);
		} else {
			// get the user from the dialog
			String userName = dialog.getUser();
		    if (!Platform.getInstanceLocation().isSet()) {
		        String defaultPath = System.getProperty("user.home");
		        String path = defaultPath + "/" + userName + "/workspace/";
		        URL instanceLocationUrl = new URL("file", null, path);
		        Platform.getInstanceLocation().set(instanceLocationUrl, false);
		    }
		}

	}

	private void setLocation(Display display, Shell shell) {
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle monitorRect = monitor.getBounds();
		Rectangle shellRect = shell.getBounds();
		int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
		int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}
}
