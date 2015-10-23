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

import it.infn.ct.futuregateway.apiserver.utils.annotations.Status;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The TaskService provide the REST APIs for the tasks as defined in the
 * documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("tasks")
public class TaskService {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskService.class);

    /**
     * Used to retrieve the JPA EntityManager.
     */
    @Context
    private HttpServletRequest request;

    /**
     * Used to customise the response header.
     */
    @Context
    private HttpServletResponse response;

    /**
     * Retrieve the list of tasks.
     *
     * The list includes only the tasks associated to the user.
     *
     * @return The json
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final List<Task> listTasks() {
        List<Task> tasks = new LinkedList<>();
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        List<Object[]> taskList = null;
        try {
            et = em.getTransaction();
            et.begin();
            taskList = em.createNamedQuery("findTasks").
                    setParameter("user", getUser()).
                    getResultList();
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            response.setStatus(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            log.error("Impossible to retrieve the task list");
            log.error(re);
            throw new RuntimeException("Impossible to access the task list");
        }
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        for (Object[] elem: taskList) {
            int idElem = 0;
            Task tmpTask = new Task();
            tmpTask.setId((Long) elem[idElem++]);
            tmpTask.setDescription((String) elem[idElem++]);
            tmpTask.setStatus((Task.STATUS) elem[idElem++]);
            tmpTask.setDate((Date) elem[idElem]);
            tasks.add(tmpTask);
        }
        URI self = UriBuilder.fromUri(request.getServletPath()).
                path(getClass()).build();

        Link link = Link.fromUri(self).rel("self").build();
        response.setHeader("Link", link.toString());
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Task createTask(final Task task) {
        Date now = new Date();
        task.setDate(now);
        task.setLastChange(now);
        task.setStatus(Task.STATUS.WAITING);
        task.setUser(getUser());
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.persist(task);
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to create the task: " + task);
            log.error(re);
        }
        return task;
    }

    /**
     * Return the EntityManager.
     * Create a JPA EntityManger from the EntityMangerFactory registered
     * for this servlet context
     *
     * @return The EntityManager
     */
    private EntityManager getEntityManager() {
        EntityManagerFactory emf =
                (EntityManagerFactory) request.getServletContext().
                        getAttribute("SessionFactory");
        return emf.createEntityManager();
    }

    /**
     * Retrieve the user performing the request.
     * The user name is extrapolated from the authorisation token.
     *
     * @return The user name
     */
    private String getUser() {
        log.error("This method is not yet implemented");
        return "pippo";
    }
    /**
     * Create the dir to store the input.
     * Create a directory inside the temporary store with path
     * "inputs/<taskId>". This is used to store the input file before the
     * submission.
     *
     * @param taskId The ID of the task to associate the files
     */
    private void createInputDir(final String taskId) {
    }
}
