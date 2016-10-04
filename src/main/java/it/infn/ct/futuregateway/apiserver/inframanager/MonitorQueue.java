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

package it.infn.ct.futuregateway.apiserver.inframanager;

import it.infn.ct.futuregateway.apiserver.resources.Task;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The MonitorQueue implements the monitor of activities executed remotely.
 * The monitor consist of a fixed thread pool with all threads reading from
 * a blocking queue the activity to monitor. The MonitorQueueFactory build both
 * the thread pool and the queue and publish outside only the queue.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class MonitorQueue {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(MonitorQueue.class);

    /**
     * The queue of monitored activities.
     */
    private BlockingQueue<Task> monitorQueue;

    /**
     * Pool of monitor threads.
     */
    private ExecutorService monitorPool;

    /**
     * Build a monitor queue.
     * A monitor queue is made of two elements: queue and monitor thread. Both
     * are provided.
     *
     * @param aMonitorQueue A queue to store the activities
     * @param aThreadPool A thread pool for the monitoring
     */
    public MonitorQueue(final BlockingQueue<Task> aMonitorQueue,
            final ExecutorService aThreadPool) {
        this.monitorQueue = aMonitorQueue;
        this.monitorPool = aThreadPool;
    }
    /**
     * Build a monitor queue.
     * A monitor queue is made of two elements: queue and monitor thread. Both
     * are created.
     *
     * @param bufferSize  A queue to store the activities
     * @param threadPoolSize A thread pool for the monitoring
     * @param checkInterval The minimum interval between checks in milliseconds
     */
    public MonitorQueue(final int bufferSize,
            final int threadPoolSize, final int checkInterval) {
        log.debug("Creating the monitor queue");
        monitorQueue = new ArrayBlockingQueue<>(bufferSize);
        log.debug("Creating the monitor thread pool");
        monitorPool = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            monitorPool.submit(new Monitor(monitorQueue, checkInterval));
        }
    }

    /**
     * Gets the queue of the monitor.
     *
     * @return The queue
     */
    public final BlockingQueue<Task> getMonitorQueue() {
        return monitorQueue;
    }

    /**
     * Sets the queue for the monitor.
     *
     * @param aMonitorQueue A queue for monitored activities
     */
    public final void setMonitorQueue(final BlockingQueue<Task> aMonitorQueue) {
        this.monitorQueue = aMonitorQueue;
    }

    /**
     * Gets the monitor thread pool.
     *
     * @return The thread pool
     */
    public final ExecutorService getMonitorPool() {
        return monitorPool;
    }

    /**
     * Sets the monitor thread pool.
     *
     * @param aThreadPool A thread pool
     */
    public final void setMonitorPool(final ExecutorService aThreadPool) {
        this.monitorPool = aThreadPool;
    }

    /**
     * Shutdown the monitor queue stopping all the associated threads.
     *
     * @return True is the monitor can be stopped.
     * @throws InterruptedException If an interrupt raises while waiting
     */
    public final boolean shutDown() throws InterruptedException {
        if (monitorPool.awaitTermination(0, TimeUnit.SECONDS)) {
            log.info("All tasks completed");
        } else {
            log.info("Forcing shutdown...");
            monitorPool.shutdownNow();
        }

        return true;

//throw new UnsupportedOperationException("Shutdown has to be correctly "
//+ "integrated to avoid waiting the end of the queue");
//monitorPool.shutdown();
//return monitorPool.awaitTermination(0, TimeUnit.DAYS);
    }
}
