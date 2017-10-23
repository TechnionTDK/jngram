package apps.jbsmekorot;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tools.LuceneIndex;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

/**
 * Created by omishali on 25/09/2017.
 */
public class JbsTanachIndex extends LuceneIndex {
    private final static String JSON_DIRECTORY = "./jbs-text/tanach/";

    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @throws Exception
     */
    public JbsTanachIndex() throws Exception {
        super();
    }

    protected String getIndexDirectory() {
        return "tanach";
    }

    protected void createIndex() throws Exception {
        insertJsons(getWriter());
        getWriter().close();
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

    public List<Document> searchExactInText(String phrase) {
        try {
            return super.searchExact("text", phrase);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> searchExactInUri(String phrase) {
        try {
            return super.searchExact("uri", phrase);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}