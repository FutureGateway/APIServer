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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public abstract class BaseService {

    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(BaseService.class);

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
     * Return the EntityManager.
     * Create a JPA EntityManger from the EntityMangerFactory registered
     * for this servlet context
     *
     * @return The EntityManager
     */
    protected final EntityManager getEntityManager() {
        EntityManagerFactory emf = (EntityManagerFactory)
                getRequest().getServletContext().getAttribute("SessionFactory");
        return emf.createEntityManager();
    }

    /**
     * Retrieve the user performing the request.
     * The user name is extrapolated from the authorisation token.
     *
     * @return The user name
     */
    protected final String getUser() {
        log.error("This method is not yet implemented");
        return "pippo";
    }

    /**
     * Retrieve the request.
     * The request associated with the service executed in the current call.
     *
     * @return The request
     */
    protected final HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Retrieve the response.
     * The response where output and header can be written.
     *
     * @return The response
     */
    protected final HttpServletResponse getResponse() {
        return response;
    }
}
