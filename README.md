Spanthera is a library for working with spans in textual documents
and doing interesting things. A span is a sequence of words in a document. (In fact,
a more precise term would be [n-gram](https://en.wikipedia.org/wiki/N-gram).)

In this README, we explain how to use spanthera using a simple example: entity detection.
Suppose we have a file with a list of entities
such as "Bibi", "Bibi Netanyahu", "Donald Trump", and "Trump",
and that we would like to detect their occurrence in a given text.  

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

# Building an entity detection app
We assume that the entity file was loaded into a class called *Entities* with an API
to fetch/search for entities. We also assume a String variable called *text*
where we would like to detect entities (its context may arrive from a file):

```
Prime minister Bibi Netanyahu had a meeting yesterday with president
Donald Trump. After the meeting Bibi said: "it was a great pleasure
to meet with the president!". 
```

## Create a SpannedDocument object
The first step would be to turn the input string into a SpannedDocument object:
```
doc = new SpannedDocument(text, 1, 3);
```

The parameters 1 and 3 say that we want to look at spans in length 1 to 3. This will allow
us to, e.g., iterate all spans in length 2 such as "Prime minister" and "minister Bibi". Here,
we assume that entities do not exceed length 3. For better performance, ask for the minimal
range of spans.

## Adding a formatter
Often, we would like to remove some characters from the input text before
we start the analysis process. This could be done
using the *SpanFormatter* interface:

```
public interface SpanFormatter {
    public String format(Span s);
    public boolean isCandidate(Span s);
}
``` 

In our example we want to remove the chars: {", !} so we implement the *format* method
accordingly, and the *isCandidate* method to return *true* (meaning that we handle each span).
Eventually, we should pass the formatter object to our SpannedDocument
object so that the formatting will take place:

```
// formatter is an object implementing the SpanFormatter interface
doc.format(formatter);
```

Note that after the formatting, we may retrieve from each span both the original text
(using *span.getText*),
and the formatted text (using *getTextFormatted*).

Consult the class *JbsSpanFormatter* for the implementation of a
formatter for a real-world problem.  

# Adding tags to relevant spans (tagging)
Now that we have "cleaned" the input text, we would like to **mark** (tag) spans that
may represent entities. Specifically, we would like to attach the identifier "BIBI" to
each span in the text that corresponds to "Bibi" or "Bibi Netanyahu", and the
identifier "TRUMP" to spans corresponding to "Donald Trump" or "Trump".

Note that after this tagging process, the document will contain some duplicates.
For example, the span of size two "Bibi Netanyahu" will point to "BIBI" where the span
"Bibi" within it will also point to "BIBI". We will remove the duplicates afterwards.

For adding tags to spans, we use another interface called *SpanTagger*:
```
public interface SpanTagger {
    public List<String> tag(Span s);
    public boolean isCandidate(Span s);
}
```
We should implement the first *tag* method to return a list with "BIBI" for spans such
as "Bibi" and "Bibi Netanyahu", and a list with "TRUMP" for spans such as "Donald Trump"
and "Trump". Again, for the tagging to take place, the tagger object should be added
to the SpannedDocument object:
```
doc.add(tagger);
doc.tag();
```

After calling *doc.tag* we may access the tags of each span using the *span.getTags* method.

Refer to *PsukimTagger* for a more elaborated tagger example. 

# Removing tags from inner spans
As mentioned, since we tag both spans of size one and two (and also three),
the span "Bibi Netanyahu" will have the tag "BIBI", where its inner span "BIBI" will also
have the same tag. The task now is to remove tags from such inner spans, leaving the tag for
only the outer span.

The interface for doing this and other related taks is *SpanManipulation*:
```
public interface SpanManipulation {
    public void manipulate(SpannedDocument doc);
}
```

The method *manipulate* is provided with the SpannedDocument,
and you may perform any manipulation you like on the tags found in the contained spans.
In our example... // TODO

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
