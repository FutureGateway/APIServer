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

import it.infn.ct.futuregateway.apiserver.filter.StatusFilter;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@ApplicationPath("/v1.0")
public class ApiServer extends ResourceConfig {

    /**
     * Register resources.
     *
     * @param context The servlet context to access configuration values
     */
    public ApiServer(@Context final ServletContext context) {
        Log log = LogFactory.getLog(ApiServer.class);
        packages("it.infn.ct.futuregateway.apiserver.v1");
        register(StatusFilter.class);
        register(DeclarativeLinkingFeature.class);
        register(MultiPartFeature.class);
        String aFilter = (String) context.getAttribute("AuthFilters");
        try {
            register(Class.forName(aFilter.trim()));
            log.info("Registerd the authentication filter: " + aFilter);
        } catch (ClassNotFoundException ex) {
            log.error("The filter " + aFilter + " cannot be registered");
        }
    }
}
