/**
 * <p>
 * An EMF model for describing {@link edu.teco.dnd.module.Application}s. Each model is a single Application. Each
 * model/Application consists of {@link edu.teco.dnd.blocks.FunctionBlock}s (represented by
 * {@link edu.teco.dnd.graphiti.model.FunctionBlockModel}s) which have {@link edu.teco.dnd.blocks.Input}s (
 * {@link edu.teco.dnd.graphiti.model.InputModel}) and {@link edu.teco.dnd.blocks.Output}s (
 * {@link edu.teco.dnd.graphiti.model.OutputModel}) as well as the {@link edu.teco.dnd.graphiti.model.OptionModel
 * options} that can be set on each FunctionBlock.
 * </p>
 * 
 * <p>
 * The EMF model is generated from the Java code in this package. To regenerate it open
 * <code>model/graphiti.genmodel</code>, right-click on Graphiti and select Reload. Chose "Annotated Java". EMF should
 * scan the project and show this package. If this does not work, make sure that Eclipse is running with the same
 * JRE/JDK version as the project (which should be Java 6). Click on Finish to update the model.
 * </p>
 * 
 * <p>
 * Now the necessary code has to be generated. For this right-click on Graphiti again and select "Generate Model Code".
 * EMF will then automatically add the necessary methods.
 * </p>
 */
package edu.teco.dnd.graphiti.model;

