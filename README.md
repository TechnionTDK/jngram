Spanthera is a library for working with ngrams in textual documents
and doing interesting things. An [n-gram](https://en.wikipedia.org/wiki/N-gram) is a contiguous sequence of words in a document.

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
These steps are needed if you wish use to spanthera for text analysis tasks. 
- Open IntelliJ and create a **new** project where the code using spanthera
will reside.
- Add a dependency to spanthera in the new project:
  - File > Project Structure > Modules
  - Dependencies > Add JARs or directories...
  - Browse and add the folder **spanthera/target/classes**
  - Make sure the dependency is checked and press "OK".

Note: in case the spanthera library is changed, to accommodate the changes, 'git gull' the
spanthera repository and 'Rebuild Project'. 
 
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

## Create an NgramDocument
The first step would be to turn the input string into an NgramDocument object:
```
doc = new NgramDocument(text, 1, 3);
```

The parameters 1 and 3 say that we want to look at ngrams in length 1 to 3. This will allow
us to, e.g., iterate all ngrams in length 2 such as "Prime minister" and "minister Bibi". Here,
we assume that entities do not exceed length 3. For better performance, ask for the minimal
range of ngrams.

## Adding a formatter
Often, we would like to remove some characters from the input text before
we start the analysis process. This could be done
using the *NgramFormatter* interface:

```
public interface NgramFormatter {
    public String format(Ngram ng);
    public boolean isCandidate(Ngram ng);
}
``` 

In our example we want to remove the chars: {", !} so we implement the *format* method
accordingly, and the *isCandidate* method to return *true* (meaning that we handle each ngram).
Eventually, we should pass the formatter object to our NgramdDocument
object so that the formatting will take place:

```
// formatter is an object implementing the NgramFormatter interface
doc.format(formatter);
```

Note that after the formatting, we may retrieve from each ngram both the original text
(using *ngram.getText*),
and the formatted text (using *getTextFormatted*).

Consult the class *JbsNgramFormatter* for the implementation of a
formatter for a real-world problem.  

# Adding tags to relevant ngrams (tagging)
Now that we have "cleaned" the input text, we would like to **mark** (tag) ngrams that
may represent entities. Specifically, we would like to attach the identifier "BIBI" to
each ngram in the text that corresponds to "Bibi" or "Bibi Netanyahu", and the
identifier "TRUMP" to ngrams corresponding to "Donald Trump" or "Trump".

Note that after this tagging process, the NgramDocument will contain some duplicates.
For example, the ngram of size two "Bibi Netanyahu" will point to "BIBI" where the ngram
"Bibi" within it will also point to "BIBI". We will remove the duplicates afterwards.

For adding tags to ngrams, we use another interface called *NgramTagger*:
```
public interface NgramTagger {
    public List<String> tag(Ngram ng);
    public boolean isCandidate(Ngram ng);
}
```
We should implement the first *tag* method to return a list with "BIBI" for ngrams such
as "Bibi" and "Bibi Netanyahu", and a list with "TRUMP" for ngrams such as "Donald Trump"
and "Trump". Again, for the tagging to take place, the tagger object should be added
to the NgramDocument object:
```
doc.add(tagger);
doc.tag();
```

After calling *doc.tag* we may access the tags of each ngram using the *ngram.getTags* method.

Refer to *PsukimTagger* for a more elaborated tagger example. 

# Removing tags from inner ngrams
As mentioned, since we tag both ngrams of size one and two (and also three),
the ngram "Bibi Netanyahu" will have the tag "BIBI", where its inner ngram "BIBI" will also
have the same tag. The task now is to remove tags from such inner ngrams, leaving the tag for
only the outer ngram.

The interface for doing this and other related taks is *NgramDocumentManipulation*:
```
public interface NgramDocumentManipulation {
    public void manipulate(NgramDocument doc);
}
```

The method *manipulate* is provided with the NgramDocument,
and you may perform any manipulation you like on the tags found in the contained ngrams.
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
