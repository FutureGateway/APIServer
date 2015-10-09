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

import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("tasks")
public class TaskService {

    /**
     * Used to retrieve the JPA EntityManager.
     */
    @Context
    private HttpServletRequest httpRequest;

    /**
     *
     * @return The json
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final String listTasks() {
        EntityManager em = getEntityManager();
        return "{\"message\"=\"Hello, tasks!\"}";
    }

    /**
     * Register a new task.
     *
     * @param task The task to register
     * @return The task registered
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Task createTask(final Task task) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(task);
        em.getTransaction().commit();
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
                (EntityManagerFactory) httpRequest.getServletContext().
                        getAttribute("SessionFactory");
        return emf.createEntityManager();
    }
}
