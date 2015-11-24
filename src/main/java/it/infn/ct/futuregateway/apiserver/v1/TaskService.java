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

package it.infn.ct.futuregateway.apiserver.v1;

import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The TaskService provide the REST APIs for the task
 * as defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/tasks/{id}")
public class TaskService extends BaseService {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskService.class);

    /**
     * Retrieve the task details.
     * Task details include all the field a task consist of as described in the
     * documentation. This include all the information included in the task
     * collection and many others.
     *
     * @param id The task id. This is a path parameter retrieved from the url
     * @return The task
     */
    @GET
    @Produces(Constants.MIMETYPE)
    public final Task getTaskDetails(@PathParam("id") final String id) {
        Task task;
        EntityManager em = getEntityManager();
        try {
            task = em.find(Task.class, id);
            log.error("Associated " + task.getInputFiles().size() + " files");
        } catch (RuntimeException re) {
            log.error("Impossible to retrieve the task list");
            log.error(re);
            throw new RuntimeException("Impossible to access the task list");
        } finally {
            em.close();
        }
        return task;
    }
}
