# Artisan Extensions

This module is built on the format specified at [artisan-format](../artisan-format/README.md). See the artisan-format
module for more information regarding the actual format. This will cover basic extension usage.

## Rational

The rational behind the design of the extensions module is to be versatile and fairly small in size.
This module relies upon the asm and asm-tree libraries to make this module as simple to use as possible.
While the asm-tree format is fairly slower than the asm visitor format I decided to go with asm-tree due to
its simplicity when writing extensions that do not communicate their intentions with each-other.

## Creating an Extension Overview

ArtisanExtension's themselves are fairly simplistic and only have a few fields. Firstly the name, version
and then finally a buildHandlers() method. You can see an example at the
builtin [ArtisanAccessTransformationExtension](src/main/java/sh/miles/artisan/extension/builtin/ArtisanAccessTransformationExtension.java).

More interestingly in this section we will briefly focus on the ContainerHandler, which is built within buildHandlers().
ContainerHandler's handle parsing of a "function container" and handle visiting classes. Here is a basic example below

```java
public void ExampleContainerHandler implements ContainerHandler {

    /*
    * this parses from the ajex file function content e.g. in this case
    * ~EXAMPLE
    * sh/miles/artisan/MyClass
    * ~EXAMPLE
    * would pass a literal container the "sh/miles/artisan/MyClass" line.
    * It is up to you the implementer to store this result and handle it for later!
    */
    @Override
    public void parse(LiteralResult literal, ArtisanLogger logger) {
    
    }

    /*
    * this method will pass in a node of the class being editor.
    * All of your bytecode edits will happen here. Its a good idea to use the logger!
    */
    @Override
    public void visit(ClassNode node, JvmClasspath path, ArtisanLogger logger) {
    
    }
    
    /*
    * This method defines the "function" name to parse from in the .ajex file
    * e.g. 
    * ~EXAMPLE
    * sh/miles/artisan/MyClass
    * ~EXAMPLE
    */
    @Override
    public String containerName() {
        return "EXAMPLE"
    }
}
```
