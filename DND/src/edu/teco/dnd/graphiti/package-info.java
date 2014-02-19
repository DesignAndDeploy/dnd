/**
 * <p>
 * A Graphiti graph editor used for building {@link edu.teco.dnd.module.Application}s from
 * {@link edu.teco.dnd.blocks.FunctionBlock}s. The user can add FunctionBlocks which are displayed as rectangles to the
 * graph. The FunctionBlocks show circles corresponding with their {@link edu.teco.dnd.blocks.Input}s and
 * {@link edu.teco.dnd.blocks.Output}s. Those can be connected with each other so that the values from the Output will
 * be sent to the matching Input once the Application is running.
 * </p>
 * 
 * <p>
 * Graphiti editors internally use an EMF model to represent the graph. For this editor, the model used is the
 * {@link edu.teco.dnd.graphiti.model.FunctionBlockModel}. On saving the editor will store the model in a file with the
 * same base name as the graph, but with the extension <code>.blocks</code> which can then be opened with the
 * {@link edu.teco.dnd.eclipse.deployEditor.DeployEditor}.
 * </p>
 * 
 * <p>
 * The main classes for the editor are the {@link edu.teco.dnd.graphiti.DiagramTypeProvider} and the
 * {@link edu.teco.dnd.graphiti.FeatureProvider}. The plugin also does some static analysis of the classes in the
 * current Eclipse project to gather the necessary information about the FunctionBlocks. This is done by the
 * FeatureProvider using the {@link edu.teco.dnd.graphiti.CreateFeatureFactory}.
 * </p>
 */
package edu.teco.dnd.graphiti;

