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

import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * Concrete state <i>Waiting</i> for the task.
 * When a task is in Waiting state the associated action will be to check the
 * required input and if present move to the next state.
 */
public class Waiting extends TaskState {
    /**
     * Reference to the task.
     */
    private Task task;

   /**
     * Builds the Waiting concrete state and associates the task.
     * @param aTask The associated task
     */
    public Waiting(final Task aTask) {
        this.task = aTask;
    }

    @Override
    public final void action(
            final ExecutorService anExecutorService,
            final BlockingQueue<Task> aBlockingQueue, final Storage aStorage) {
        if (this.task.getInputFiles() != null) {
            for (final TaskFile tf : this.task.getInputFiles()) {
                if (tf.getStatus().equals(TaskFile.FILESTATUS.NEEDED)) {
                    return;
                }
            }
        }
        this.task.setState(Task.STATE.READY);
    }

}
