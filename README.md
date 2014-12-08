# smart-cli-parser

smart-cli-parser is a powerful, flexible and easy-to-use command line parser for
Java.

# Building and development instructions.

Gradle version 2.1+ is needed for building the executable. Once you checkout
the code, navigate to the top folder and execute one of the following commands.

 * `gradle compileJava`: Creates all .class files corresponding to `src/`.
 * `gradle compileTests`: Creates all .class files corresponding to `tests/`.
 * `gradle jar`: Build a jar file in `bin/jar` folder.
 * `gradle test`: Runs all unit tests.
 * `gradle -Dtest.single=SomeTest test`: Runs a specific test.
 * `gradle javadoc`: Generates Javadoc html pages.
 * `gradle coverage`: Generates code coverage report.
