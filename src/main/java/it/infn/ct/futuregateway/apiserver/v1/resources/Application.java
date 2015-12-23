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
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * The Application represents the operation a user can perform in a remote
 * infrastructure.
 * An application describes the activity a user task will perform in the remote
 * infrastructure associated. An application can execute in multiple
 * infrastructures but only one infrastructure is associated with a task.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQueries({
    @NamedQuery(name = "applications.all",
            query = "SELECT a FROM Application a"),
    @NamedQuery(name = "applications.forInfrastructure",
            query = "SELECT a.id FROM Application a INNER JOIN "
                    + "a.infrastructures i WHERE i.id = :infraId")
})

@Entity
@Table(name = "Application")

@InjectLinks({
    @InjectLink(value = "applications/{id}", rel = "self"),
})

@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class Application extends AccessibleElements {

    /**
     * List of references.
     */
    @InjectLinks({
        @InjectLink(value = "applications/{id}", rel = "self"),
    })
    @XmlElement(name = "_links")
    @XmlJavaTypeAdapter(value = LinkJaxbAdapter.class)
    private List<Link> links;

    /**
     * Infrastructures associated with the application.
     */
    @XmlTransient
    private List<Infrastructure> infrastructures;

    /**
     * Infrastructures associated with the application.
     */
    @XmlElement(name = "infrastructures")
    private List<String> infrastructureIds;

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


    /**
     * Retrieves the infrastructures.
     *
     * @return A list of infrastructures
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "Application_Infrastructures",
            joinColumns = {@JoinColumn(name = "applicationId",
                    referencedColumnName = "id",
                    nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "infrastructureId",
                    referencedColumnName = "id",
                    nullable = false)})
    public List<Infrastructure> getInfrastructures() {
        return infrastructures;
    }


    /**
     * Sets the infrastructures.
     * The infrastructures has to be registered before in order to associate
     * an application.
     *
     * @param someInfrastructures A list of infrastructures
     */
    public void setInfrastructures(
            final List<Infrastructure> someInfrastructures) {
        this.infrastructures = someInfrastructures;
    }


    /**
     * Retrieves the list of infrastructure identifiers.
     *
     * @return List of identifiers
     */
    @ElementCollection
    @CollectionTable(name = "Application_Infrastructures",
            joinColumns = {@JoinColumn(name = "applicationId",
                    referencedColumnName = "id",
                    nullable = false)})
    @Column(name = "infrastructureId", updatable = false, insertable = false)
    public List<String> getInfrastructureIds() {
        return infrastructureIds;
    }


    /**
     * Sets the list of infrastructures.
     *
     * @param someInfrastructureIds The list of infrastructure identifiers
     */
    public void setInfrastructureIds(final List<String> someInfrastructureIds) {
        this.infrastructureIds = someInfrastructureIds;
    }
}
