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

import it.infn.ct.futuregateway.apiserver.inframanager.state.TaskState;
import it.infn.ct.futuregateway.apiserver.resources.Application;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implement the action monitor. The activity to monitor is retrieved from a
 * queue and if the resource is not in a final state it is added to the queue
 * again.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class Monitor implements Runnable {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(Monitor.class);

    /**
     * Queue with activity to monitor.
     */
    private BlockingQueue<Task> bQueue;

    /**
     * Interval between status checks.
     * Values are in milliseconds.
     */
    private int checkInterval;

    /**
     * Build a monitor associated with a queue.
     *
     * @param aBlockingQueue The queue of activities to monitor
     * @param aCheckInterval The minimum interval between checks in milliseconds
     */
    public Monitor(final BlockingQueue<Task> aBlockingQueue,
            final int aCheckInterval) {
        this.bQueue = aBlockingQueue;
        this.checkInterval = aCheckInterval;
    }

    @Override
    public final void run() {
        Task task;
        while ((task = getNext()) != null) {
            long remainingTime = checkInterval - System.currentTimeMillis()
                    + task.getLastStatusCheckTime().getTime();
            if (remainingTime > 0) {
                try {
                    Thread.sleep(remainingTime);
                } catch (InterruptedException ie) {
                    log.warn("Monitoring thread interrupted while waiting "
                            + "for next check");
                }
            }
            if (task.getApplicationDetail().getOutcome().equals(
                    Application.TYPE.JOB)) {
                log.debug("Monitoring Task: " + task.getId());
                try {
                    TaskState ts = task.getStateManager();
                    ts.action(null, bQueue, null);
                } catch (TaskException te) {
                    task.setState(Task.STATE.ABORTED);
                    log.error(te.getMessage());
                }
            }
            if (task.getApplicationDetail().getOutcome().equals(
                    Application.TYPE.RESOURCE)) {
                throw new UnsupportedOperationException("Not implemented yet");
            }

        }
    }


    /**
     * Retrieves the next task to monitor.
     * @return The task
     */
    private Task getNext() {
        Task t = null;
        try {
            t = bQueue.take();
            if (t.getId() == null) {
                return null;
            }
        } catch (InterruptedException ie) {
            log.warn("Monitoring queue interrupted.");
        }
        return t;
    }

}
