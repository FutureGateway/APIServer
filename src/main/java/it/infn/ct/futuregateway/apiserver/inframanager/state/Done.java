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

import fr.in2p3.jsaga.impl.job.instance.JobImpl;
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
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.Job;

/**
 * Concrete state <i>Done</i> for the task.
 * No specific action connected with the aborted state.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
public class Done extends TaskState {
    /**
     * Logger object. Based on apache commons logging.
     */
    private static final Log log = LogFactory.getLog(Scheduled.class);
    /**
     * Reference to the task.
     */
    private Task task;

    /**
     * Builds the Done concrete state and associates the task.
     * @param aTask The associated task
     */
    public Done(final Task aTask) {
        this.task = aTask;
    }

    @Override
    public final void action(
            final ExecutorService anExecutorService,
            final BlockingQueue<Task> aBlockingQueue, final Storage aStorage) {
        try {
            final Job job = CustomJobFactory.createJob(this.task, aStorage);
            ((JobImpl) job).postStagingAndCleanup();
        } catch (InfrastructureException | BadParameterException
                | DoesNotExistException | NotImplementedException
                | PermissionDeniedException | IncorrectStateException
                | TimeoutException | NoSuccessException ex) {
            log.error("Unable to retrive get job for task " + this.task.getId()
                    + "Exception: " + ex.getMessage());
            this.task.setState(Task.STATE.ABORTED);
        }
    }

}
