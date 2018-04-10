# Installation (Windows)
- Use IntelliJ IDE.
- Install [Git for Windows](https://git-scm.com/download/win).
- Open git shell and clone the repository:
```
git clone https://github.com/TechnionTDK/spanthera.git
```
- Open the project using IntelliJ.
 
 Validate your installation:
 - You should have no compilation errors of course.
 - Execute the test *TestJbsManipulations*. All tests should pass.

# Installation (Linux)
- Clone the repository:
```
git clone https://github.com/TechnionTDK/spanthera.git
```

- Make sure you have Java 8 & Maven installed.

- Build:
```
mvn install
```

- In case you get errors for failing tests, then build using:
```
mvn clean install -DskipTests
```

## Finding psukim for a given text (command line)
- Execute the command:
```
java -cp target/spanthera-1.0-jar-with-dependencies.jar apps.jbsmekorot.JbsMekorot -text "Type your text here"
```

The output (in json format) will be sent to the standard output.
