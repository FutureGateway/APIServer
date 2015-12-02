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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@InjectLinks({
    @InjectLink(value = "tasks", rel = "self")
})
@XmlRootElement(name = "TaskList")

public class TaskList {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskList.class);

    /**
     * List of references.
     */
    @InjectLinks({
        @InjectLink(value = "tasks", rel = "self"),
    })
    @XmlElement(name = "_links")
    @XmlJavaTypeAdapter(value = LinkJaxbAdapter.class)
    private List<Link> links;

    /**
     * List of associated tasks.
     */
    private List<Task> tasks;

    /**
     * Create an empty task list.
     * In this case the tasks cannot be retrieved from the storage but they
     * have to be provided from the manger object.
     */
    public TaskList() {
    }

    /**
     * Create a task list for the user.
     * Tasks are retrieved from the storage using the provided entity manger.
     *
     * @param em Entity manager to access the stored tasks
     * @param user The user owner of the tasks
     */
    public TaskList(final EntityManager em, final String user) {
        List<Task> lstTasks = new LinkedList<>();
        EntityTransaction et = null;
        List<Object[]> taskList = null;
        try {
            et = em.getTransaction();
            et.begin();
            taskList = em.createNamedQuery("findTasks").
                    setParameter("user", user).
                    getResultList();
            et.commit();
        } catch (RuntimeException re) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Impossible to retrieve the task list");
            log.error(re);
            throw new RuntimeException("Impossible to access the task list");
        } finally {
            em.close();
        }
        if (taskList != null && !taskList.isEmpty()) {
            for (Object[] elem: taskList) {
                int idElem = 0;
                Task tmpTask = new Task();
                tmpTask.setId((String) elem[idElem++]);
                tmpTask.setDescription((String) elem[idElem++]);
                tmpTask.setStatus((Task.STATUS) elem[idElem++]);
                tmpTask.setDateCreated((Date) elem[idElem]);
                lstTasks.add(tmpTask);
            }
        }
        this.tasks = lstTasks;
    }

    /**
     * Retrieve the list of tasks.
     *
     * @return List of tasks
     */
    public final List<Task> getTasks() {
        return tasks;
    }

    /**
     * Set a new task list.
     *
     * @param someTasks A list of tasks
     */
    public final void setTasks(final List<Task> someTasks) {
        this.tasks = someTasks;
    }
}
