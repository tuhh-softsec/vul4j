/**
 * Copyright (c) 2017-2018 The Semux Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.semux.util;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static final Set<PosixFilePermission> POSIX_SECURED_PERMISSIONS = new HashSet<>(Arrays.asList(
            OWNER_READ,
            OWNER_WRITE));
    /**
     * Delete a file or directory recursively.
     * 
     * @param file
     */
    public static void recursiveDelete(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        }

        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.error("Failed to delete file: {}", file, e);
        }
    }

    public static boolean isPosixPermissionSecured(File file) throws IOException {
        return Files.getPosixFilePermissions(file.toPath()).equals(POSIX_SECURED_PERMISSIONS);
    }

    private FileUtil() {
    }

}
