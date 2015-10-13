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
package it.infn.ct.futuregateway.apiserver.v1.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "inputFiles")
public class InputFile {

    /**
     * Possible status for the input files.
     */
    public enum INPUTSTATUS {

        /**
         * Input file does not provided.
         */
        NEEDED,
        /**
         * Input file provided.
         */
        READY
    }

    /**
     * File identifier.
     */
    @XmlTransient
    private Long id;

    /**
     * File name.
     * The file name does not include the path with is relative to store
     * directory and the task folder
     */
    private String name;

    /**
     * The status of the task.
     */
    private INPUTSTATUS status;

    /**
     * Retrieve the file identifier.
     *
     * @return The identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public final Long getId() {
        return id;
    }

    /**
     * Set the identifier for the file.
     *
     * @param anId The identifier
     */
    public final void setId(final Long anId) {
        this.id = anId;
    }

    /**
     * Get the value of status.
     *
     * @return the value of status
     */
    public final INPUTSTATUS getStatus() {
        return status;
    }

    /**
     * Set the value of status.
     *
     * @param aStatus new value of status
     */
    public final void setStatus(final INPUTSTATUS aStatus) {
        this.status = aStatus;
    }

    /**
     * Get the value of name.
     *
     * @return the value of name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param aName new value of name
     */
    public final void setName(final String aName) {
        this.name = aName;
    }

}
