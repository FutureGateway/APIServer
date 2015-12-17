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
import it.infn.ct.futuregateway.apiserver.utils.annotations.Status;
import it.infn.ct.futuregateway.apiserver.v1.resources.Application;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The TaskCollectionService provide the REST APIs for the task collection
 * as defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task-collection
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/tasks")
public class TaskCollectionService extends BaseService {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskCollectionService.class);


    /**
     * Retrieves the list of tasks.
     *
     * The list includes only the tasks associated to the user.
     *
     * @return The task collection
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final TaskList listTasks() {
        TaskList tasks;
        try {
            tasks = new TaskList(retrieveTaskList());
        } catch (RuntimeException re) {
            getResponse().setStatus(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            throw re;
        }
        return tasks;
    }

    /**
     * Register a new task.
     *
     * @param task The task to register
     * @return The task registered
     */
    @POST
    @Status(Response.Status.CREATED)
    @Consumes({MediaType.APPLICATION_JSON, Constants.INDIGOMIMETYPE})
    @Produces(Constants.INDIGOMIMETYPE)
    public final Task createTask(final Task task) {
        Date now = new Date();
        task.setDateCreated(now);
        task.setLastChange(now);
        task.setStatus(Task.STATUS.WAITING);
        task.setUserName(getUser());
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            Application app = em.find(Application.class,
                    task.getApplicationId());
            if (app == null) {
                throw new BadRequestException();
            }
            task.setApplicationDetail(app);
            em.persist(task);
            et.commit();
            log.debug("New task registered: " + task.getId());
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to create a task");
            log.debug(re);
            throw re;
        } finally {
            em.close();
        }
        return task;
    }


    /**
     * Retrieve a task list for the user.
     * Tasks are retrieved from the storage for the user performing the request.
     *
     * @return A list of tasks
     */
    private List<Task> retrieveTaskList() {
        List<Task> lstTasks = new LinkedList<>();
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        List<Object[]> taskList = null;
        try {
            et = em.getTransaction();
            et.begin();
            taskList = em.createNamedQuery("tasks.userAll").
                    setParameter("user", getUser()).
                    getResultList();
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to retrieve the task list");
            log.error(re);
            throw new RuntimeException("Impossible to access the task list");
        } finally {
            em.close();
        }
        if (taskList != null && !taskList.isEmpty()) {
            for (Object[] elem: taskList) {
                int idElem = 0;
                Task tmpTask = new Task();
                tmpTask.setId((String) elem[idElem++]);
                tmpTask.setDescription((String) elem[idElem++]);
                tmpTask.setStatus((Task.STATUS) elem[idElem++]);
                tmpTask.setDateCreated((Date) elem[idElem]);
                lstTasks.add(tmpTask);
            }
        }
        return lstTasks;
    }

}
