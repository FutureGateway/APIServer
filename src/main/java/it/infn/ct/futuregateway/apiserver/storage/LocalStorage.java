/***********************************************************************
 * Copyright (c) 2015:
 * Istituto Nazionale di Fisica Nucleare (INFN), Italy
 * Consorzio COMETA (COMETA), Italy
 *
 * See http://www.infn.it and and http://www.consorzio-cometa.it for details on
 * the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************/

package it.infn.ct.futuregateway.apiserver.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LocalStorage manager. Manage the local storage for the API Server.
 * The storage is needed to permanently store I/O and temporary files for the
 * tasks and the applications.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class LocalStorage implements Storage {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(LocalStorage.class);

    /**
     * Path to the storage area. The storage are is a location in the local
     * file system where files are saved.
     */
    private String path;


    /**
     * Create the storage.
     * The storage will be initiated with the location in the local file system
     * where files can be stored. The structure of the storage is managed by
     * the instances of this class.
     *
     * @param aPath Path to the dedicated storage
     */
    public LocalStorage(final String aPath) {
        this.path = aPath;
    }

    @Override
    public final Path getCachePath(RESOURCE res, String id, String subfolder) {
        Path filePath;
        if (subfolder != null && !subfolder.isEmpty()) {
            filePath = Paths.get(path, res.name().toLowerCase(),
                id, subfolder);
        } else {
            filePath = Paths.get(path, res.name().toLowerCase(), id);
        }
        try {
            Files.createDirectories(filePath);
        } catch (IOException ioe) {
            log.error("Impossible to create the directory for " + filePath);
            log.error(ioe);
        }
        return filePath;
    }

    @Override
    public final void storeCache(RESOURCE res, String id) {
        log.debug("Store Cache request not needed in local storage,"
                + " it will be ignored!");
    }


    @Override
    public final void storeFile(final RESOURCE res, final String id,
            final InputStream input, final String destinationName)
            throws IOException {
        storeFile(res, id, input, destinationName, null);
    }


    @Override
    public final void storeFile(final RESOURCE res, final String id,
            final InputStream input, final String destinationName,
            final String subfolder) throws IOException {

        Path filePath;
        if (subfolder != null && !subfolder.isEmpty()) {
            filePath = Paths.get(path, res.name().toLowerCase(),
                id, subfolder, destinationName);
        } else {
            filePath = Paths.get(
                path, res.name().toLowerCase(),
                id, destinationName);
        }


        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        Files.copy(input, filePath);
        log.debug("File " + destinationName + " written at '" + filePath + "'");
    }


    @Override
    public final void removeAllFiles(final RESOURCE res, final String id)
            throws IOException {
        java.nio.file.Path filePath = Paths.get(
                path, res.name().toLowerCase(), id);
        if (Files.notExists(filePath)) {
            return;
        }
        if (Files.isDirectory(filePath)) {
            Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(final Path dir,
                        final IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file,
                        final BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        Files.delete(filePath);
    }
}
