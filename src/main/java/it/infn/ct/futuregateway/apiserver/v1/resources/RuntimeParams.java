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

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * The class represent a generic runtime parameter.
 * Runtime parameters are associated with tasks and they differ from the normal
 * parameters because they have a timestamp.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class RuntimeParams extends Params {
    /**
     * The date when the task was created.
     */
    @XmlElement(name = "creation")
    private Date dateCreated;

    /**
     * The date of last task status update.
     */
    private Date lastChange;

        /**
     * Returns the creation time of the parameter.
     *
     * @return Creation time
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateCreated() {
        return dateCreated;
    }


    /**
     * Sets the creation time of the parameter.
     *
     * @param creationDate The creation time
     */
    public void setDateCreated(final Date creationDate) {
        this.dateCreated = creationDate;
    }


    /**
     * Returns the time of last change.
     *
     * @return The time of last status change
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getLastChange() {
        return lastChange;
    }

    /**
     * Sets the time of last status change.
     *
     * @param newChangeDate The time of last status change
     */
    public void setLastChange(final Date newChangeDate) {
        this.lastChange = newChangeDate;
    }
}
