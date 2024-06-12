package org.vul4j;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GitUtils {

    private GitUtils() {
    }

    private static final Logger logger = Logger.getLogger(GitUtils.class.getName());

    public static String[] getFirstTwoCommitHashes(String repositoryDir) throws IOException, NullPointerException {

        String[] commitHashes = new String[2];

        try (Repository repository = Git.open(new File(repositoryDir)).getRepository()) {

            ReflogReader reflogReader = repository.getReflogReader("refs/heads/master");

            if (reflogReader != null) {
                ReflogEntry firstTwoCommits = reflogReader.getReverseEntries(1).get(0);
                commitHashes[0] = firstTwoCommits.getOldId().getName();
                commitHashes[1] = firstTwoCommits.getNewId().getName();
            }
        }

        return commitHashes;
    }

    /*
    Returns the modified filenames and lines of the given commit.
     */
    public static Map<String, int[]> getModifiedFilesAndLines(
            String repositoryDir,
            String vulnerableHash,
            String fixingHash
    ) throws IOException {
        Map<String, int[]> modifiedFilesAndLines = new HashMap<>();

        // Connect to repository
        try (Repository repository = Git.open(new File(repositoryDir)).getRepository();
             Git git = new Git(repository)) {

            ObjectId fixingCommitId = repository.resolve(fixingHash);
            ObjectId vulnerableCommitId = repository.resolve(vulnerableHash);

            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit fixingCommit = revWalk.parseCommit(fixingCommitId);
                RevCommit vulnerableCommit = revWalk.parseCommit(vulnerableCommitId);

                RevTree fixingTree = fixingCommit.getTree();
                RevTree vulnerableTree = vulnerableCommit.getTree();

                try (ObjectReader reader = repository.newObjectReader();
                     ByteArrayOutputStream diffOutput = new ByteArrayOutputStream()) {

                    CanonicalTreeParser fixingTreeParser = new CanonicalTreeParser();
                    fixingTreeParser.reset(reader, fixingTree.getId());

                    CanonicalTreeParser vulnerableTreeParser = new CanonicalTreeParser();
                    vulnerableTreeParser.reset(reader, vulnerableTree.getId());

                    List<DiffEntry> diffs = git.diff()
                            .setOldTree(vulnerableTreeParser)
                            .setNewTree(fixingTreeParser)
                            .call();

                    DiffFormatter formatter = new DiffFormatter(diffOutput);
                    formatter.setRepository(repository);

                    for (DiffEntry diff : diffs) {
                        formatter.setContext(0);
                        formatter.format(diff);

                        int[] modifiedLines = getModifiedLines(diffOutput.toString(StandardCharsets.UTF_8));
                        modifiedFilesAndLines.put(diff.getNewPath(), modifiedLines);
                        diffOutput.reset();
                    }
                }
            }
        } catch (GitAPIException e) {
            logger.severe(e.getMessage());
        }

        return modifiedFilesAndLines;
    }


    /*
    Returns an array of modified lines based in a given file based on the git diff string.
     */
    private static int[] getModifiedLines(String diffText) {
        String[] lines = diffText.split("\n");
        List<Integer> lineNumbersList = new ArrayList<>();

        for (String line : lines) {
            int startLine;
            int endLine;

            if (line.startsWith("@@")) {
                String[] parts = line.split(" ");
                String[] range = parts[2].split(",");
                if (range.length == 2) {
                    startLine = Integer.parseInt(range[0].substring(1));
                    endLine = startLine + Integer.parseInt(range[1]);
                } else {
                    startLine = Integer.parseInt(range[0]);
                    endLine = Integer.parseInt(range[0]);
                }
                for (int i = 0; i <= endLine - startLine; i++) {
                    lineNumbersList.add(startLine + i);
                }
            }
        }

        return lineNumbersList.stream().mapToInt(Integer::intValue).toArray();
    }

    /*
    Returns whether any constructor was modified in the given file
     */
    public static boolean isConstructorModified(
            String fileContent,
            int[] lineNumbers
    ) throws NoSuchElementException {
        List<ConstructorDeclaration> constructorList = new ArrayList<>();

        // parse the file content with JavaParser
        CompilationUnit compilationUnit = new JavaParser().parse(fileContent).getResult().orElseThrow();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ConstructorDeclaration constructorDeclaration, Void arg) {

                // only add modified methods
                if (isModified(constructorDeclaration, lineNumbers)) constructorList.add(constructorDeclaration);

                super.visit(constructorDeclaration, arg);
            }
        }, null);

        return !constructorList.isEmpty();
    }

    /*
    Adds methods to the keepList that were modified in the given file
     */
    public static List<String> extractModifiedMethodNames(
            String fileContent,
            int[] lineNumbers
    ) throws NoSuchElementException {
        List<MethodDeclaration> methodList = new ArrayList<>();

        // parse the file content with JavaParser
        CompilationUnit compilationUnit = new JavaParser().parse(fileContent).getResult().orElseThrow();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration methodDeclaration, Void arg) {

                // only add modified methods
                if (isModified(methodDeclaration, lineNumbers)) methodList.add(methodDeclaration);

                super.visit(methodDeclaration, arg);
            }
        }, null);

        var methodsNamesList = methodList
                .stream().map(MethodDeclaration::getNameAsString).collect(Collectors.toList());

        if (isConstructorModified(fileContent, lineNumbers)) methodsNamesList.add("<init>");

        return methodsNamesList;
    }

    /*
    Adds class attributes to the keepList that were modified in the given file
     */
    public static List<String> extractModifiedAttributes(
            String fileContent,
            int[] lineNumbers
    ) throws NoSuchElementException {
        List<FieldDeclaration> attributeList = new ArrayList<>();

        // parse the file content with JavaParser
        CompilationUnit compilationUnit = new JavaParser().parse(fileContent).getResult().orElseThrow();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(FieldDeclaration fieldDeclaration, Void arg) {

                // only add modified methods
                if (isModified(fieldDeclaration, lineNumbers)) attributeList.add(fieldDeclaration);

                super.visit(fieldDeclaration, arg);
            }
        }, null);

        return attributeList.stream().map(field -> field.getVariable(0).getNameAsString()).toList();
    }

    public static ExtractedData extractData(
            ClassOrInterfaceDeclaration parentClass,
            int[] lineNumbers
    ) throws NoSuchElementException {
        ExtractedData extractedData = new ExtractedData();

        parentClass.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration classDeclaration, Void arg) {
                // only add modified methods
                if (classDeclaration != parentClass && isModified(classDeclaration, lineNumbers)) {
                    var result = extractData(classDeclaration, lineNumbers);
                    extractedData.classes.put(classDeclaration.getNameAsString(), result);
                }
                super.visit(classDeclaration, arg);
            }

            @Override
            public void visit(ConstructorDeclaration constructorDeclaration, Void arg) {
                // only add modified methods
                if (Objects.equals(constructorDeclaration.getParentNode().orElse(null), parentClass)
                        && isModified(constructorDeclaration, lineNumbers)) {
                    extractedData.methods.add("<init>");
                }
                super.visit(constructorDeclaration, arg);
            }

            @Override
            public void visit(MethodDeclaration methodDeclaration, Void arg) {
                // only add modified methods
                if (Objects.equals(methodDeclaration.getParentNode().orElse(null), parentClass)
                        && isModified(methodDeclaration, lineNumbers)) {
                    extractedData.methods.add(methodDeclaration.getNameAsString());
                }
                super.visit(methodDeclaration, arg);
            }

            @Override
            public void visit(FieldDeclaration fieldDeclaration, Void arg) {
                // only add modified methods
                if (Objects.equals(fieldDeclaration.getParentNode().orElse(null), parentClass)
                        && isModified(fieldDeclaration, lineNumbers)) {
                    extractedData.attributes.add(fieldDeclaration.getVariable(0).getNameAsString());
                }
                super.visit(fieldDeclaration, arg);
            }
        }, null);

        return extractedData;
    }


    private static boolean isModified(Node node, int[] lineNumbers) {
        // get the start line and end line of the method
        int startLine = node.getBegin().orElseThrow().line;
        int endLine = node.getEnd().orElseThrow().line;

        // check if any of the lines in the method is modified
        for (int lineNumber : lineNumbers) {
            if (lineNumber >= startLine && lineNumber <= endLine) {
                return true;
            }
        }
        return false;
    }


    /*
    Extract content from selected file in the repository.
     */
    public static String getFileContentAtCommit(String repositoryDir, String filePath, String commitHash) throws IOException {
        try (Repository repository = Git.open(new File(repositoryDir)).getRepository()) {
            RevCommit commit = repository.parseCommit(repository.resolve(commitHash));

            try (TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, commit.getTree());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                if (treeWalk != null) {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    loader.copyTo(out);
                    return out.toString(StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }


    public static ClassOrInterfaceDeclaration extractRootClass(String fileContent) {

        // Parse the file content with JavaParser
        CompilationUnit compilationUnit = new JavaParser().parse(fileContent).getResult().orElseThrow();

        return compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElse(null);

    }

    /*
    Extracts class name with package.
     */
    public static String extractClassNameWithPackage(String fileContent) {

        StringBuilder builder = new StringBuilder();

        // Parse the file content with JavaParser
        CompilationUnit compilationUnit = new JavaParser().parse(fileContent).getResult().orElseThrow();

        compilationUnit.getPackageDeclaration()
                .ifPresent(packageDeclaration -> builder.append(packageDeclaration.getName()).append("."));

        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .ifPresent(classDeclaration -> builder.append(classDeclaration.getNameAsString()));

        return builder.toString();
    }
}
