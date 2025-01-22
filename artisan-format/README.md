# Artisan Format

## What is `.ajex`

`.ajex` file extension or **Artisan Java Extension** is a simple file extension for
defining Java bytecode transformations. The goal of this format is to be a couple things:
- precise to write
- easy to write and parse

The `.ajex` format achieves this via its easy syntax and extensibility. Below is an
example of the `.ajex` format for the, bundled in another module, Access Transformers
```ajex
~AT
public sh/miles/test/class/path/MyCustomClass getCustomMethod(Ljava/lang/String;)V
~AT
```

While at first seemingly more verbose than the standard `.at` format used by
other java projects. `.ajex` allows the definition of more "functions/extensions"
within a single file. Below is a made up example of method generation with the tag `~CUSTOM`

```ajex
~AT
public sh/miles/test/class/path/MyCustomClass getCustomMethod(Ljava/lang/String;)V
~AT
~CUSTOM
sh/miles/test/class/path/MyCustomClass sh/miles/apply/class/path/MyCustomClassAPIImpl
~CUSTOM
```
As you see the `.ajex` format provides the extensibility to further allow different forms
of bytecode generation through "extensions" you can read more about these extensions in
the `artisan-extensions` module.

## What is the `ajex-format` Module?

The `ajex-format` module supplies code to tokenize and sort valid `.ajex` files into both
tokens and into an Abstract Syntax Tree, which can then be later parsed.

### Tokenization

`.ajex` files have only a few tokens:
- `Open` -> defines an opening tag
- `Close` -> closing tag
- `Meta` -> represents a block of metadata
- `Entry` -> a line within an Open and Close tag
- `Comment` -> a comment that is tokenized.

These tokens make up a basis of the `.ajex` format.

### Syntax Tree
The `.ajex` syntax tree is simple and only consists of 3 node types
- `ContainerNode` -> Contains `Literal` tokens or the special case `MetadataContainer`
- `LiteralNode` -> Contains some `literal` text that can be handled further later
- `MetadataNode` -> contains some metadata that can be put in a `MetadataContainer` type of 
`ContainerNode`, or, a `LiteralNode`. This contains other information that could be useful
during further parsing.

## Definition of .ajex

This module relates to parsing tools used for the Artisan Java Extension format or
(.ajex). This module defines a basic syntax tree that can be used surrounding an API
relating to that API.

The `.ajex` format is as follows. It should be generally followed that any other lexer,
and parser should settle on these standards for `.ajex`

### Simple Ajex Example

```ajex
@Version: 1.0.0
@LastUpdated: 2025-01-18
@Author: Y2Kwastaken
@Project: Artisan Java Extension Test

~AT
@Inheritable: All
public sh/miles/test/class/path/MyCustomClass getCustomMethod(Ljava/lang/String;)V
~AT

~CUSTOM
sh/miles/test/class/path/MyCustomClass /sh/miles/test/class/path/MyApiClass
~CUSTOM
```

### Syntactical Characters

- `@` A metadata character which defines a key value pair
- `:` separates key and value pair within metadata
- `~` starts a function start and end declaration
- `#` Defines a single comment (neither inline nor multiline comments are supported)

### The Metadata `@` definition

The `@` character defines the start of a metadata key value pair. The following rules
govern how the format parses the metadata and what entries are allowed

- Metadata is parsed starting immediately after the character is defined e.g. in the
scenario `@Hello: World` the metadata is parsed as `Hello, World` where `Hello` is the key
and `World` is the value. Likewise a metadata definition of `@ Hello: World` is parsed into the
key ` Hello` and value `World` as seen the space is **included** into the key. This is because the
capture starts as soon as the `@` is defined.
- Metadata keys and values must not include `:` it is forbidden. There is no escape character.
- While metadata keys can contain spaces it is generally more accepted to use `_`, `-`, or any
other casing scheme.
- Metadata can be applied to `literals` inside of function blocks. As well as at the beginning
of a file, however, Metadata can NOT be applied to function blocks.

### The Comment `#` definition
- Comments can be included anywhere and are ignored
- Comments can contain spaces
- Comments are not multiline nor can they be inlined. e.g. `# This is okay` `~AT # This is not okay`

### The Function `~` definition
- Within the `.ajex` format there are no traditional "functions" as you'd expect. Instead
these functions are a container for `literals`. These `literals` can be handled by the "function provider".
These blocks are called "functions" because they are innately tied to some applicator function defined by
any provider who has implemented said function.
- Function definitions must begin and end with their "signature". E.g. as seen above the `AT` definition
must both start and end with `~AT` here is an example excerpt:
```ajex
~AT
public sh/miles/test/class/path/MyCustomClass getCustomMethod(Ljava/lang/String;)V
~AT
```
- All content with the `~AT` block is handled line by line and collected into `literals`.
These literals can be handled by said "function provider" to provide functionality.
