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

import it.infn.ct.futuregateway.apiserver.resources.Application;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.Job;

/**
 * The Submitter implements the thread responsible for the
 * submission of a task to the remote infrastructure.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class Submitter implements Runnable {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(Submitter.class);

    /**
     * Task managed by this thread.
     */
    private final Task task;

    /**
     * Storage managing the files for the task.
     */
    private final Storage store;

    /**
     * The thread managing the submission of a task.
     * The submission is performed with jSAGA and run in a separate thread.
     *
     * @param aTask The task managed by the thread
     * @param aStore The storage managing the task files
     */
    public Submitter(final Task aTask, final Storage aStore) {
        this.task = aTask;
        this.store = aStore;
    }


    @Override
    public final void run() {
        if (!task.getState().equals(Task.STATE.READY)) {
            log.error("Task " + task.getApplicationId()
                    + " submitted but not in READY status");
            task.setState(Task.STATE.ABORTED);
            return;
        }
        if (task.getApplicationDetail().getOutcome().equals(
                Application.TYPE.JOB)) {
            Job job;
            try {
                job = CustomJobFactory.createJob(task, store);
                job.run();
                task.setNativeId(job.getAttribute(Job.JOBID));                
                task.updateCheckTime();
            } catch (InfrastructureException ex) {
                log.error("JobFactory does not work");
                log.error(ex);
                task.setState(Task.STATE.ABORTED);
            } catch (BadParameterException ex) {
                log.error("Paramaters not correct for the task "
                        + task.getId()
                        + " using the infrastructure "
                        + task.getAssociatedInfrastructureId());
                log.error(ex);
                task.setState(Task.STATE.ABORTED);
            } catch (NotImplementedException | AuthenticationFailedException
                    | AuthorizationFailedException | PermissionDeniedException
                    | IncorrectStateException | DoesNotExistException
                    | TimeoutException | NoSuccessException ex) {
                log.error("Impossible to submit the task: " + task.getId());
                log.error(ex);
                task.setState(Task.STATE.ABORTED);
            }
        }
        if (task.getApplicationDetail().getOutcome().equals(
                Application.TYPE.RESOURCE)) {
            throw new UnsupportedOperationException("Resources not managed");
        }
    }
}
