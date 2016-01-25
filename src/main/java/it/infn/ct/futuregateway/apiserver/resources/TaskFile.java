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

import it.infn.ct.futuregateway.apiserver.utils.Constants;
import java.io.Serializable;
import javax.persistence.Column;
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
 * Super class for different task files.
 * Files can be differentiate for the direction: input toward the task
 * and output from the task.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskFile implements Serializable {

    /**
     * Possible status for the input files.
     */
    public enum FILESTATUS {

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
     * URL of the file.
     * This can be used to retrieve the file from the server
     */
    private String url;

    /**
     * The status of the file.
     */
    private FILESTATUS status = FILESTATUS.NEEDED;

    /**
     * Retrieve the file identifier.
     *
     * @return The identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * Sets the identifier for the file.
     *
     * @param anId The identifier
     */
    public void setId(final Long anId) {
        this.id = anId;
    }

    /**
     * Returns the value of status.
     *
     * @return the value of status
     */
    public FILESTATUS getStatus() {
        return status;
    }

    /**
     * Sets the value of status.
     *
     * @param aStatus new value of status
     */
    public void setStatus(final FILESTATUS aStatus) {
        this.status = aStatus;
    }

    /**
     * Returns the value of name.
     *
     * @return The file name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of name.
     *
     * @param aName The name to associate with the file
     */
    public void setName(final String aName) {
        this.name = aName;
    }

    /**
     * Returns the value of url.
     *
     * @return The file url
     */
    @Column(length = Constants.LONGTEXT)
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of url.
     *
     * @param aUrl New value for the file url
     */
    public void setUrl(final String aUrl) {
        this.url = aUrl;
    }

}
