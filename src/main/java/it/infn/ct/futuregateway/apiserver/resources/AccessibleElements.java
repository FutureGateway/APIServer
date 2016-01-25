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

package it.infn.ct.futuregateway.apiserver.resources;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The super class of all the elements accessible by the user.
 * These include applications and infrastructures.
 * <p>
 * The AccessibleElements object persists information shared among different
 * entities such as id, name, parameters and others.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AccessibleElements implements Serializable {

    /**
     * The identifier of the element.
     */
    @XmlElement(name = "id")
    private String id;
    /**
     * The id of the element associated with the element.
     */
    private String name;
    /**
     * A user provided description of the element.
     */
    private String description;
    /**
     * A list of parameters for the element.
     */
    private List<Params> parameters;
    /**
     * The date when the element was created.
     */
    @XmlElement(name = "date")
    private Date dateCreated;
    /**
     * The status of the element. If false the element cannot be
     * associated with an element.
     */
    private boolean enabled = true;

    /**
     * Retrieve the element identifier.
     *
     * @return The identifier of this element
     */
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Sets the element identifier.
     *
     * @param anId The element identifier
     */
    public void setId(final String anId) {
        this.id = anId;
    }

    /**
     * Returns user defined element name.
     * Application name is not unique. Two different elements could have the
     * same name is they perform the same operation for the user but differ for
     * some parameters or infrastructures.
     *
     * @return The element name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a user readable name for the element.
     * Application name is not unique. Two different elements could have the
     * same name is they perform the same operation for the user but differ for
     * some parameters or infrastructures.
     *
     * @param aName The name to assign to the element
     */
    public void setName(final String aName) {
        this.name = aName;
    }

    /**
     * Returns the element description.
     *
     * @return The element description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets an element description for the users.
     *
     * @param aDescription The description
     */
    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    /**
     * Retrieves the element parameters.
     *
     * @return A list of parameters
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    public List<Params> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters for the element.
     * @param someParameters A list of parameters
     */
    public void setParameters(final List<Params> someParameters) {
        this.parameters = someParameters;
    }

    /**
     * Returns the creation time of the element.
     *
     * @return Creation time
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the creation time of the element.
     *
     * @param creationDate The creation time
     */
    public void setDateCreated(final Date creationDate) {
        this.dateCreated = creationDate;
    }

    /**
     * Returns the element status.
     * Elements can be enabled or disabled and if disabled the activity
     * depending on the element will wait in order to move on.
     *
     * @return The status
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the element status.
     * Elements can be enabled or disabled and if disabled the activity
     * depending on the element will wait in order to move on.
     *
     * @param active The status
     */
    public void setEnabled(final boolean active) {
        this.enabled = active;
    }

}
