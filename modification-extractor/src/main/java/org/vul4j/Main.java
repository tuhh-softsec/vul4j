package org.vul4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final String VERSION = "1.0.0";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static void extractData(String repositoryDir, String outputFile) {

        Map<String, ExtractedData> results = new HashMap<>();

        try {
            // vulnerable, fixed
            String[] commitHashes = GitUtils.getFirstTwoCommitHashes(repositoryDir);

            // Get the modified files and changed line numbers
            Map<String, int[]> modifiedFilesAndLines =
                    GitUtils.getModifiedFilesAndLines(repositoryDir, commitHashes[0], commitHashes[1]);

            // Process each modified file
            for (Map.Entry<String, int[]> entry : modifiedFilesAndLines.entrySet()) {

                String filename = entry.getKey();
                int[] lineNumbers = entry.getValue();

                // Check for java and tests
                if (!filename.endsWith("java") || filename.contains("src/test")) continue;

                // Load the file content
                String fileContent = GitUtils.getFileContentAtCommit(repositoryDir, filename, commitHashes[1]);

                ExtractedData extractedData = GitUtils.extractData(GitUtils.extractRootClass(fileContent), lineNumbers);

                var parentClass = GitUtils.extractClassNameWithPackage(fileContent);
                results.put(parentClass, extractedData);


            }

            saveNamesToJson(outputFile, results);
            logger.info("Extraction completed!");
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }


    private static void saveNamesToJson(String filename, Map<String, ExtractedData> results) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(gson.toJson(results));
        } catch (IOException exc) {
            logger.severe(exc.getMessage());
        } finally {
            logger.log(Level.INFO, "Results saved to JSON!");
        }
    }

    public static void main(String[] args) {

        try {
            if (args.length == 2) {
                extractData(args[0], args[1]);
            } else if (args.length == 1 && args[0].equals("-version")) {
                logger.info("Version: " + VERSION);
            } else {
                throw new IllegalArgumentException("Invalid number of arguments!");
            }
        } catch (IllegalArgumentException e) {
            logger.severe("Usage: <repo_dir> <output_file> OR -version");
            System.exit(1);
        }
    }
}
