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
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class Defaults {

    /**
     * Proxy renewal option.
     */
    public static final String PROXYRENEWAL = "false";

    /**
     * VOMS proxy option.
     */
    public static final String DISABLEVOMSPROXY = "false";

    /**
     * RFC proxy option.
     */
    public static final String RFCPROXY = "true";

    /**
     * Avoid the class be instantiable.
     */
    private Defaults() { }
}
