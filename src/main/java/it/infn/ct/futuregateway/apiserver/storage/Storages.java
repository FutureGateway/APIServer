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

/**
 * Factory class for storages.
 * The storage is the cache storage used by the API Service to store temporary
 * files.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class Storages {

    /**
     * Avoid the class be instantiable.
     */
    private Storages() { }

    /**
     * Create a storage access object.
     * The files are stored in a folder inside the local file system.
     *
     * @see Storages#getStorage(java.lang.String, java.lang.String)
     * @param path Path to a folder for caching the files
     * @return A storage access object
     */
    public static Storage getStorage(final String path) {
        return getStorage(null, path);
    }

    /**
     * Create a storage access object.
     * The storage object is selected depending on the protocol specified in
     * the url. If the url is without protocol the local file system is
     * selected. A cache folder contains the I/O files during the task running
     * and then they are moved to the storage.
     * <p>
     * If the url is null the cache folder will be the storage connected with
     * the service.
     *
     * @param url The url to the storage
     * @param cachePath Path to a folder for caching the files
     * @return A storage access object
     */
    public static Storage getStorage(final String url, final String cachePath) {
        if (url == null || !url.contains("://")) {
            return new LocalStorage(cachePath);
        }
        throw new UnsupportedOperationException("No plugin for storage at "
                + url);
    }
}
