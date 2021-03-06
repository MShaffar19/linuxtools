/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Andrew Ferguson (Symbian)
 *     Markus Schorn (Wind River Systems)
 *     Anton Leherbauer (Wind River Systems)
 *******************************************************************************/
package org.eclipse.linuxtools.cdt.libhover.tests;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigExtensionReference;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.internal.core.pdom.indexer.IndexerPreferences;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Helper methods to set up a ICProject.
 */
@SuppressWarnings("restriction")
public class CProjectHelper {

    public final static String PLUGIN_ID = "org.eclipse.linuxtools.cdt.libhover.tests"; //$NON-NLS-1$

    /**
     * Creates a ICProject.
     */
    private static ICProject createCProject(final String projectName, String binFolderName, final String indexerID) throws CoreException {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final ICProject newProject[] = new ICProject[1];
        ws.run((IWorkspaceRunnable) monitor -> {
		    IWorkspaceRoot root = ws.getRoot();
		    IProject project = root.getProject(projectName);
		    if (indexerID != null) {
		        IndexerPreferences.set(project, IndexerPreferences.KEY_INDEX_UNUSED_HEADERS_WITH_DEFAULT_LANG, "true");
		        IndexerPreferences.set(project, IndexerPreferences.KEY_INDEXER_ID, indexerID);
		    }
		    if (!project.exists()) {
		        project.create(null);
		    } else {
		        project.refreshLocal(IResource.DEPTH_INFINITE, null);
		    }
		    if (!project.isOpen()) {
		        project.open(null);
		    }
		    if (!project.hasNature(CProjectNature.C_NATURE_ID)) {
		        String projectId = PLUGIN_ID + ".TestProject";
		        addNatureToProject(project, CProjectNature.C_NATURE_ID, null);
		        CCorePlugin.getDefault().mapCProjectOwner(project, projectId, false);
		    }
		    addDefaultBinaryParser(project);
		    newProject[0] = CCorePlugin.getDefault().getCoreModel().create(project);
		}, null);

        return newProject[0];
    }

    /**
     * Add the default binary parser if no binary parser configured.
     *
     * @param project
     * @throws CoreException
     */
    private static boolean addDefaultBinaryParser(IProject project) throws CoreException {
        ICConfigExtensionReference[] binaryParsers= CCorePlugin.getDefault().getDefaultBinaryParserExtensions(project);
        if (binaryParsers == null || binaryParsers.length == 0) {
            ICProjectDescription desc= CCorePlugin.getDefault().getProjectDescription(project);
            if (desc == null) {
                return false;
            }

            desc.getDefaultSettingConfiguration().create(CCorePlugin.BINARY_PARSER_UNIQ_ID, CCorePlugin.DEFAULT_BINARY_PARSER_UNIQ_ID);
            CCorePlugin.getDefault().setProjectDescription(project, desc);
        }
        return true;
    }

    public static ICProject createCCProject(final String projectName, final String binFolderName, final String indexerID) throws CoreException {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final ICProject newProject[] = new ICProject[1];
        ws.run((IWorkspaceRunnable) monitor -> {
		    ICProject cproject = createCProject(projectName, binFolderName, indexerID);
		    if (!cproject.getProject().hasNature(CCProjectNature.CC_NATURE_ID)) {
		        addNatureToProject(cproject.getProject(), CCProjectNature.CC_NATURE_ID, null);
		    }
		    newProject[0] = cproject;
		}, null);
        return newProject[0];
    }

    private static void addNatureToProject(IProject proj, String natureId, IProgressMonitor monitor) throws CoreException {
        IProjectDescription description = proj.getDescription();
        String[] prevNatures = description.getNatureIds();
        String[] newNatures = new String[prevNatures.length + 1];
        System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
        newNatures[prevNatures.length] = natureId;
        description.setNatureIds(newNatures);
        proj.setDescription(description, monitor);
    }
}