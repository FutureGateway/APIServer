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

package it.infn.ct.futuregateway.apiserver.utils;

/**
 * Collection of constants to use in the application.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class Constants {
    /**
     * Max size of long strings in DB columns.
     */
    public static final int LONGTEXT = 1024;

    /**
     * Default thread pool size.
     * The size of thread pool if not differently specified.
     */
    public static final int DEFAULTTHREADPOOLSIZE = 20;

    /**
     * Maximum multiple of thread pool size.
     * The thread pool can increase the size until the number of threads
     * is MAXTHREADPOOLSIZETIMES * the base number of threads.
     */
    public static final int MAXTHREADPOOLSIZETIMES = 4;

    /**
     * Maximum lifetime of idle thread.
     * Idle thread exceeding the base capacity of a pool are removed if idle
     * for more than MAXTHREADIDLELIFE milliseconds.
     */
    public static final int MAXTHREADIDLELIFE = 30000;

    /**
     * Maximum waiting time for thread to stop.
     * Waiting time in minutes for thread to complete their work during the
     * shutdown of the thread pool.
     */
    public static final int MAXTHREADWAIT = 5;

    /**
     * Mime type produced by the server.
     */
    public static final String  INDIGOMIMETYPE =
            "application/vnd.indigo-datacloud.apiserver+json";

    /**
     * Avoid the class be instantiable.
     */
    private Constants() { }
}
