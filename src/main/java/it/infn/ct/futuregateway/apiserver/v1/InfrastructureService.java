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
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
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
 * The InfrastructureService provides the REST APIs for the infrastructure as
 * defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/infrastructures/{id}")
public class InfrastructureService extends BaseService {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(InfrastructureService.class);


    /**
     * Retrieve the infrastructure details.
     * Infrastructure details include all the information needed to access the
     * remote infrastructure.
     *
     * @param id The infrastructure id. This is a path parameter retrieved from
     * the URL
     * @return The infrastructure
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final Infrastructure getInfraDetails(
            @PathParam("id") final String id) {
        Infrastructure infra;
        EntityManager em = getEntityManager();
        try {
            infra = em.find(Infrastructure.class, id);
        } catch (IllegalArgumentException re) {
            log.error("Impossible to retrieve the application");
            log.error(re);
            throw new RuntimeException("Impossible to access the application "
                    + "list");
        } finally {
            em.close();
        }
        if (infra == null) {
            throw new NotFoundException();
        } else {
            return infra;
        }
    }


    /**
     * Removes the infrastructure. Delete the infrastructure only if there are
     * not applications associated with it to avoid inconsistency in the DB.
     * <p>
     * Infrastructures with associated applications can only be disabled to
     * avoid future execution of applications.
     *
     * @param id Id of the infrastructure to remove
     */
    @DELETE
    public final void deleteInfra(@PathParam("id") final String id) {
        Infrastructure infra;
        EntityManager em = getEntityManager();
        try {
            infra = em.find(Infrastructure.class, id);
            if (infra == null) {
                throw new NotFoundException();
            }
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                List<Object[]> appsForInfra = em.
                        createNamedQuery("applications.forInfrastructure").
                        setParameter("infraId", id).
                        setMaxResults(1).
                        getResultList();
                if (appsForInfra == null || appsForInfra.isEmpty()) {
                    em.remove(infra);
                } else {
                    throw new WebApplicationException("The infrastructure "
                            + "cannot be removed because there are associated "
                            + "applications",
                            Response.Status.CONFLICT);
                }
                et.commit();
            } catch (WebApplicationException wex) {
                throw wex;
            } catch (RuntimeException re) {
                log.error(re);
                log.error("Impossible to remove the infrastructure");
                throw new InternalServerErrorException("Errore to remove "
                        + "the infrastructure " + id);
            } finally {
                if (et != null && et.isActive()) {
                    et.rollback();
                }
            }
        } catch (IllegalArgumentException re) {
            log.error("Impossible to retrieve the infrastructure list");
            log.error(re);
            throw new BadRequestException("Task '" + id + "' does not exist!");
        } finally {
            em.close();
        }
    }
}
