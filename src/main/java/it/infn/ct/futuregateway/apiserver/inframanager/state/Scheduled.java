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
     * Logger object. Based on apache commons logging.
     */
    private static final Log LOG = LogFactory.getLog(Scheduled.class);

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
            final BlockingQueue<Task> aBlockingQueue, final Storage aStorage) {

        State state = null;
        try {
            final Job job = CustomJobFactory.createJob(this.task, aStorage);
            state = job.getState();
        } catch (InfrastructureException | BadParameterException
                | DoesNotExistException | NotImplementedException
                | TimeoutException | NoSuccessException ex) {
            LOG.error("Error checking job status: " + ex.getMessage());
            return;
        }

        this.task.updateCheckTime();
        switch (state) {
            case DONE:
                this.task.setState(Task.STATE.DONE);
                break;
            case RUNNING:
                this.task.setState(Task.STATE.RUNNING);
                break;
            case CANCELED:
                this.task.setState(Task.STATE.CANCELLED);
                break;
            case FAILED:
            case NEW:
            case SUSPENDED:
                this.task.setState(Task.STATE.ABORTED);
                break;
            default:
                LOG.error("Task: " + this.task.getId() + " is in a invalid"
                        + " state: " + state);
                this.task.setState(Task.STATE.ABORTED);
                break;
        }

    }
}
