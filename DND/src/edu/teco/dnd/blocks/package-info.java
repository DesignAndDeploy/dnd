/**
 * Definition of FunctionBlocks as well as ways of inspecting them.
 * 
 * A FunctionBlock is a piece of code that can be executed on a Module. It is considered atomic, that is contrary to
 * Applications, a single FunctionBlock will never run on multiple Modules.
 * 
 * To define a FunctionBlock, add the DND classes to your project’s class path and extend
 * {@link edu.teco.dnd.blocks.FunctionBlock}. For inspecting FunctionBlocks statically, load them via
 * {@link edu.teco.dnd.blocks.FunctionBlockClassFactory}.
 */
package edu.teco.dnd.blocks;

