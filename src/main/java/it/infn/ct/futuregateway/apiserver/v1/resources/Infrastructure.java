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

package it.infn.ct.futuregateway.apiserver.v1.resources;

import it.infn.ct.futuregateway.apiserver.utils.LinkJaxbAdapter;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * The Infrastructure represents the remote infrastructure where application
 * can execute.
 * An infrastructure describes all the relevant information to allow the access
 * and execution of applications and services in a remote infrastructure.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQuery(name = "infrastructures.all",
        query = "SELECT i FROM Infrastructure i")

@Entity
@Table(name = "Infrastructure")

@InjectLinks({
    @InjectLink(value = "infrastructures/{id}", rel = "self"),
})

@XmlRootElement(name = "infrastructure")
@XmlAccessorType(XmlAccessType.FIELD)
public class Infrastructure extends AccessibleElements {

    /**
     * List of references.
     */
    @InjectLinks(value = {
        @InjectLink(value = "infrastructures/{id}", rel = "self")})
    @XmlElement(name = "_links")
    @XmlJavaTypeAdapter(value = LinkJaxbAdapter.class)
    private List<Link> links;


    /**
     * Initialise the id.
     *
     * The id for the task is generated with a random uuid. There is not a
     * collision control at this level. If the persistence failed because of
     * this Id is repeated the code at higher level should manage the situation
     * by replacing the Id.
     */
    @PrePersist
    private void generateId() {
        if (this.getId() == null || this.getId().isEmpty()) {
            this.setId(UUID.randomUUID().toString());
        }
    }

}
