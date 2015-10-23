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

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

/**
 * Custom converter for Link JAX-RS objects.
 * This is needed for a bug raised using JAX-RS with MOXy. This
 * is well explained here <a href="http://kingsfleet.blogspot.it/2014/05/
 * reading-and-writing-jax-rs-link-objects.html">
 * http://kingsfleet.blogspot.it/2014/05/
 * reading-and-writing-jax-rs-link-objects.html</a>.
 *
 * @see <a href="https://java.net/jira/browse/JAX_RS_SPEC-446">Bug:
 * JAX_RS_SPEC-446</a>
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class LinkJaxbAdapter extends XmlAdapter<LinkJaxb, Link> {

    /**
     * Empty constructor.
     */
    public LinkJaxbAdapter() {
    }

    @Override
    public final Link unmarshal(final LinkJaxb v) throws Exception {
        Link.Builder builder = Link.fromUri(v.getUri());
        for (Map.Entry<QName, Object> entry : v.getParams().entrySet()) {
            builder.param(entry.getKey().getLocalPart(),
                    entry.getValue().toString());
        }
        return builder.build();
    }

    @Override
    public final LinkJaxb marshal(final Link v) throws Exception {
        Map<QName, Object> params = new HashMap<>();
        for (Map.Entry<String, String> entry : v.getParams().entrySet()) {
            params.put(new QName("", entry.getKey()), entry.getValue());
        }
        return new LinkJaxb(v.getUri(), params);
    }

}
