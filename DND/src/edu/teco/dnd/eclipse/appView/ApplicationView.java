package edu.teco.dnd.eclipse.appView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.server.ServerManager;

/**
 * The application view shows all running applications and allows to kill them.
 * 
 * @author Philipp Adolf
 */
public class ApplicationView extends ViewPart {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationView.class);

	private final UpdateButtonActivator updateButtonActivator = new UpdateButtonActivator();

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException {
		LOGGER.entry(site, memento);
		super.init(site, memento);

		ServerManager serverManager = Activator.getDefault().getServerManager();
		serverManager.addServerStateListener(updateButtonActivator);
		LOGGER.exit();
	}

	@Override
	public void dispose() {
		LOGGER.entry();
		super.dispose();

		Activator.getDefault().getServerManager().removeServerStateListener(updateButtonActivator);
		updateButtonActivator.setUpdateButton(null);
		LOGGER.exit();
	}

	@Override
	public void createPartControl(final Composite parent) {
		LOGGER.entry(parent);
		setLayout(parent);
		createUpdateButton(parent);
		createKillButton(parent);
		createApplicationTree(parent);
		LOGGER.exit();
	}

	private void setLayout(final Composite parent) {
		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 2;
		parent.setLayout(gridLayout);
		parent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true, true));
	}

	private void createUpdateButton(final Composite parent) {
		final Button button = createButton(parent, Messages.ApplicationView_UPDATE);
		updateButtonActivator.setUpdateButton(button);

		final ServerManager serverManager = Activator.getDefault().getServerManager();
		updateButtonActivator.setState(serverManager.isRunning());
	}

	private Button createKillButton(Composite parent) {
		final Button killButton = createButton(parent, Messages.ApplicationView_KILL_APPLICATION);
		killButton.setEnabled(false);
		return killButton;
	}

	private Button createButton(final Composite parent, final String text) {
		final Button button = new Button(parent, SWT.NONE);
		button.setText(text);
		return button;
	}

	private Tree createApplicationTree(final Composite parent) {
		final Tree applicationTree = new Tree(parent, SWT.SINGLE);

		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		applicationTree.setLayoutData(gridData);

		return applicationTree;
	}

	@Override
	public void setFocus() {
		// not needed by this view
	}
}
