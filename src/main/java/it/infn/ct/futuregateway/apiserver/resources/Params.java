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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The class represent a generic parameter.
 * Params can be associated with applications and infrastructures.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Params implements Serializable {

    /**
     * The parameter id.
     */
    @XmlTransient
    private Long id;

    /**
     * Parameter name.
     */
    private String name;

    /**
     * Parameter value.
     */
    private String value;

    /**
     * Parameter description.
     */
    private String description;


    /**
     * Retrieves the name of the parameter.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of the parameter.
     *
     * @param aName The name
     */
    public void setName(final String aName) {
        this.name = aName;
    }

    /**
     * Retrieves the value of the parameter.
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }


    /**
     * Sets the value of the parameter.
     * @param aValue The value
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }


    /**
     * Retrieves the description of the parameter.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description of the parameter.
     *
     * @param aDescription The description
     */
    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }


    /**
     * Retrieves the id of the parameter.
     *
     * @return The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }


    /**
     * Sets the id of the parameter.
     * @param anId The id
     */
    public void setId(final Long anId) {
        this.id = anId;
    }



    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Params)) {
            return false;
        }
        Params tmpParam = (Params) obj;
        return this.name.equals(tmpParam.name)
                && this.value.equals(tmpParam.value);
    }



    @Override
    public final int hashCode() {
        return name.concat(value).hashCode();
    }
}
