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

import it.infn.ct.futuregateway.apiserver.inframanager.MonitorQueue;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Build the resource monitor.
 * It is a MonitorQueue which is a combination of an activity queue and a thread
 * pool.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class MonitorQueueFactory implements ObjectFactory {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(MonitorQueueFactory.class);


    @Override
    public final Object getObjectInstance(final Object obj, final Name name,
            final Context ctx, final Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference) obj;
        Enumeration<RefAddr> addrs = ref.getAll();
        int threadPoolSize = Constants.DEFAULTTHREADPOOLSIZE;
        int bufferSize = Constants.MONITORBUFFERSIZE;
        int checkInterval = Constants.MONITORCHECKINTERVAL;
        while (addrs.hasMoreElements()) {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String addrName = addr.getType();
            String addrValue = (String) addr.getContent();
            switch (addrName) {
                case "poolSize":
                    try {
                        threadPoolSize = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute poolSize format not correct."
                                + " Default value (" + threadPoolSize
                                + ") applied.");
                    }
                    break;
                case "bufferSize":
                    try {
                        bufferSize = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute bufferSize format not correct."
                                + " Default value (" + bufferSize
                                + ") applied.");
                    }
                    break;
                case "checkInterval":
                    try {
                        checkInterval = Integer.parseInt(addrValue);
                    } catch (NumberFormatException nfe) {
                        log.warn("Attribute checkInterval format not correct."
                                + " Default value (" + checkInterval
                                + ") applied.");
                    }
                default:
            }
        }
        return new MonitorQueue(bufferSize, threadPoolSize, checkInterval);
    }
}
