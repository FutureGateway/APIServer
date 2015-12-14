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

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public interface Storage {
    /**
     * Resource types supported.
     */
    enum RESOURCE {
        /**
         * Storage area for tasks.
         */
        TASKS,
        /**
         * TaskStorage area for application.
         */
        APPLICATIONS,
        /**
         * TaskStorage area for infrastructure.
         */
         INFRASTRUCTURES
    }

    /**
     * Clean task storage area. Remove all the files associated with a task
     * from the storage area.
     *
     * @param res The resource type
     * @param id The ID of the resource to associate the files
     * @throws java.io.IOException In case the directory cannot be cleaned
     */
    void removeAllFiles(final RESOURCE res, final String id)
            throws IOException;

    /**
     * Create the directory to store the input. Create a directory inside the
     * temporary store with path "<taskId>/input". This is used to store the
     * input file before the submission.
     * <p>
     * Equal to {@code storeFile(res, id, input, destinationName, null)}
     *
     * @param res The resource type
     * @param id The ID of the resource to associate the files
     * @param input InputStream of the file to store
     * @param destinationName File destination
     * @throws IOException In case the file cannot be written
     */
    void storeFile(final RESOURCE res, final String id,
            final InputStream input,
            final String destinationName) throws IOException;

    /**
     * Create the directory to store the input. Create a directory inside the
     * temporary store with path "<taskId>/input". This is used to store the
     * input file before the submission.
     * <p>
     * The action allows to define a sub-folder for the resource where the
     * file is stored. As an example, the action is useful for resources
     * like <i>tasks</i> where it is important to distinguish between input
     * and output files.
     *
     * @param res The resource type
     * @param id The ID of the resource to associate the files
     * @param input InputStream of the file to store
     * @param destinationName File destination
     * @param action Action the file is associated with
     * @throws IOException In case the file cannot be written
     */
    void storeFile(final RESOURCE res, final String id,
            final InputStream input, final String destinationName,
            final String action) throws IOException;
}
