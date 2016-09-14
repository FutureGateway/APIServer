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
 ***********************************************************************
 */
package it.infn.ct.futuregateway.apiserver.v1;

import it.infn.ct.futuregateway.apiserver.utils.TestDataIT;
import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.resources.Application;
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskList;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for the Task collection.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class TaskCollectionServiceIT extends JerseyTest {

    /**
     * Infrastructures to use for testing the tasks.
     */
    private List<String> infras;
    /**
     * Application to use for test for testing the tasks.
     */
    private List<String> apps;

    @Override
    protected final javax.ws.rs.core.Application configure() {
        return new ResourceConfig(TaskCollectionService.class);
    }


    /**
     * Create applications and infrastructures to associate the
     * tasks with.
     */
    @Before
    public final void prepareApplication() {
        infras = new LinkedList<>();
        for (int i = 0;
                i < 1 + (int) TestDataIT.MAX_ENTITIES_IN_LIST * Math.random();
                i++) {
            Response rs = target("/v1.0/infrastructures").
                    request(Constants.INDIGOMIMETYPE).
                    post(Entity.entity(TestDataIT.createInfrastructure(),
                            Constants.INDIGOMIMETYPE));
            infras.add(rs.readEntity(Infrastructure.class).getId());
        }

        apps = new LinkedList<>();
        for (int i = 0;
                i < 1 + (int) TestDataIT.MAX_ENTITIES_IN_LIST * Math.random();
                i++) {
            Application app = TestDataIT.createApplication();
            app.setInfrastructureIds(infras);
            Response rs = target("/v1.0/applications").
                    request(Constants.INDIGOMIMETYPE).
                    post(Entity.entity(app, Constants.INDIGOMIMETYPE));
            apps.add(rs.readEntity(Application.class).getId());
        }
    }


    /**
     * Remove the applications and infrastructures after the tests.
     */
    @After
    public final void cleanApplication() {
        for (String id: apps) {
            target("/v1.0/applications/" + id).
                request().delete();
        }
        for (String id: infras) {
            target("/v1.0/infrastructures/" + id).
                request().delete();
        }
    }


    /**
     * Test the collection list.
     */
    @Test
    public final void testListTasks() {
        Response rs;
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        TaskList lstTaskEmpty =
                rs.readEntity(TaskList.class);
        Assert.assertNotNull(lstTaskEmpty);
        Assert.assertEquals(new LinkedList<Task>(),
                lstTaskEmpty.getTasks());

        List<Task> lstNewTask = new LinkedList<>();
        for (int i = 0;
                i < (int) (1 + Math.random() * TestDataIT.MAX_ENTITIES_IN_LIST);
                i++) {
            Task newTask = TestDataIT.createTask();
            newTask.setApplicationId(
                    apps.get((int) (Math.random() * apps.size())));
            lstNewTask.add(newTask);
            target("/v1.0/tasks").request(Constants.INDIGOMIMETYPE).
                    post(Entity.entity(newTask, Constants.INDIGOMIMETYPE));
        }
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        TaskList lstTask = rs.readEntity(TaskList.class);
        Assert.assertNotNull(lstTask);
        Assert.assertEquals(lstNewTask.size(),
                lstTask.getTasks().size());
        for (Task remTask: lstTask.getTasks()) {
            target("/v1.0/tasks/" + remTask.getId()).
                request().delete();
        }
    }


    /**
     * Test to add a task.
     */
    @Test
    public final void testAddTask() {
        Task task = TestDataIT.createTask();
        Response rs;

        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(task, Constants.INDIGOMIMETYPE));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                rs.getStatus());

        task.setApplicationId(RandomStringUtils.randomAlphanumeric(
                (int) (1 + (Math.random() * TestDataIT.MAX_STRING_LENGTH))));
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(task, Constants.INDIGOMIMETYPE));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                rs.getStatus());

        task.setApplicationId(apps.get((int) (Math.random() * apps.size())));
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(task, Constants.INDIGOMIMETYPE));
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(),
                rs.getStatus());

        Task newTask = rs.readEntity(Task.class);
        Assert.assertNotNull(newTask);
        Assert.assertNotNull(newTask.getId());
        Assert.assertNotNull(newTask.getDateCreated());
        if (task.getInputFiles() != null) {
            Assert.assertNotNull(newTask.getInputFiles());
            Assert.assertEquals(task.getInputFiles().size(),
                    newTask.getInputFiles().size());
        }
        if (task.getOutputFiles() != null) {
            Assert.assertNotNull(newTask.getOutputFiles());
            Assert.assertEquals(task.getOutputFiles().size(),
                    newTask.getOutputFiles().size());
        }
        Assert.assertEquals(Task.STATE.WAITING, newTask.getState());
        Assert.assertEquals(task.getDescription(), newTask.getDescription());
        target("/v1.0/tasks/" + newTask.getId()).
                request().delete();
    }


    /**
     * Try to remove the application when one or more tasks exist.
     */
    @Test
    public final void testDeleteAssociatedApplication() {
        Task task = TestDataIT.createTask();
        Response rs;

        task.setApplicationId(apps.get(0));
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(task, Constants.INDIGOMIMETYPE));
        Task newTask = rs.readEntity(Task.class);
        rs = target("/v1.0/applications/" + apps.get(0)).
                request(Constants.INDIGOMIMETYPE).delete();
        Assert.assertEquals(Response.Status.CONFLICT.getStatusCode(),
                rs.getStatus());
        target("/v1.0/tasks/" + newTask.getId()).
                request(Constants.INDIGOMIMETYPE).delete();
    }
}
