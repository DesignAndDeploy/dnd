Design and Deploy
=================
Design and Deploy (or DND for short) is a framework for developing applications running on distributed machines. It simplifies the development by managing all the work needed to make network communication work.

Current status
--------------
DND still has a lot of rough edges, however if you can live with a hickup now and then you should have a good experience using it. Note that things may change in ways that’ll break code you’ll write now. But as function blocks are rather simple adjusting them shouldn’t be hard.

To run the graph editor you'll need Eclipse 3.7 or newer. The Eclipse plugins EMF and Graphiti (Incubation) are needed. They can be installed from Eclipse package manager using the update site for your Eclipse version. Right now we don't have a binary version, so you'll have to import the project into Eclipse and choose Run As > Eclipse Application.

How does it work?
-----------------
Your application logic is split into small pieces of code that perfom a single function. We call those function blocks. They are run by modules (instances of our software running on the hardware providing sensors and actors) and can communicate via inputs and outputs.

### Creating FunctionBlocks
The basic steps to create a FunctionBlock are:

1. extend [FunctionBlock](DND/src/edu/teco/dnd/blocks/FunctionBlock.java)
2. annotate attributes (which can even be private) with [Input](DND/src/edu/teco/dnd/blocks/Input.java) to define inputs
3. use [Output](DND/src/edu/teco/dnd/blocks/Output.java) to define outputs
4. overwrite update() with your code that processes input and sets the output

Have a look at [temperature](DND/src/edu/teco/dnd/temperature) and [meeting](DND/src/edu/teco/dnd/meeting) for some examples.

### Graph editor
The graph editor can be accessed by using Eclipse's New dialogue and choosing Graphiti Diagram. As diagram type choose dataflowgraph. The editor will automatically show your FunctionBlocks as well as the ones that come with our plugin. You can drag them form the right into the graph and connect the inputs and outputs (represented by circles) with the "add connection" feature as long as the data types are compatible.

### Deploying an application
Later it will be possible to take a dataflow graph and have our framework distribute it to modules to have it actually run. This will be done from Eclipse with autodetection of running modules. Our framework will assign FunctionBlocks to modules that are able to run them and optimize for different properties. While the application is running our framework will also handle the passing of values from outputs to the connected inputs.
