/*******************************************************************************
 * Copyright (c) 2016, 2018 Red Hat.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.eclipse.linuxtools.internal.docker.ui.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.linuxtools.docker.core.DockerConnectionManager;
import org.eclipse.linuxtools.docker.core.IDockerConnection;
import org.eclipse.linuxtools.docker.core.IDockerImage;
import org.eclipse.linuxtools.docker.core.IDockerImageHierarchyNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class DockerImageHierarchyView extends CommonNavigator
		implements ITabbedPropertySheetPageContributor {

	/** the id of the view as defined in {@code plugin.xml}. */
	public static final String VIEW_ID = "org.eclipse.linuxtools.docker.ui.dockerImageHierarchyView"; //$NON-NLS-1$

	private Control hierarchyPane;
	private Control explanationsPane;
	private PageBook pageBook;

	private IDockerImageHierarchyNode selectedImageHierarchy = null;

	private Control currentPane;

	private IDockerConnection connection;

	@Override
	protected Object getInitialInput() {
		return this.selectedImageHierarchy;
	}

	/**
	 * Shows the given resolved hierarchy associated with the selected
	 * {@link IDockerImage}.
	 * 
	 * @param selectedImageHierarchy
	 *            the hierarchy to display in this view
	 * @param selectedElement
	 *            the element to select in the view
	 */
	public void show(final IDockerImageHierarchyNode selectedImageHierarchy) {
		this.selectedImageHierarchy = selectedImageHierarchy;
		if (this.selectedImageHierarchy != null) {
			this.getCommonViewer().setInput(new DockerImageHiearchy(
					this.selectedImageHierarchy.getRoot()));
			if (this.getCommonViewer() == null)
				System.out.println("null viewer");
			this.getCommonViewer().expandAll();
			this.getCommonViewer().setSelection(
					new StructuredSelection(selectedImageHierarchy));
		}
		showHierarchyOrExplanations();
	}

	@Override
	public void createPartControl(final Composite parent) {
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		this.pageBook = new PageBook(parent, SWT.NONE);
		this.hierarchyPane = createHierarchyPane(pageBook, toolkit);
		this.explanationsPane = createExplanationPane(pageBook, toolkit);
		showHierarchyOrExplanations();
	}

	private Control createExplanationPane(final PageBook pageBook,
			final FormToolkit toolkit) {
		final Form form = toolkit.createForm(pageBook);
		final Composite container = form.getBody();
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(container);
		final Label label = new Label(container, SWT.WRAP);
		label.setText(
				DVMessages.getString("DockerHierarchyViewNoImageSelected.msg")); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL)
				.grab(true, false).applyTo(label);
		return form;
	}

	private Control createHierarchyPane(final PageBook pageBook,
			final FormToolkit toolkit) {
		final Form form = toolkit.createForm(pageBook);
		final Composite container = form.getBody();
		GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5)
				.applyTo(container);
		super.createPartControl(container);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
				.grab(true, true).applyTo(getCommonViewer().getControl());
		return form;
	}

	@Override
	protected CommonViewer createCommonViewer(final Composite parent) {
		final CommonViewer viewer = super.createCommonViewer(parent);
		setLinkingEnabled(false);
		return viewer;
	}

	@Override
	public String getContributorId() {
		return "org.eclipse.linuxtools.docker.ui.propertiesViewContributor"; //$NON-NLS-1$
		// return getSite().getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		if (IPropertySheetPage.class.isAssignableFrom(adapter)) {
			return (T) new TabbedPropertySheetPage(this, true);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void dispose() {

	}

	/**
	 * Shows the {@link DockerExplorerView#explanationsPane} or the
	 * {@link DockerExplorerView#hierarchyPane} depending on the number of
	 * connections in the {@link DockerConnectionManager}.
	 */
	public void showHierarchyOrExplanations() {
		if (this.selectedImageHierarchy == null) {
			this.currentPane = explanationsPane;
			pageBook.showPage(explanationsPane);
		} else {
			this.currentPane = hierarchyPane;
			pageBook.showPage(hierarchyPane);
		}
	}

	/**
	 * @return <code>true</code> if the current panel is the one containing a
	 *         {@link TreeViewer} of {@link IDockerConnection}s,
	 *         <code>false</code> otherwise.
	 */
	public boolean isShowingConnectionsPane() {
		return this.currentPane == hierarchyPane;
	}

	static class DockerImageHiearchy {

		private final IDockerImageHierarchyNode root;

		public DockerImageHiearchy(final IDockerImageHierarchyNode root) {
			this.root = root;
		}

		public IDockerImageHierarchyNode getRoot() {
			return this.root;
		}
	}

	/**
	 * @return the {@link IDockerConnection} used to display the current
	 *         hierarchy.
	 */
	public IDockerConnection getConnection() {
		return connection;
	}

	public void setConnection(IDockerConnection connection) {
		this.connection = connection;
	}

}
