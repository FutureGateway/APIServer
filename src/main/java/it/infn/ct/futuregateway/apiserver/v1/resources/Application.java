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
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
 * The Application represents the operation a user can perform in a remote
 * infrastructure.
 * An application describe the what a user task perform in the remote
 * infrastructure associated. An application can execute in multiple
 * infrastructures but only one infrastructure is associated with a task.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQuery(name = "applications.all",
        query = "SELECT a FROM Application a")

@Entity
@Table(name = "Application")

@InjectLinks({
    @InjectLink(value = "applications/{id}", rel = "self"),
})

@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class Application implements Serializable {

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
     * The identifier of the application.
     */
    @XmlElement(name = "id")
    private String id;

    /**
     * The id of the application associated with the task.
     */
    private String name;

    /**
     * A user provided description of the task.
     */
    private String description;

    /**
     * A list of parameters for the application.
     */
    private List<Params> parameters;
    /**
     * Arguments to provide to the application.
     */
    private List<String> infrastructures;

    /**
     * The date when the application was created.
     */
    @XmlElement(name = "date")
    private Date dateCreated;

    /**
     * The status of the application. If false the application cannot be
     * associated with a task.
     */
    private boolean enabled = true;

    /**
     * Retrieve the application identifier.
     *
     * @return The identifier of this application
     */
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Sets the application identifier.
     *
     * @param anId The task application
     */
    public void setId(final String anId) {
        this.id = anId;
    }

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
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    /**
     * Returns user defined application name.
     * Application name is not unique. Two different applications could have the
     * same name is they perform the same operation for the user but differ for
     * some parameters or infrastructures.
     *
     * @return The application name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets a user readable name for the application.
     * Application name is not unique. Two different applications could have the
     * same name is they perform the same operation for the user but differ for
     * some parameters or infrastructures.
     *
     * @param aName The name to assign to the application
     */
    public void setName(final String aName) {
        this.name = aName;
    }


    /**
     * Returns the application description.
     *
     * @return The application description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets an application description for the users.
     *
     * @param aDescription The description
     */
    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    /**
     * Retrieves the application parameters.
     *
     * @return A list of parameters
     */
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    public List<Params> getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters for the application.
     * @param someParameters A list of parameters
     */
    public void setParameters(final List<Params> someParameters) {
        this.parameters = someParameters;
    }


    /**
     * Returns the infrastructures.
     *
     * @return A list of infrastructures
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_infrastructures",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "infrastructures")
    public List<String> getInfrastructures() {
        return infrastructures;
    }


    /**
     * Sets the infrastructures.
     * The infrastructures has to be registered before in order to associate
     * an application.
     *
     * @param someInfrastructures A list of infrastructures
     */
    public void setInfrastructures(final List<String> someInfrastructures) {
        this.infrastructures = someInfrastructures;
    }


    /**
     * Returns the creation time of the application.
     *
     * @return Creation time
     */
    public Date getDateCreated() {
        return dateCreated;
    }


    /**
     * Sets the creation time of the task.
     *
     * @param creationDate The creation time
     */
    public void setDateCreated(final Date creationDate) {
        this.dateCreated = creationDate;
    }


    /**
     * Returns the application status.
     * Application can be enabled or disabled and if disabled tasks can be
     * created but they will not execute.
     *
     * @return The status
     */
    public boolean isEnabled() {
        return enabled;
    }


    /**
     * Returns the application status.
     * Application can be enabled or disabled and if disabled tasks can be
     * created but they will not execute.
     *
     * @param active The status
     */
    public void setEnabled(final boolean active) {
        this.enabled = active;
    }


}
