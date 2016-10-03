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

package it.infn.ct.futuregateway.apiserver.inframanager.gLite;

import it.infn.ct.futuregateway.apiserver.inframanager.NoResorucesAvailable;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Identify the resources accessible in the infrastructure according to the
 * request.
 * The resources are selected using the information specified in the
 * infrastructure parameters. If there are not resources specified but an
 * information system is specified then this is used and filtered accordingly.
 * Finally, if nothing is specified the information are retrieved from the
 * GOCDB for the selected VO.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class ResourceDiscovery {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(ResourceDiscovery.class);

    /**
     * Resource list for different type of resource.
     */
    private final Map<ResourceType, List<String>> resources;

    /**
     * Policy to select a resource from the list.
     */
    private final SelectPolicy resourceSelectPolicy;

    /**
     * A random generator.
     */
    private final Random rand = new Random((new Date()).getTime());

    /**
     * Available policy for resource selection.
     */
    public enum SelectPolicy {
        /**
         * Random order.
         */
        RANDOM,
        /**
         * Following the same order as specified by the user in the parameters.
         */
        ORDERED
    }

    /**
     * Possible resource types.
     */
    public enum ResourceType {
        /**
         * WMS.
         */
        WMS,
        /**
         * CE.
         */
        CE,
        /**
         * SE.
         */
        SE,
        /**
         * BDII.
         */
        BDII
    }

    /**
     * Initialise the ResourceDiscovery.
     * The ResourceDiscovery build a list of resources for the Job. These
     * are end-point to contact for different types of resources such as WMS,
     * CE, SE and other. The list can be in the parameters or has to be
     * retrieved and this is done during the initialisation phase.
     *
     * @param params Infrastructure params
     * @param vo VO name associated
     * @param resourceEndPoint Resource to force. If specified the resource will
     * not be selected from a list but this will be used
     */
    public ResourceDiscovery(final List<Params> params, final String vo,
            final String resourceEndPoint) {
        throw new UnsupportedOperationException("Not implemented");
    }


    /**
     * Return a list of end-points where jobs can be submitted.
     *
     * @param type The type of resources requested
     * @return A list of resource services
     * @throws NoResorucesAvailable If there are no resources to get
     */
    public final List<String> getJobResources(final ResourceType type)
            throws NoResorucesAvailable {
        if (resources == null || resources.isEmpty()
                || resources.get(type) == null
                || resources.get(type).isEmpty()) {
            throw new NoResorucesAvailable();
        }
        return resources.get(type);
    }

    /**
     * Return an end-point where jobs can be submitted.
     *
     * @param type The type of resource requested
     * @return A resource service
     * @throws NoResorucesAvailable If there are not anymore resources to get
     */
    public final String getJobResource(final ResourceType type)
            throws NoResorucesAvailable {
        if (resources == null || resources.isEmpty()
                || resources.get(type) == null
                || resources.get(type).isEmpty()) {
            throw new NoResorucesAvailable();
        }
        if (SelectPolicy.RANDOM.equals(resourceSelectPolicy)) {
            return resources.get(type).remove(rand.nextInt(resources.size()));
        }
        return resources.get(type).remove(0);
    }
}
