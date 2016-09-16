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

import it.infn.ct.futuregateway.apiserver.inframanager.CustomJobFactory;
import it.infn.ct.futuregateway.apiserver.inframanager.InfrastructureException;
import it.infn.ct.futuregateway.apiserver.inframanager.TaskException;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.Job;
import org.ogf.saga.task.State;

/**
 * Concrete state <i>Running</i> for the task.
 * When a task is in Running state the associated action will be to check the
 * state of the activity on the remote infrastructure and eventually move to
 * the next state.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
public class Running extends TaskState {
    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(Running.class);

    /**
     * Reference to the task.
     */
    private Task task;

    /**
     * Builds the Running concrete state and associates the task.
     * @param aTask The associated task
     */
    public Running(final Task aTask) {
        this.task = aTask;
    }

    @Override
    public final void action(
            final ExecutorService anExecutorService,
            final BlockingQueue<Task> aBlockingQueue, final Storage aStorage) {

        State state = null;
        try {
            Job job = CustomJobFactory.createJob(task, aStorage);
            state = job.getState();
        } catch (InfrastructureException | BadParameterException
                | DoesNotExistException | NotImplementedException
                | TimeoutException | NoSuccessException ex) {
            log.error("Error checking job status: " + ex.getMessage());
        }
        try {
            if (state != null) {
                task.updateCheckTime();
                switch (state) {
                    case DONE:
                        task.setState(Task.STATE.DONE);
                        break;
                    case RUNNING:
                        try {
                            aBlockingQueue.put(task);
                        } catch (InterruptedException ex) {
                            log.error(ex.getMessage());
                            task.setState(Task.STATE.ABORTED);
                        }
                        break;
                    case FAILED:
                    case CANCELED:
                    case NEW:
                    case SUSPENDED:
                        task.setState(Task.STATE.ABORTED);
                        break;
                    default:
                        log.error("Task: " + task.getId() + " is in a invalid "
                                + " state: " + state);
                        task.setState(Task.STATE.ABORTED);
                        break;
                }
            } else {
                throw new TaskException("Unable to retrieve job state.");
            }
        } catch (TaskException ex) {
            log.error(ex.getMessage());
        }
    }

}
