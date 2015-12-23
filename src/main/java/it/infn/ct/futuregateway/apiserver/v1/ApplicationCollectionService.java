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
import it.infn.ct.futuregateway.apiserver.v1.resources.ApplicationList;
import it.infn.ct.futuregateway.apiserver.v1.resources.Infrastructure;
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
 * The ApplicationCollectionService provide the REST APIs for the application
 * collection as defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/application-collection
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/applications")
public class ApplicationCollectionService extends BaseService {

    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(
            ApplicationCollectionService.class);


    /**
     * Retrieves the list of applications.
     *
     * @return The application collection
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final ApplicationList listApplications() {
        ApplicationList apps;
        try {
            apps = new ApplicationList(retrieveApplicationList());
        } catch (RuntimeException re) {
            getResponse().setStatus(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            throw re;
        }
        return apps;
    }


    /**
     * Register a new application.
     *
     * @param application The application to register
     * @return The registered application
     */
    @POST
    @Status(Response.Status.CREATED)
    @Consumes({MediaType.APPLICATION_JSON, Constants.INDIGOMIMETYPE})
    @Produces(Constants.INDIGOMIMETYPE)
    public final Application createApplication(final Application application) {
        if (application.getInfrastructureIds() == null) {
            throw new BadRequestException();
        }
        Date now = new Date();
        application.setDateCreated(now);
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            List<Infrastructure> lstInfra = new LinkedList<>();
            for (String infraId: application.getInfrastructureIds()) {
                Infrastructure infra = em.find(Infrastructure.class,
                        infraId);
                if (infra != null) {
                    lstInfra.add(infra);
                }
            }
            if (!lstInfra.isEmpty()) {
                application.setInfrastructures(lstInfra);
                em.persist(application);
            } else {
                throw new BadRequestException();
            }
            et.commit();
            log.debug("New application registered: " + application.getId());
        } catch (BadRequestException re) {
            throw re;
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to create the application");
            log.error(re);
            throw re;
        } finally {
            em.close();
        }
        if (application.getInfrastructures() == null
                || application.getInfrastructures().isEmpty()) {
            throw new BadRequestException();
        }
        return application;
    }


    /**
     * Retrieve the application list.
     * The fields not requested for the list are cleaned.
     * @return The list of applications
     */
    private List<Application> retrieveApplicationList() {
        List<Application> lstApps;
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            lstApps = em.createNamedQuery("applications.all",
                    Application.class).
                    getResultList();
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to retrieve the Application list");
            log.error(re);
            throw new RuntimeException("Impossible to access the "
                    + "application list");
        } finally {
            em.close();
        }
        if (lstApps != null) {
            for (Application ap: lstApps) {
                ap.setDescription(null);
                ap.setParameters(null);
            }
        }
        return lstApps;
    }
}
