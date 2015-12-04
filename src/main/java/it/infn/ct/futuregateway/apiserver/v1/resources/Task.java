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

import it.infn.ct.futuregateway.apiserver.utils.LinkJaxbAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * The Task represents any activity a user send to an infrastructure, such as a
 * run a job in a grid computing site or deploy a VM in a cloud.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQuery(name = "findTasks",
        query = "Select t.id, t.description, t.status, t.dateCreated"
        + " from Task t where t.userName = :user")
@Entity
@Table(name = "Task")

@InjectLinks({
    @InjectLink(value = "tasks/{id}", rel = "self"),
    @InjectLink(value = "tasks/{id}/input", rel = "input")
})

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends Observable implements Serializable {

    /**
     * Possible status for the task.
     */
    public enum STATUS {

        /**
         * Task created but input still required.
         */
        WAITING,
        /**
         * Task ready to be scheduled to the infrastructure.
         */
        READY,
        /**
         * Task ready to execute in the selected infrastructure.
         */
        SCHEDULED,
        /**
         * In execution.
         */
        RUNNING,
        /**
         * Task completed.
         */
        DONE,
        /**
         * Some error prevent the task from the execution.
         */
        ABORTED,
        /**
         * Task deleted by the user.
         */
        CANCELLED
    };

    /**
     * List of references.
     */
    @InjectLinks({
        @InjectLink(value = "tasks/{id}", rel = "self"),
        @InjectLink(value = "tasks/{id}/input", rel = "input")
    })
    @XmlElement(name = "_links")
    @XmlJavaTypeAdapter(value = LinkJaxbAdapter.class)
    private List<Link> links;

    /**
     * The identifier of the task.
     */
    @XmlElement(name = "id")
    private String id;

    /**
     * The id of the application associated with the task.
     */
    private String application;

    /**
     * A user provided description of the task.
     */
    private String description;

    /**
     * Arguments to provide to the application.
     */
    private List<String> arguments;

    /**
     * Arguments to provide to the application.
     */
    @XmlElement(name = "output_files")
    private List<TaskFileOutput> outputFiles;

    /**
     * Input file for the application.
     */
    @XmlElement(name = "input_files")
    private List<TaskFileInput> inputFiles;

    /**
     * The current status of the task.
     */
    private STATUS status;

    /**
     * The user name submitting the task.
     */
    @XmlElement(name = "user")
    private String userName;

    /**
     * The date when the task was created.
     */
    @XmlElement(name = "date")
    private Date dateCreated;

    /**
     * The date of last task status update.
     */
    private Date lastChange;

    /**
     * Retrieve the task identifier.
     *
     * @return The identifier of this task
     */
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Set the task identifier.
     *
     * @param anId The task identifier
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
     * Get the id of the associated application.
     *
     * @return The id of the application associated with the task
     */
    public String getApplication() {
        return application;
    }

    /**
     * Set the application to associate with the task.
     *
     * @param anApplication The application identifier
     */
    public void setApplication(final String anApplication) {
        this.application = anApplication;
    }

    /**
     * Get the user description of the task.
     *
     * @return The user description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set a description for the task.
     *
     * @param aDescription Task description
     */
    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    /**
     * Retrieve the list of arguments for the application. This is a list of
     * string the system will use as parameter for the application. Actually,
     * the list will be converted in a space separated string maintaining the
     * order of the list.
     * <p>
     * The use of the list will depend on the application. In case of an
     * executable the list will be appended to the command line but for a
     * service there is not a standard use at the moment.
     *
     * @return The list of arguments
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_arguments",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "arguments")
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Set a list of arguments for the application. This is a list of string the
     * system will use as parameter for the application. Actually, the list will
     * be converted in a space separated string maintaining the order of the
     * list.
     * <p>
     * The use of the list will depend on the application. In case of an
     * executable the list will be appended to the command line but for a
     * service there is not a standard use at the moment.
     *
     * @param someArguments The arguments to provide to the application
     */
    public void setArguments(final List<String> someArguments) {
        this.arguments = someArguments;
    }

    /**
     * Retrieve the list of output files of the application. This is the list of
     * files the system has to retrieve after the application or the service has
     * completed its execution.
     *
     * @return The output files
     */
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    public List<TaskFileOutput> getOutputFiles() {
        return this.outputFiles;
    }

    /**
     * Set the list of output files of the application. This is the list of
     * files the system has to retrieve after the application or the service has
     * completed its execution.
     *
     * @param someOutputFiles A list with the output files
     */
    public void setOutputFiles(
            final List<TaskFileOutput> someOutputFiles) {
        this.outputFiles = someOutputFiles;
    }

    /**
     * Retrieve the input files for the application. This is a map of files sent
     * to the remote infrastructure for the execution of the application and/or
     * service. The map key is the file name and the map value is an URL
     * locating the file.
     * <p>
     * The URL can be local or remote to the service.
     *
     * @return The input files
     */
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    public List<TaskFileInput> getInputFiles() {
        return this.inputFiles;
    }

    /**
     * Set the input files for the application. This is a map of files sent to
     * the remote infrastructure for the execution of the application and/or
     * service. The map key is the file name and the map value is an URL
     * locating the file.
     * <p>
     * The URL can be local or remote to the service.
     *
     * @param someInputFiles A map with the input files.
     */
    public void setInputFiles(final List<TaskFileInput> someInputFiles) {
        this.inputFiles = someInputFiles;
    }

    /**
     * Get the status of the task.
     *
     * @return The status.
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    @Enumerated(EnumType.STRING)
    public STATUS getStatus() {
        return status;
    }

    /**
     * Set the status for the task.
     *
     * @param aStatus The status to associate with the task
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    public void setStatus(final STATUS aStatus) {
        if (aStatus.equals(this.status)) {
            setChanged();
        }
        this.status = aStatus;
        notifyObservers();
    }

    /**
     * Get the user identifier. Every task is associated to a user. The
     * identifier is provided by an external entity (e.g. a Web Application) and
     * can be of any kind but has to be unique for the user.
     *
     * @return The user identifier
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user identifier. Every task is associated to a user. The
     * identifier is provided by an external entity (e.g. a Web Application) and
     * can be of any kind but has to be unique for the user.
     *
     * @param aUser The user identifier
     */
    public void setUserName(final String aUser) {
        this.userName = aUser;
    }

    /**
     * Get the creation time of the task.
     *
     * @return Creation time
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Set the creation time of the task.
     *
     * @param creationDate The creation time
     */
    public void setDateCreated(final Date creationDate) {
        this.dateCreated = creationDate;
    }

    /**
     * Get the time of last status change.
     *
     * @return The time of last status change
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getLastChange() {
        return lastChange;
    }

    /**
     * Set the time of last status change.
     *
     * @param newChangeDate The time of last status change
     */
    public void setLastChange(final Date newChangeDate) {
        this.lastChange = newChangeDate;
    }

    /**
     * Get the references for this entity.
     *
     * @return The list of Link references
     */
    @Transient
    public List<Link> getLinks() {
        return links;
    }

    /**
     * Set the references for this entity.
     *
     * @param someLinks The list of link references
     */
    public void setLinks(final List<Link> someLinks) {
        this.links = someLinks;
    }

    /**
     * Update the status of input files.
     * Change the status value for the input file specified. If the name is not
     * associated with any input file nothing happen.
     * <p>
     * There is not check on the file existence and real status which
     * is delegated to the caller object.
     *
     * @param name File name
     * @param aStatus New status
     */
    public final void updateInputFileStatus(
            final String name, final TaskFile.FILESTATUS aStatus) {

        TaskFileInput tfi = IterableUtils.find(inputFiles,
                new Predicate<TaskFileInput>() {
            @Override
            public boolean evaluate(final TaskFileInput t) {
                return t.getName().equals(name);
            }
        });
        if (tfi != null) {
            tfi.setStatus(aStatus);
            setChanged();
            notifyObservers();
        }
    }
}
