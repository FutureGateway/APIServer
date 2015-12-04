/**
 * *********************************************************************
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
 **********************************************************************
 */
package it.infn.ct.futuregateway.apiserver.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class ThreadPoolFactory implements ObjectFactory {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(ThreadPoolFactory.class);

    @Override
    public final Object getObjectInstance(final Object obj, final Name name,
            final Context ctx, final Hashtable<?, ?> env) throws Exception {

        Reference ref = (Reference) obj;
        Enumeration addrs = ref.getAll();
        int threadPoolSize = Constants.DEFAULTTHREADPOOLSIZE;
        int maxThreadPoolSize = Constants.MAXTHREADPOOLSIZETIMES
                * threadPoolSize;
        int maxThreadIdleTime = Constants.MAXTHREADPOOLSIZETIMES;
        while (addrs.hasMoreElements()) {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String addrName = addr.getType();
            String addrValue = (String) addr.getContent();
            switch (addrName) {
                case "poolSize":
                    try {
                        threadPoolSize = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute poolSize format not correct."
                                + " Default value applied.");
                    }
                    break;
                case "maxPoolSize":
                    try {
                        maxThreadPoolSize = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute maxPoolSize format not correct."
                                + " Default value applied.");
                    }
                    break;
                case "maxThreadIdleTimeMills":
                    try {
                        maxThreadIdleTime = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute maxThreadIdleTimeMills format not"
                                + " correct. Default value applied.");
                    }
                    break;
                default:
            }
        }
        log.info("A new thread pool created with name: " + name.toString());
        return (ThreadPoolFactory.getThreadPool(threadPoolSize,
                maxThreadPoolSize, maxThreadIdleTime));
    }

    /**
     * Create a new ExecutorService.
     * The ExecutorService is based on the ThreadPoolExecutor but only
     * a subset of parameter can be specified.
     *
     * @param threadPoolSize The initial and minimum size of the pool
     * @param maxThreadPoolSize The maximum size of the pool
     * @param maxThreadIdleTime The time in milliseconds a thread can be idle
     * @return The new ExecutorService
     */
    public static ExecutorService getThreadPool(final int threadPoolSize,
                final int maxThreadPoolSize, final int maxThreadIdleTime) {
        return new ThreadPoolExecutor(
                threadPoolSize,
                maxThreadPoolSize,
                maxThreadIdleTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }
}
