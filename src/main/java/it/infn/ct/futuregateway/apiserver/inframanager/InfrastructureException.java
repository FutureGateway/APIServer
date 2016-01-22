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

package it.infn.ct.futuregateway.apiserver.inframanager;

/**
 * Problem with the defined infrastructure.
 * This exception group all possible errors connected with the infrastructure
 * access and management.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class InfrastructureException extends Exception {

    /**
     * Creates a new instance of <code>InfrastrucureException</code> without
     * detail message.
     */
    public InfrastructureException() {
    }

    /**
     * Constructs an instance of <code>InfrastrucureException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InfrastructureException(final String msg) {
        super(msg);
    }
}
