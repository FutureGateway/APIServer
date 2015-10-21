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

package it.infn.ct.futuregateway.apiserver.utils;

import it.infn.ct.futuregateway.apiserver.utils.annotations.Status;
import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Fix the status when differ from JAX-RS default.
 * The methods returning a different status should be annotated
 * with @Status(<status_value>)
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Status
@Provider
public class StatusFilter implements ContainerResponseFilter {

    @Override
    public final void filter(final ContainerRequestContext cReqC,
            final ContainerResponseContext cRespC) throws IOException {
        if (cRespC.getStatus() == Response.Status.OK.getStatusCode()) {
            for (Annotation annotation: cRespC.getEntityAnnotations()) {
                if (annotation instanceof Status) {
                    Response.Status reqStatus = ((Status) annotation).value();
                    cRespC.setStatus(reqStatus.getStatusCode());
                    break;
                }
            }
        }
    }

}
