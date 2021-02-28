# json-kotlin-schema-codegen

[![Build Status](https://travis-ci.org/pwall567/json-kotlin-schema-codegen.svg?branch=main)](https://travis-ci.org/pwall567/json-kotlin-schema-codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v1.4.0&color=blue&logo=kotlin)](https://github.com/JetBrains/kotlin/releases/tag/v1.4.0)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.json/json-kotlin-schema-codegen?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.json%22%20AND%20a:%22json-kotlin-schema-codegen%22)

Code generation for JSON Schema.

## Background

[JSON Schema](https://json-schema.org/) provides a means of describing JSON values &ndash; the properties of an object,
constraints on values etc. &ndash; in considerable detail.
Many APIs now use JSON Schema to specify the content of JSON parameters and response objects, and one way of ensuring
conformity to the specified schema is to generate code directly from the schema itself.

This is not always possible &ndash; some characteristics described by a schema may not be representable in the
implementation language.
But for a large subset of schema definitions a viable code representation is possible, and this library attempts to
provide conversions for the broadest possible range of JSON Schema definitions.

The library uses a template mechanism (employing [Mustache](https://github.com/pwall567/kotlin-mustache) templates), and
templates are provided to generate classes in Kotlin and Java.

## Quick Start

Simply create a `CodeGenerator`, supply it with details like destination directory and package name, and invoke the
generation process:
```kotlin
        val codeGenerator = CodeGenerator()
        codeGenerator.baseDirectoryName = "output/directory"
        codeGenerator.basePackageName = "com.example"
        codeGenerator.generate(File("/path/to/example.schema.json"))
```
The resulting code will be something like this (assuming the schema is the one referred to in the introduction to
[json-kotlin-schema](https://github.com/pwall567/json-kotlin-schema)):
```kotlin
package com.example

import java.math.BigDecimal

data class Test(
    /** Product identifier */
    val id: BigDecimal,
    /** Name of the product */
    val name: String,
    val price: BigDecimal,
    val tags: List<String>? = null,
    val stock: Stock? = null
) {

    init {
        require(price >= cg_dec0) { "price < minimum 0 - ${'$'}price" }
    }

    data class Stock(
        val warehouse: BigDecimal? = null,
        val retail: BigDecimal? = null
    )

    companion object {
        private val cg_dec0 = BigDecimal.ZERO
    }

}
```
Some points to note:
- the generated class is an immutable value object (in Java, getters are generated but not setters)
- validations in the JSON Schema become initialisation checks in Kotlin
- nested objects are converted to Kotlin nested classes (or Java static nested classes)
- fields of type `number` are implemented as `BigDecimal` (there is insufficient information in the schema in this case
to allow the field to be considered an `Int` or `Long`)
- non-required fields may be nullable and may be omitted from the constructor (the inclusion of `null` in the `type`
array will allow a field to be nullable, but not to be omitted from the constructor)
- a `description` will be converted to a KDoc comment in the generated code if available

## Multiple Files

The Code Generator can process a single file or multiple files in one invocation.
The `generate()` function takes a `vararg` parameter list, and each item may be a file or a directory; in the latter
case all files in the directory with filenames ending `.json` or `.yaml` (or `.yml`) will be processed.

It is preferable to process multiple files in this way because the code generator can create references to other classes
that it knows about &ndash; that is, classes generated in the same run.
For example, if a `properties` entry consists of a  `$ref` pointing to a schema that is in the list of files to be
generated, then a reference to an object of that type will be generated (instead of a nested class).

## Clean Code

It is important to note that the output code will be "clean" &ndash; that is, it will not contain annotations or other
references to external libraries.
I recommend the use of the [`json-kotlin`](https://github.com/pwall567/json-kotlin) library for JSON serialisation and
deserialisation, but the classes generated by this library should be capable of being processed by the library of your
choice.

There is one exception to this &ndash; classes containing properties subject to "format" validations will in some cases
(e.g. "email", "ipv4") cause references to the [`json-validation`](https://github.com/pwall567/json-validation) library
to be generated, and that library must be included in the build of the generated code.

## `additionalProperties`

The default in the JSON Schema specification for `additionalProperties` is `true`, meaning that any additional
properties in an object will be accepted without validation.
Many schema designers will be happy with this default, or will even explicitly specify `true`, so that future extensions
to the schema will not cause existing uses to have problems.

Unfortunately, there is no straightforward implementation for `additionalProperties` in code generation, so the setting
will be taken as `false` even if it is specified otherwise.
Most JSON deserialisation libraries have a means of specifying that additional properties are to be ignored; for
[`json-kotlin`](https://github.com/pwall567/json-kotlin) the `allowExtra` variable (`Boolean`) in `JSONConfig` must be
set to `true`.

## JSON Schema Version

This code generator targets the 2019-09 draft of the JSON Schema specification, but it should cover most of draft 07 as
well.

It also includes support for the `int32` and `int64` format types from the
[OpenAPI 3.0 Specification](https://swagger.io/specification/).

## API Reference

A `CodeGenerator` object is used to perform the generation.
It takes a number of parameters, many of which can be specified either as constructor parameters or by modifying
variables in the constructed instance.

### Parameters

- `templates` - the set of templates to use (the default is "kotlin" and the options are "kotlin", "java" or
"typescript" - typescript coverage is extremely rudimentary at this time)
- `suffix` - filename suffix to use on generated files (default ".kt")
- `templateName` - the primary template to use for the generation of a class
- `enumTemplateName` - the primary template to use for the generation of an enum
- `basePackageName` - the base package name for the generated classes (if directories are supplied to the `generate()`
function, the subdirectory names are used as sub-package names)
- `baseDirectoryName` - the base directory to use for generated output (in line with the Java convention, output
directory structure will follow the package structure)
- `derivePackageFromStructure` - a boolean flag (default `true`) to indicate that generated code for schema files in
subdirectories are to be output to sub-packages following the same structure
- `generatorComment` - a comment to add to the header of generated files

### Functions

#### `generate()`

There are two `generate()` functions, one taking a `List` of `File`s, the other taking a `vararg` list of `File`
arguments.
As described above, it is helpful to supply all the schema objects to be generated in a single operation.

#### `generateClass()`, `generateClasses()`

While the `generate()` functions take a file or files and convert them to an internal form before generating code, the
`generateClass()` and `generateClasses()` functions take pre-parsed schema objects.
This can be valuable in cases like a Swagger/OpenAPI file which contains a set of schema definitions embedded in another
file.

#### `generateAll()`

The `generateAll()` function allows the use of a composite file such as a Swagger or OpenAPI file containing several
schema definitions.
For example, a Swagger file will typically have a `definitions` section which consists of definitions of the objects
input to or output from the API.
Using the `generateAll()` function, the set of definitions can be selected (and optionally filtered) and the classes
generated for each of them.

## Dependency Specification

The latest version of the library is 0.25, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.json</groupId>
      <artifactId>json-kotlin-schema-codegen</artifactId>
      <version>0.25</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.json:json-kotlin-schema-codegen:0.25'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.json:json-kotlin-schema-codegen:0.25")
```

Peter Wall

2021-02-28
