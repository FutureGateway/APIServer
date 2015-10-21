/**
 * *********************************************************************
 * Copyright (c) 2015: Istituto Nazionale di Fisica Nucleare (INFN), Italy
 * Consorzio COMETA (COMETA), Italy
 *
 * See http://www.infn.it and and http://www.consorzio-cometa.it for details on
 * the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 **********************************************************************
 */
package it.infn.ct.futuregateway.apiserver;

import it.infn.ct.futuregateway.apiserver.utils.StatusFilter;
import it.infn.ct.futuregateway.apiserver.v1.TaskService;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@ApplicationPath("/v1.0")
public class ApiServer extends Application {

    @Override
    public final Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(StatusFilter.class);
        classes.add(TaskService.class);
        return classes;
    }


}
