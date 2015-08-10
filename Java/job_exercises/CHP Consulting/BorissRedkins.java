package fragment.submissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BorissRedkins {

    private static final String DELIMITER = ";";
    private static String assembleFlag;

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsageAndExit();
        }

        Path path = Paths.get(args[0]);
        if (!Files.exists(path)) {
            printFileNotExistAndExit(path);
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String fragmentProblem;
            while ((fragmentProblem = reader.readLine()) != null) {
                System.out.println(reassemble(fragmentProblem));
            }
        } catch (IOException e) {
            System.err.println("I/O error occured opening the file: " + path);
            e.printStackTrace();
        }

    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java BorissRedkins <input_file>");
        System.exit(-1);
    }

    private static void printFileNotExistAndExit(Path path) {
        System.err.println("File does not exist or its existence cannot be determined: " + path);
        System.exit(-1);
    }

    private static String reassemble(String string) {
        return reassemble(new ArrayList<>(Arrays.asList(string.split(DELIMITER))));
    }

    private static String reassemble(ArrayList<String> tokens) {
        String assembledTokens = "";

        if (tokens.size() > 1) {
            String token = tokens.get(0);
            int maxOverlap = maxNumberOfOverlappingChars(tokens);
            for (String nextToken : tokens.subList(1, tokens.size())) {
                int overlap = numberOfOverlappingChars(token, nextToken);
                if (maxOverlap > 0 && overlap == maxOverlap) {
                    assembledTokens = assemble(token, nextToken, maxOverlap);
                    tokens.remove(token);
                    tokens.remove(nextToken);
                    tokens.add(assembledTokens);
                    break;
                }
            }
            return reassemble(tokens);
        } else {
            return tokens.get(0);
        }
    }

    private static int maxNumberOfOverlappingChars(List<String> tokens) {
        int maxOverlap = 0;

        String token = tokens.get(0);
        for (String nextToken : tokens.subList(1, tokens.size())) {
            int overlap = numberOfOverlappingChars(token, nextToken);
            maxOverlap = overlap > maxOverlap ? overlap : maxOverlap;
        }

        return maxOverlap;
    }

    private static int numberOfOverlappingChars(String string1, String string2) {
        int numOfOverlappingRight = findNumOfIntersectionChars(string1, string2);
        int numOfOverlappingLeft = findNumOfIntersectionChars(string2, string1);
        int numOfInclusion = findNumOfInclusionChars(string1, string2);
        int max = Math.max(numOfOverlappingRight, Math.max(numOfOverlappingLeft, numOfInclusion));

        if (numOfOverlappingRight == max) {
            assembleFlag = "right";
        } else if (numOfOverlappingLeft == max) {
            assembleFlag = "left";
        } else if (numOfInclusion == max) {
            assembleFlag = "center";
        }

        return max;
    }

    private static int findNumOfIntersectionChars(String string1, String string2) {
        int n = 0;

        for (int i = 1; i <= string2.length(); i++) {
            if (string1.endsWith(string2.substring(0, i))) {
                n = i;
            }
        }

        return n;
    }

    private static int findNumOfInclusionChars(String string1, String string2) {
        int n = 0;

        if (string1.contains(string2)) {
            return string2.length();
        } else if (string2.contains(string1)) {
            return string1.length();
        }

        return n;
    }

    private static String assemble(String string1, String string2, int maxOverlap) {
        switch (assembleFlag) {
            case "right":
                return string1 + string2.substring(maxOverlap, string2.length());
            case "left":
                return string2 + string1.substring(maxOverlap, string1.length());
            case "center":
                return string1.length() > string2.length() ? string1 : string2;
        }
        return "";
    }

}
