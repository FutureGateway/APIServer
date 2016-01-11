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
 ***********************************************************************
 */
package it.infn.ct.futuregateway.apiserver.v1.resources.observers;

import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFile;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Observes the changes in a task and takes due actions.
 * This observer verify the status of the task after the changes and take the
 * corresponding actions to enable the following steps. As en example, if the
 * task is READY then the TaskObserber will schedule it.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class TaskObserver implements Observer {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskObserver.class);

    /**
     * EntityManagerFactory registered for the persistence.
     */
    private final EntityManagerFactory emf;

    /**
     * Executor service for the task submission.
     */
    private final ExecutorService es;


    /**
     * Generate the observer of the task.
     * The Observer will monitor the task and trigger the operation requested
     * to move the task to the next step until its execution complete.
     *
     * @param anEntityManagerFactory An EntityManagerFactory to retrieve the
     * persistence context
     * @param anExecutorService An ExecutorService to retrieve threads managing
     * the task submission
     */
    public TaskObserver(EntityManagerFactory anEntityManagerFactory,
            ExecutorService anExecutorService) {
        this.emf = anEntityManagerFactory;
        this.es = anExecutorService;
    }


    @Override
    public final void update(final Observable obs, final Object arg) {
        if (!(obs instanceof Task)) {
            log.error("Wrong abject associated with the oserver");
        }
        Task t = (Task) obs;
        if (t.getId() == null) {
            return;
        }
        if (t.getStatus() != null && t.getStatus().equals(Task.STATUS.WAITING)
                && t.getApplicationDetail() != null) {
            if (t.getInputFiles() != null) {
                for (TaskFile tf: t.getInputFiles()) {
                    if (tf.getStatus().equals(TaskFile.FILESTATUS.NEEDED)) {
                        return;
                    }
                }
            }
            t.setStatus(Task.STATUS.READY);
            submit(t);
        }
    }

    /**
     * Submit the task to the remote infrastructure.
     * Submission is performed in a separate thread and it is not blocking
     * so the server can return immediately to the client.
     *
     * @param t The task to submit
     */
    private void submit(final Task t) {
        log.info("Submitted the task: " + t.getId());
    }
}
