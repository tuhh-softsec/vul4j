/*
 * Copyright (c) 2015 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MigratePerfSig {
    private final static String SEARCHSTRING = "performance-signature";
    private final static String REPLACESTRING = SEARCHSTRING + "-dynatrace";
    private final static Charset charset = StandardCharsets.UTF_8;

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("usage: java -jar migratePerfSig <JenkinsJobDirectory>");
            System.exit(1);
        }
        walk(args[0]);
    }

    private static void walk(final String path) throws IOException {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else {
                if(f.getName().matches("(build|config)\\.xml")) {
                    System.out.println("File: " + f.getAbsoluteFile());
                    searchAndReplace(f.toPath());
                }
            }
        }
    }

    private static void searchAndReplace(final Path file) throws IOException {
        String content = new String(Files.readAllBytes(file), charset);
        if(content.contains(REPLACESTRING)) {
            System.out.println("File already migrated");
            return;
        }
        content = content.replaceAll(SEARCHSTRING, REPLACESTRING);
        Files.write(file, content.getBytes(charset));
        System.out.println("File migrated");
    }
}
