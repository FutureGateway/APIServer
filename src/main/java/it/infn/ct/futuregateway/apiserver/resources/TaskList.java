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

import it.infn.ct.futuregateway.apiserver.utils.LinkJaxbAdapter;
import java.util.List;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * The TaskList represents a collection of tasks.
 * This is a utility resource used to represent the collection correctly. The
 * resource annotation does not allow to define the collection representation
 * as defined in the documentation and this class is the workaround.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task-collection
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@InjectLinks({
    @InjectLink(value = "tasks", rel = "self")
})
@XmlRootElement(name = "TaskList")
public class TaskList {

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
     *
     * @param someTasks Tasks to insert in the list
     */
    public TaskList(final List<Task> someTasks) {
        this.tasks = someTasks;
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
