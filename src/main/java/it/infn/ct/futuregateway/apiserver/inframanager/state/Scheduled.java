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
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.concurrent.ExecutorService;

/**
 * Concrete state <i>Scheduled</i> for the task.
 * When a task is in Scheduled state the associated action will be to check the
 * state of the activity on the remote infrastructure and eventually move to
 * the next state.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
public class Scheduled extends TaskState {
    /**
     * Reference to the task.
     */
    private Task task;

   /**
     * Builds the Scheduled concrete state and associates the task.
     * @param aTask The associated task
     */
    public Scheduled(final Task aTask) {
        this.task = aTask;
    }

    @Override
    public final void action(
            final ExecutorService anExecutorService,
            final MonitorQueue aMonitorQueue, final Storage aStorage) {
        aMonitorQueue.addTaskToMonitor(task);
    }
}
