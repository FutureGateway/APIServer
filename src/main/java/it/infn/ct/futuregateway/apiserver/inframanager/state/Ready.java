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

package it.infn.ct.futuregateway.apiserver.inframanager.state;

import it.infn.ct.futuregateway.apiserver.inframanager.MonitorQueue;
import it.infn.ct.futuregateway.apiserver.inframanager.Submitter;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete state <i>Ready</i> for the task.
 * When a task is in Ready state the associated action will be to submit
 * the task to the remote infrastructure
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
public class Ready extends TaskState {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(Ready.class);

    /**
     * Reference to the task.
     */
    private Task task;

   /**
     * Builds the Ready concrete state and associates the task.
     * @param aTask The associated task
     */
    public Ready(final Task aTask) {
        this.task = aTask;
    }

    @Override
    public final void action(
            final ExecutorService anExecutorService,
            final MonitorQueue aMonitorQueue, final Storage aStorage) {
        aStorage.createCache(Storage.RESOURCE.TASKS, task.getId());
        Future<?> taskThread =
                anExecutorService.submit(new Submitter(task, aStorage));
        try {
            taskThread.get();
        } catch (ExecutionException | InterruptedException ex) {
            log.warn("Submission thread stop without control");
        }
        log.debug("Submitted the task: " + task.getId());
    }

}
