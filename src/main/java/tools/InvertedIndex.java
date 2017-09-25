package tools;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InvertedIndex {


    private Directory index;
    private IndexSearcher indexSearcher;
    private String indexDirectory = "./invertedIndex";
    private final static String JSON_DIRECTORY = "./jbs-text/tanach/";


    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     * @throws Exception
     */
    public InvertedIndex() throws Exception {
        index = FSDirectory.open(Paths.get(indexDirectory));
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(index);
        } catch (IOException e) {
            create();
            reader = DirectoryReader.open(index);
        }
        indexSearcher = new IndexSearcher(reader);
    }

//    void clear() throws IOException {
//        FileUtils.cleanDirectory(new File(indexDirectory));
//    }

    /**
     * Creates the Index
     * @throws IOException
     * @throws ParseException
     * @throws org.json.simple.parser.ParseException
     */
    public void create() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(index, config);

        insertJsons(writer);
        writer.close();
    }

    private void insertJsons(IndexWriter writer) throws Exception {
        String[] jsons = getSourceJsons();
        for (String json : jsons)
            insertJsonToIndex(writer, json);
    }

    String[] getSourceJsons() {
        File dir = new File(JSON_DIRECTORY);
        String[] result = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.contains("packages");
            }
        });

        return result;
    }

    private JSONObject getJsonObject(String jsonPath) throws IOException, org.json.simple.parser.ParseException {

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(jsonPath));
        return (JSONObject) obj;
    }

    private String pureText(String text) {
        text = text.replaceAll("\\(.*?\\) ?", "");
        text = text.replaceAll("[^א-ת ]", "");
        text = text.replaceAll("\\s+", " ");
        return text;
    }

    private void insertJsonToIndex(IndexWriter w, String json) throws Exception {
        String jsonPath = JSON_DIRECTORY + json;
        JSONObject jsonObject = getJsonObject(jsonPath);
        JSONArray book = (JSONArray) jsonObject.get("subjects");
        for (Object aBook : book) {
            String text = pureText((String) ((JSONObject) aBook).get("jbo:text"));
            String uri = ((String) ((JSONObject) aBook).get("uri"));
            addDoc(w, text, uri);
        }
    }



    private void addDoc(IndexWriter w, String title, String uri) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("text", title, Field.Store.YES));
        doc.add(new StringField("uri", uri, Field.Store.YES));
        w.addDocument(doc);
    }

    // see https://stackoverflow.com/questions/18100233/lucene-fuzzy-search-on-a-phrase-fuzzyquery-spanquery
    private Query generateQuery(String[] words, int startWord, int minimalPhrase) {
        SpanQuery[] clauses = new SpanQuery[minimalPhrase];
        for (int i = 0; i < minimalPhrase; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term("text", words[startWord + i]), 2));
        }
        return new SpanNearQuery(clauses, 0, true);
    }

//    private List<Subject> getSubjectsFromHits(ScoreDoc[] hits) throws IOException {
//        List<Subject> list = new ArrayList<>();
//        for (ScoreDoc hit : hits) {
//            String text = searcher.doc(hit.doc).get("text");
//            String uri = searcher.doc(hit.doc).get("uri");
//            list.add(new Subject(uri, text));
//        }
//        return list;
//    }

//    List<Subject> search(String[] splitedText, int startWord, int minimalPhrase) throws IOException {
//        Query query = generateQuery(splitedText, startWord, minimalPhrase);
//        int hitsPerPage = 20;
//        TopDocs docs = searcher.search(query, hitsPerPage);
//        ScoreDoc[] hits = docs.scoreDocs;
//        return getSubjectsFromHits(hits);
//    }

    /**
     * Get a string phrase and returns the URIs of the mekorot
     * that contain that phrase
     * @param phrase
     * @return
     */
    public List<Text> searchExact(String phrase) throws IOException {
        List<Text> result;
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        String[] terms = phrase.split(" ");
        for (String term : terms)
            builder.add(new Term("text", term));

        builder.setSlop(0);
        PhraseQuery q = builder.build();
        TopDocs docs = indexSearcher.search(q, 50);
        ScoreDoc[] hits = docs.scoreDocs;

        return getTextList(hits);
    }



    private List<Text> getTextList(ScoreDoc[] hits) throws IOException {
        List<Text> result = new ArrayList<>();
        for (int k = 0; k < hits.length; k++) {
            int docId = hits[k].doc;
            Document d = indexSearcher.doc(docId);
            result.add(new Text(d.get("uri"), d.get("text")));
        }

        return result;
    }

    public void searchFuzzy(String phrase) throws IOException {
        String[] terms = phrase.split(" ");
        SpanQuery[] clauses = new SpanQuery[terms.length];
        for (int i = 0; i < terms.length; i++) {
            clauses[i] = new SpanMultiTermQueryWrapper<>(new FuzzyQuery(new Term("text", terms[i]), 1));
        }

        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        TopDocs docs = indexSearcher.search(q, 50);
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

    /**
     * Represents the result
     */
    public class Text {
        private String uri, text;

        public Text(String uri, String text) {
            setUri(uri);
            setText(text);
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
