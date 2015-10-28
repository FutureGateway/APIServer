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
package it.infn.ct.futuregateway.apiserver.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

/**
 * Custom converter for Link JAX-RS objects.
 * This is needed for a bug raised using JAX-RS with MOXy. This
 * is well explained here <a href="http://kingsfleet.blogspot.it/2014/05/
 * reading-and-writing-jax-rs-link-objects.html">
 * http://kingsfleet.blogspot.it/2014/05/
 * reading-and-writing-jax-rs-link-objects.html</a>.
 *
 * The class represent the Link in a JAXB compatible format so conversion is
 * possible for MOXy engine.
 *
 * @see <a href="https://java.net/jira/browse/JAX_RS_SPEC-446">Bug:
 * JAX_RS_SPEC-446</a>
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class LinkJaxb {

    /**
     * Uri.
     */
    private URI uri;

    /**
     * Param.
     */
    private Map<QName, Object> params;

    /**
     * Ciccio.
     */
    LinkJaxb() {
        this(null, null);
    }

    /**
     * Ciccio.
     *
     * @param aUri Ciccio.
     */
    LinkJaxb(final URI aUri) {
        this(aUri, null);
    }

    /**
     * Ciccio.
     *
     * @param map Ciccio.
     * @param aUri Ciccio.
     */
    public LinkJaxb(final URI aUri, final Map<QName, Object> map) {
        this.uri = aUri;
        if (map != null) {
            this.params = map;
        } else {
            this.params = new HashMap<QName, Object>();
        }

    }

    /**
     * Ciccio.
     *
     * @return Ciccio.
     */
    @XmlAttribute(name = "href")
    public final URI getUri() {
        return uri;
    }

    /**
     * Ciccio.
     *
     * @return Ciccio.
     */
    @XmlAnyAttribute
    public final Map<QName, Object> getParams() {
        return params;
    }

    /**
     * Ciccio.
     *
     * @param aUri Ciccio.
     */
    public final void setUri(final URI aUri) {
        this.uri = aUri;
    }

    /**
     * Ciccio.
     *
     * @param someParams Ciccio.
     */
    public final void setParams(final Map<QName, Object> someParams) {
        this.params = someParams;
    }
}
