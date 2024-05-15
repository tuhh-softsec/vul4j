package org.vul4j;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static void extractData(String repositoryDir, String outputFile) {

        Map<String, List<String>> results = new HashMap<>();

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

                var methodsList = GitUtils.extractModifiedMethodNames(fileContent, lineNumbers)
                        .stream().map(MethodDeclaration::getNameAsString).toList();
                var parentClass = GitUtils.extractClassNameWithPackage(fileContent);
                results.put(parentClass, methodsList);
            }

            saveNamesToJson(outputFile, results);
            logger.info("Extraction completed!");
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }


    private static void saveNamesToJson(String filename, Map<String, List<String>> results) {
        JSONObject json = new JSONObject(results);
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(json.toString(2));
        } catch (IOException exc) {
            logger.severe(exc.getMessage());
        } finally {
            logger.log(Level.INFO, "Results saved to JSON!");
        }
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            logger.severe("Usage: GitCommitAnalyzer <repo_dir> <output_file>");
            System.exit(1);
        }

        extractData(args[0], args[1]);
    }
}
