package spanthera.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by valkh on 11-Dec-16.
 * Thanks to taglib guys!
 */
public class SpantheraIO {

    private static JsonObject readJsonFromPath(String filePath){
        JsonObject temp = null;
        try {
            String fullPath = filePath;
            Path myPath = Paths.get(fullPath);
            String content = new String(Files.readAllBytes(myPath));

            temp = new JsonParser().parse(content).getAsJsonObject();
        }
        catch (IOException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }

        return temp;
    }

    public static TaggerInput readInputJson(String filePath){
        return new Gson().fromJson(readJson(filePath), TaggerInput.class);
    }

    /**
     * Returns the names of all json files in the given directory, skips "packages" jsons.
     * @param dirPath
     * @return
     */
    public static String[] getJsonsInDir(String dirPath) {
        File dir = new File(dirPath);
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json") && !name.contains("-packages");
            }
        });

        return files;
    }

    private static JsonObject readJson(String filePath){
        return readJsonFromPath(filePath);
    }

    private static TaggerInput convertJsonToInput(JsonObject jo){
        return new Gson().fromJson(jo, TaggerInput.class);
    }

    private static TaggerInput readInputFile(String filePathWithName){
        return convertJsonToInput(readJsonFromPath(filePathWithName));
    }

//    public static void writeJsonToPath(TaggerOutput out, String outFileFullNameWithPath){
//        String fullPath = outFileFullNameWithPath;
//
//        if (out.countTags() != 0){
//            try (Writer writer = new FileWriter(fullPath)){
//                Gson g = new GsonBuilder().setPrettyPrinting().create();
//                g.toJson(out, writer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void writeJson(TaggerOutput out, String outFileName){
//        writeJsonToPath(out ,"jsons/output/" + outFileName);
//    }
}
