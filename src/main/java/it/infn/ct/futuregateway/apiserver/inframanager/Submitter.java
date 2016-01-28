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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.job.Job;

/**
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
     * The thread managing the submission of a task.
     * The submission is performed with jSAGA and run in a separate thread.
     *
     * @param aTask The task managed by the thread
     */
    public Submitter(final Task aTask) {
        this.task = aTask;
    }


    @Override
    public final void run() {
        Job job;
        try {
            job = CustomJobFactory.createJob(task);
        } catch (InfrastructureException ex) {
            log.error("JobFactory does not work");
            log.error(ex);
            task.setStatus(Task.STATUS.ABORTED);
        }
    }
}
