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
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.InfrastructureList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
 * The InfrastructureCollectionService provide the REST APIs for the
 * infrastructure collection as defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/application-collection
 */
@Path("/infrastructures")
public class InfrastructureCollectionService extends BaseService {

    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(
            InfrastructureCollectionService.class);


    /**
     * Retrieves the list of infrastructures.
     *
     * @return The infrastructure collection
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final InfrastructureList listInfrastructures() {
        InfrastructureList infra;
        try {
            infra = new InfrastructureList(retrieveInfrastructureList());
        } catch (RuntimeException re) {
            getResponse().setStatus(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            throw re;
        }
        return infra;
    }


    /**
     * Register a new infrastructure.
     *
     * @param infra The infrastructure to register
     * @return The registered infrastructure
     */
    @POST
    @Status(Response.Status.CREATED)
    @Consumes({MediaType.APPLICATION_JSON, Constants.INDIGOMIMETYPE})
    @Produces(Constants.INDIGOMIMETYPE)
    public final Infrastructure createInfrastructure(
            final Infrastructure infra) {
        Date now = new Date();
        infra.setDateCreated(now);
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.persist(infra);
            et.commit();
            log.debug("New infrastructure registered: " + infra.getId());
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to create the infrastructure");
            log.error(re);
        } finally {
            em.close();
        }
        return infra;
    }


    /**
     * Retrieve the application list.
     * The fields not requested for the list are cleaned.
     * @return The list of applications
     */
    private List<Infrastructure> retrieveInfrastructureList() {
        List<Infrastructure> lstInfras;
        EntityManager em = getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            lstInfras = em.createNamedQuery("infrastructures.all",
                    Infrastructure.class).
                    getResultList();
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to retrieve the infrastructure list");
            log.error(re);
            throw new RuntimeException("Impossible to access the "
                    + "infrastructures list");
        } finally {
            em.close();
        }
        if (lstInfras != null) {
            for (Infrastructure in: lstInfras) {
                in.setDescription(null);
                in.setParameters(null);
            }
        }
        return lstInfras;
    }
}
