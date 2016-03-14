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

import it.infn.ct.futuregateway.apiserver.resources.Params;
import java.util.List;
import java.util.Properties;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class Utilities {

    /**
     * Avoid the class be instantiable.
     */
    private Utilities() { }


    /**
     * Retrieves the parameter value from a list.
     * If the parameter has multiple definition, and so multiple values, the
     * first occurrence is returned.
     *
     * @param params The parameter list
     * @param name Parameter name
     * @return Parameter value or null is not present
     */
    public static String getParamterValue(final List<Params> params,
            final String name) {
        Params tmpParam = IterableUtils.find(
                params, new Predicate<Params>() {
                    @Override
                    public boolean evaluate(final Params t) {
                        return t.getName().equals(name);
                    }
                });
        if (tmpParam != null) {
            return tmpParam.getValue();
        }
        return null;
    }


    /**
     * Retrieves the parameter value from a list.
     * If the parameter has multiple definition, and so multiple values, the
     * first occurrence is returned.
     *
     * @param params The parameter list
     * @param name Parameter name
     * @param defaultValue Default value if the parameter is not defined
     * @return Parameter value or null is not present
     */
    public static String getParamterValue(final List<Params> params,
            final String name, final String defaultValue) {
        Params tmpParam = IterableUtils.find(
                params, new Predicate<Params>() {
                    @Override
                    public boolean evaluate(final Params t) {
                        return t.getName().equals(name);
                    }
                });
        if (tmpParam != null) {
            return tmpParam.getValue();
        }
        return defaultValue;
    }


    /**
     * Convert a Params list in a properties object.
     * Similar to {@code onvertParamsToProperties(params, new Properties())}.
     *
     * @param params The Params to convert
     * @return A new Properties containing the parameters
     */
    public static Properties convertParamsToProperties(
            final List<Params> params) {
        return convertParamsToProperties(params, new Properties());
    }


    /**
     * Convert a Params list in a properties object.
     *
     * @param params The Params to convert
     * @param properties The Properties where the parameters will be added
     * @return The Properties containing the parameters
     */
    public static Properties convertParamsToProperties(
            final List<Params> params, final Properties properties) {
        Properties pr = properties;
        for (Params par: params) {
            Object previous = pr.setProperty(par.getName(), par.getValue());
            if (previous != null) {
                pr.setProperty(par.getName(), previous + "," + par.getValue());
            }
        }
        return pr;
    }
}
