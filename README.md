# Installation
Clone the repository:
```
git clone https://github.com/TechnionTDK/spanthera.git
```

Make sure you have Java 8 & Maven installed.

Build:
```
mvn install
```

In case you get errors for failing tests, then build using:
```
mvn clean install -DskipTests
```

# Finding psukim for a given text
Execute the command:
```
java -cp target/spanthera-1.0-jar-with-dependencies.jar apps.jbsmekorot.JbsMekorot -text "Type your text here"
```

The output (in json format) will be sent to the standard output.