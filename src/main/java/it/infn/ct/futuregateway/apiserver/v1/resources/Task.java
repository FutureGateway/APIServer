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

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Task represents  any activity a user send to an infrastructure, such as
 * a run a job in a grid computing site or deploy a VM in a cloud.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQuery(name = "findTasks", query = "Select task from Task task")
@XmlRootElement
@Entity
@Table(name = "Task")
public class Task {

    /**
     * Possible status for the task.
     */
    public enum STATUS {

        /**
         * Task ready to execute in the selected infrastructure.
         */
        SCHEDULED,
        /**
         * Task created but input still required.
         */
        WAITING,
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
     * The identifier of the task.
     */
    private Long id;

    /**
     * The id of the application associated with the task.
     */
    private Long application;

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
    private List<String> outputFiles;

    /**
     * Input file for the application.
     */
    private Map<String, URL> inputFiles;

    /**
     * The current status of the task.
     */
    private STATUS status;

    /**
     * The user name submitting the task.
     */
    private String user;

    /**
     * The date when the task was created.
     */
    private Date date;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public final Long getId() {
        return id;
    }

    /**
     * Set the task identifier.
     *
     * @param anId The task identifier
     */
    public final void setId(final Long anId) {
        this.id = anId;
    }

    /**
     * Get the id of the associated application.
     *
     * @return The id of the application associated with the task
     */
    public final Long getApplication() {
        return application;
    }

    /**
     * Set the application to associate with the task.
     *
     * @param anApplication The application identifier
     */
    public final void setApplication(final Long anApplication) {
        this.application = anApplication;
    }

    /**
     * Get the user description of the task.
     *
     * @return The user description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Set a description for the task.
     * @param aDescription Task description
     */
    public final void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    /**
     * Retrieve the list of arguments for the application.
     * This is a list of string the system will use as parameter for the
     * application. Actually, the list will be converted in a space separated
     * string maintaining the order of the list.
     * <p>
     * The use of the list will depend on the application. In case of an
     * executable the list will be appended to the command line but for a
     * service there is not a standard use at the moment.
     *
     * @return The list of arguments
     */
    @ElementCollection
    @CollectionTable(name = "application_arguments",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "arguments")
    public final List<String> getArguments() {
        return arguments;
    }

    /**
     * Set a list of arguments for the application.
     * This is a list of string the system will use as parameter for the
     * application. Actually, the list will be converted in a space separated
     * string maintaining the order of the list.
     * <p>
     * The use of the list will depend on the application. In case of an
     * executable the list will be appended to the command line but for a
     * service there is not a standard use at the moment.
     *
     * @param someArguments The arguments to provide to the application
     */
    public final void setArguments(final List<String> someArguments) {
        this.arguments = someArguments;
    }

    /**
     * Retrieve the list of output files of the application.
     * This is the list of files the system has to retrieve after the
     * application or the service has completed its execution.
     *
     * @return The output files
     */
    @ElementCollection
    @CollectionTable(name = "application_arguments",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "arguments")
    public final List<String> getOutputFiles() {
        return outputFiles;
    }

    /**
     * Set the list of output files of the application.
     * This is the list of files the system has to retrieve after the
     * application or the service has completed its execution.
     *
     * @param someOutputFiles A list with the output files
     */
    public final void setOutputFiles(final List<String> someOutputFiles) {
        this.outputFiles = someOutputFiles;
    }

    /**
     * Retrieve the input files for the application.
     * This is a map of files sent to the remote infrastructure for the
     * execution of the application and/or service. The map key is the file
     * name and the map value is an URL locating the file.
     * <p>
     * The URL can be local or remote to the service.
     *
     * @return The input files
     */
    @ElementCollection
    @CollectionTable(name = "application_inputs",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "inputs")
    public final Map<String, URL> getInputFiles() {
        return inputFiles;
    }

    /**
     * Set the input files for the application.
     * This is a map of files sent to the remote infrastructure for the
     * execution of the application and/or service. The map key is the file
     * name and the map value is an URL locating the file.
     * <p>
     * The URL can be local or remote to the service.
     *
     * @param someInputFiles A map with the input files.
     */
    public final void setInputFiles(final Map<String, URL> someInputFiles) {
        this.inputFiles = someInputFiles;
    }

    /**
     * Get the status of the task.
     *
     * @return The status.
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    @Enumerated(EnumType.STRING)
    public final STATUS getStatus() {
        return status;
    }

    /**
     * Set the status for the task.
     *
     * @param aStatus The status to associate with the task
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    public final void setStatus(final STATUS aStatus) {
        this.status = aStatus;
    }

    /**
     * Get the user identifier.
     * Every task is associated to a user. The identifier is provided by an
     * external entity (e.g. a Web Application) and can be of any kind but has
     * to be unique for the user.
     *
     * @return The user identifier
     */
    public final String getUser() {
        return user;
    }

    /**
     * Set the user identifier.
     * Every task is associated to a user. The identifier is provided by an
     * external entity (e.g. a Web Application) and can be of any kind but has
     * to be unique for the user.
     *
     * @param aUser The user identifier
     */
    public final void setUser(final String aUser) {
        this.user = aUser;
    }

    /**
     * Get the creation time of the task.
     *
     * @return Creation time
     */
    public final Date getDate() {
        return date;
    }

    /**
     * Set the creation time of the task.
     *
     * @param creationDate The creation time
     */
    public final void setDate(final Date creationDate) {
        this.date = creationDate;
    }

    /**
     * Get the time of last status change.
     *
     * @return The time of last status change
     */
    public final Date getLastChange() {
        return lastChange;
    }

    /**
     * Set the time of last status change.
     *
     * @param newChangeDate The time of last status change
     */
    public final void setLastChange(final Date newChangeDate) {
        this.lastChange = newChangeDate;
    }

}
