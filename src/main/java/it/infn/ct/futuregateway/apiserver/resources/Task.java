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
package it.infn.ct.futuregateway.apiserver.resources;

import it.infn.ct.futuregateway.apiserver.utils.LinkJaxbAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Random;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The Task represents any activity a user send to an infrastructure, such as a
 * run a job in a grid computing site or deploy a VM in a cloud.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQueries({
    @NamedQuery(name = "tasks.userAll",
            query = "SELECT t.id, t.description, t.status, t.dateCreated"
            + " FROM Task t WHERE t.userName = :user"),
    @NamedQuery(name = "tasks.forApplication",
            query = "SELECT t.id FROM Task t WHERE "
                    + "t.applicationDetail.id = :appId")
})
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
    @XmlElement(name = "application")
    private String applicationId;

    /**
     * The id of the application associated with the task.
     */
    @XmlTransient
    private Application applicationDetail;

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
     * The date when the task was created.
     */
    @XmlElement(name = "runtime_data")
    private List<RuntimeParams> runtime;

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
    @XmlElement(name = "last_change")
    private Date lastChange;

    /**
     * Infrastructure executing the task.
     * An application can be associated with multiple infrastructure, this is
     * the Id of one selected to execute the task.
     */
    @XmlTransient
    private String associatedInfrastructureId;


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
     * Sets the task identifier.
     *
     * @param anId The task identifier
     */
    public void setId(final String anId) {
        this.id = anId;
        setChanged();
        notifyObservers();
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
     * Returns the associated application.
     *
     * @return The id of the application associated with the task
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "applicationId", referencedColumnName = "id",
            nullable = false)
    public Application getApplicationDetail() {
        return applicationDetail;
    }

    /**
     * Sets the application to associate with the task.
     * The application has to be valid in order the task to be valid.
     *
     * @param anApplication The application identifier
     */
    public void setApplicationDetail(final Application anApplication) {
        this.applicationDetail = anApplication;
        if (applicationId == null) {
            applicationId = applicationDetail.getId();
        }
        lastChange = new Date();
        setChanged();
        notifyObservers();
    }


    /**
     * Returns the id of the associated application.
     *
     * @return The id of the application associated with the task
     */
    @Transient
    public String getApplicationId() {
        if (applicationId == null && applicationDetail != null) {
            applicationId = applicationDetail.getId();
        }
        return applicationId;
    }

    /**
     * Sets the application to associate with the task.
     * The application has to be valid in order the task to be valid.
     *
     * @param anApplication The application identifier
     */
    public void setApplicationId(final String anApplication) {
        this.applicationId = anApplication;
        lastChange = new Date();
        setChanged();
    }

    /**
     * Returns the user description of the task.
     *
     * @return The user description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a description for the task.
     *
     * @param aDescription Task description
     */
    public void setDescription(final String aDescription) {
        this.description = aDescription;
        lastChange = new Date();
        setChanged();
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
     * Sets a list of arguments for the application. This is a list of string
     * the system will use as parameter for the application. Actually, the list
     * will be converted in a space separated string maintaining the order of
     * the list.
     * <p>
     * The use of the list will depend on the application. In case of an
     * executable the list will be appended to the command line but for a
     * service there is not a standard use at the moment.
     *
     * @param someArguments The arguments to provide to the application
     */
    public void setArguments(final List<String> someArguments) {
        this.arguments = someArguments;
        lastChange = new Date();
        setChanged();
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
    @Fetch(FetchMode.SELECT)
    public List<TaskFileOutput> getOutputFiles() {
        return this.outputFiles;
    }

    /**
     * Sets the list of output files of the application. This is the list of
     * files the system has to retrieve after the application or the service has
     * completed its execution.
     *
     * @param someOutputFiles A list with the output files
     */
    public void setOutputFiles(
            final List<TaskFileOutput> someOutputFiles) {
        this.outputFiles = someOutputFiles;
        lastChange = new Date();
        setChanged();
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
    @Fetch(FetchMode.SELECT)
    public List<TaskFileInput> getInputFiles() {
        return this.inputFiles;
    }

    /**
     * Sets the input files for the application. This is a map of files sent to
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
        lastChange = new Date();
        setChanged();
    }

    /**
     * Returns the status of the task.
     *
     * @return The status.
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    @Enumerated(EnumType.STRING)
    public STATUS getStatus() {
        return status;
    }

    /**
     * Sets the status for the task.
     *
     * @param aStatus The status to associate with the task
     * @see it.infn.ct.futuregateway.apiserver.v1.resources.Task.STATUS
     */
    public void setStatus(final STATUS aStatus) {
        lastChange = new Date();
        setChanged();
        this.status = aStatus;
        notifyObservers();
    }


    /**
     * Returns the runtime information.
     * Runtime information are retrieved by the system during the submission and
     * or execution of the task and can be relevant to the user. E.g. in case of
     * task running a VM the relevant information could be the IP and the
     * credentials of the machine.
     *
     * @return List of runtime
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    public List<RuntimeParams> getRuntime() {
        return runtime;
    }


    /**
     * Sets the runtime information.
     * Runtime information are retrieved by the system during the submission and
     * or execution of the task and can be relevant to the user. E.g. in case of
     * task running a VM the relevant information could be the IP and the
     * credentials of the machine.
     *
     * @param someRuntime A list of runtime parameters
     */
    public void setRuntime(final List<RuntimeParams> someRuntime) {
        lastChange = new Date();
        setChanged();
        this.runtime = someRuntime;
        notifyObservers();
    }

    /**
     * Returns the user identifier. Every task is associated to a user. The
     * identifier is provided by an external entity (e.g. a Web Application) and
     * can be of any kind but has to be unique for the user.
     *
     * @return The user identifier
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user identifier. Every task is associated to a user. The
     * identifier is provided by an external entity (e.g. a Web Application) and
     * can be of any kind but has to be unique for the user.
     *
     * @param aUser The user identifier
     */
    public void setUserName(final String aUser) {
        this.userName = aUser;
        lastChange = new Date();
        setChanged();
    }

    /**
     * Returns the creation time of the task.
     *
     * @return Creation time
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
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
        lastChange = new Date();
        setChanged();
    }

    /**
     * Returns the time of last status change.
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
        lastChange = new Date();
        setChanged();
    }

    /**
     * Returns the references for this entity.
     *
     * @return The list of Link references
     */
    @Transient
    public List<Link> getLinks() {
        return links;
    }

    /**
     * Sets the references for this entity.
     *
     * @param someLinks The list of link references
     */
    public void setLinks(final List<Link> someLinks) {
        this.links = someLinks;
        lastChange = new Date();
        setChanged();
    }

    /**
     * Retrieves the associated infrastructure Id.
     * This is the infrastructure selected to execute the task among
     * the many the application can run on.
     *
     * @return The infrastructure
     */
    @Transient
    public Infrastructure getAssociatedInfrastructure() {
        if (applicationDetail == null) {
            return null;
        }
        return IterableUtils.find(
                applicationDetail.getInfrastructures(),
                new Predicate<Infrastructure>() {
            @Override
            public boolean evaluate(final Infrastructure t) {
                return t.getId().equals(getAssociatedInfrastructureId());
            }
        });
    }
    /**
     * Retrieves the associated infrastructure Id.
     * This is the Id of the infrastructure selected to execute the task among
     * the many the application can run on.
     *
     * @return The infrastructure Id
     */
    public String getAssociatedInfrastructureId() {
        if ((associatedInfrastructureId == null
                || associatedInfrastructureId.isEmpty())
                && applicationDetail != null) {
            List<Infrastructure> infras =
                    ListUtils.select(applicationDetail.getInfrastructures(),
                            new Predicate<Infrastructure>() {
                                @Override
                                public boolean evaluate(
                                        final Infrastructure t) {
                                    return t.isEnabled();
                                }
                            }
                    );
            Random rand = new Random((new Date()).getTime());

            while (!infras.isEmpty()) {
                Infrastructure i = infras.remove(rand.nextInt(infras.size()));
                if (i.isEnabled()) {
                    setAssociatedInfrastructureId(i.getId());
                    return associatedInfrastructureId;
                }
            }
        }
        return associatedInfrastructureId;
    }

    /**
     * Sets the infrastructure for the execution.
     * It has to be one of the infrastructure the application is enabled to
     * execute.
     *
     * @param anInfrastructure The infrastructure Id
     */
    public void setAssociatedInfrastructureId(final String anInfrastructure) {
        this.associatedInfrastructureId = anInfrastructure;
        lastChange = new Date();
        setChanged();
        notifyObservers();
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
            lastChange = new Date();
            setChanged();
            notifyObservers();
        }
    }
}
