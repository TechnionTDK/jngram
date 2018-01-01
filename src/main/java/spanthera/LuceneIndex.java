package spanthera;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class LuceneIndex {

    private static final int NUM_OF_RESULTS = 1500;
    private Directory index;
    private IndexSearcher indexSearcher;
    private IndexWriter writer;
    private static final String ROOT_DIRECTORY = "./tools/luceneIndex/";

    abstract protected String getOutputIndexDirectory();
    abstract protected void createIndex() throws Exception;
    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     * @throws Exception
     */
    public LuceneIndex() {

        IndexReader reader = null;
        try {
            index = FSDirectory.open(Paths.get(ROOT_DIRECTORY + getOutputIndexDirectory()));
            reader = DirectoryReader.open(index);
        } catch (IOException e) {
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            try {
                writer = new IndexWriter(index, config);
                createIndex();
                reader = DirectoryReader.open(index);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        indexSearcher = new IndexSearcher(reader);
    }

//    void clear() throws IOException {
//        FileUtils.cleanDirectory(new File(indexDirectory));
//    }

    protected IndexWriter getWriter() {
        return writer;
    }

    // see https://stackoverflow.com/questions/18100233/lucene-fuzzy-search-on-a-phrase-fuzzyquery-spanquery
    private Query generateQuery(String[] words, int startWord, int minimalPhrase) {
        SpanQuery[] clauses = new SpanQuery[minimalPhrase];
        for (int i = 0; i < minimalPhrase; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term("text", words[startWord + i]), 2));
        }
        return new SpanNearQuery(clauses, 0, true);
    }

    /**
     * Get a string phrase and returns the Documents
     * that contain that phrase <b>exactly</b>.
     * @param phrase
     * @return
     */
    protected List<Document> searchExact(String field, String phrase) throws IOException {
        List<Document> result;
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        String[] terms = phrase.split(" ");
        for (String term : terms)
            builder.add(new Term(field, term));

        builder.setSlop(0);
        PhraseQuery q = builder.build();
        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
        ScoreDoc[] hits = docs.scoreDocs;

        return getTextList(hits);
    }



    private List<Document> getTextList(ScoreDoc[] hits) throws IOException {
        List<Document> result = new ArrayList<>();
        for (int k = 0; k < hits.length; k++) {
            int docId = hits[k].doc;
            Document d = indexSearcher.doc(docId);
            result.add(d);
        }

        return result;
    }

    protected List<Document> searchFuzzy(String field, String phrase, int maxEdits) throws IOException {
        String[] terms = phrase.split(" ");
        SpanQuery[] clauses = new SpanQuery[terms.length];
        for (int i = 0; i < terms.length; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term(field, terms[i]), maxEdits));
        }

        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
        ScoreDoc[] hits = docs.scoreDocs;
        return getTextList(hits);
    }

    protected List<Document> searchFuzzy(String field, String phrase, List<Integer> maxEdits) throws IOException {
        String[] terms = phrase.split(" ");
        SpanQuery[] clauses = new SpanQuery[terms.length];
        for (int i = 0; i < terms.length; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term(field, terms[i]), maxEdits.get(i)));
        }

        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
        ScoreDoc[] hits = docs.scoreDocs;
        return getTextList(hits);
    }

//    protected List<Document> searchFuzzy2(String field, String phrase, int maxEdits) throws IOException {
//        //String[] terms = phrase.split(" ");
//        FuzzyQuery clauses = new FuzzyQuery(new Term(field, phrase),maxEdits);
//        //for (int i = 0; i < terms.length; i++) {
//         //   clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term(field, terms[i]), maxEdits));
//        //}
//
//        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
//        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
//        ScoreDoc[] hits = docs.scoreDocs;
//        return getTextList(hits);
//    }

    protected void searchFuzzy(String field, String phrase) throws IOException {
        String[] terms = phrase.split(" ");
        SpanQuery[] clauses = new SpanQuery[terms.length];
        for (int i = 0; i < terms.length; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term(field, terms[i]), 1));
        }

        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
        ScoreDoc[] hits = docs.scoreDocs;
        printHits(hits);
    }

    private void printHits(ScoreDoc[] hits) throws IOException {
        for (int k = 0; k < hits.length; ++k) {
            int docId = hits[k].doc;
            Document d = indexSearcher.doc(docId);
            System.out.println((k + 1) + ". " + d.get("uri") + "\t" + d.get("text"));
        }
    }

    public void printDocs(List<Document> docs) {
        for (Document d : docs)
            System.out.println(d.get("uri") + "\t" + d.get("text"));
    }

    public List<Document> searchFuzzyRestriction(String field, String phrase, int maxEdits, int minEditedWordLength) throws IOException{
        String[] terms = phrase.split(" ");
        SpanQuery[] clauses = new SpanQuery[terms.length];
        for (int i = 0; i < terms.length; i++) {
            int edits= terms[i].length() >=minEditedWordLength ? maxEdits: 0;
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term(field, terms[i]), edits));
        }

        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        TopDocs docs = indexSearcher.search(q, NUM_OF_RESULTS);
        ScoreDoc[] hits = docs.scoreDocs;
        return getTextList(hits);
    }
}
