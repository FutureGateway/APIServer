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
import it.infn.ct.futuregateway.apiserver.resources.Application;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ApplicationService provides the REST APIs for the application as defined
 * in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/applications/{id}")
public class ApplicationService extends BaseService {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(ApplicationService.class);


    /**
     * Retrieves the application details.
     * Application details include all the fields an application
     * consist of as described in the documentation. This include all the
     * information included in the task collection and many others.
     *
     * @param id The application id. This is a path parameter retrieved from
     * the URL
     * @return The application
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final Application getAppDetails(@PathParam("id") final String id) {
        Application app;
        EntityManager em = getEntityManager();
        try {
            app = em.find(Application.class, id);
            log.debug("Find the application " + id + " associated with "
                    + app.getInfrastructureIds().size() + " infrastructures "
                    + "and " + app.getParameters().size() + " parameters");
        } catch (IllegalArgumentException re) {
            log.error("Impossible to retrieve the application");
            log.error(re);
            throw new RuntimeException("Impossible to access the application "
                    + "list");
        } finally {
            em.close();
        }
        if (app == null) {
            throw new NotFoundException();
        } else {
            return app;
        }
    }


    /**
     * Removes the application. Delete the application only if there are not
     * tasks associated with it because tasks must be associated with an
     * application.
     * <p>
     * Applications with associated tasks can only be disabled to avoid future
     * execution of new tasks. Nevertheless, a task can be associated with a
     * disabled application and in this case will stay waiting until the
     * application is enabled.
     *
     * @param id Id of the application to remove
     */
    @DELETE
    public final void deleteApp(@PathParam("id") final String id) {
        Application app;
        EntityManager em = getEntityManager();
        try {
            app = em.find(Application.class, id);
            if (app == null) {
                throw new NotFoundException();
            }
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                List<Object[]> taskForApp = em.
                        createNamedQuery("tasks.forApplication").
                        setParameter("appId", id).
                        setMaxResults(1).
                        getResultList();
                if (taskForApp == null || taskForApp.isEmpty()) {
                    em.remove(app);
                } else {
                    log.info("Application " + id + " has tasks and cannot be"
                            + " deleted");
                    throw new WebApplicationException("The application cannot "
                            + "be removed because there are associated tasks",
                            Response.Status.CONFLICT);
                }
                et.commit();
            } catch (WebApplicationException wex) {
                throw wex;
            } catch (RuntimeException re) {
                log.error(re);
                log.error("Impossible to remove the application");
                throw new InternalServerErrorException("Error to remove "
                        + "the application " + id);
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } catch (IllegalArgumentException re) {
            log.error("Impossible to retrieve the application list");
            log.error(re);
            throw new BadRequestException("Application '" + id + "' "
                    + "does not exist!");
        } finally {
            em.close();
        }
    }
}
