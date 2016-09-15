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
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for the Application.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class TaskServiceIT extends  JerseyTest {


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
        return new ResourceConfig(TaskService.class);
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
            Entity<Infrastructure> infraEntity = Entity.entity(
                    TestDataIT.createInfrastructure(),
                    Constants.INDIGOMIMETYPE);
            Response rs = target("/v1.0/infrastructures").
                    request(Constants.INDIGOMIMETYPE).post(infraEntity);
            infras.add(rs.readEntity(Infrastructure.class).getId());
        }

        apps = new LinkedList<>();
        for (int i = 0;
                i < 1 + (int) TestDataIT.MAX_ENTITIES_IN_LIST * Math.random();
                i++) {
            Application app = TestDataIT.createApplication();
            app.setInfrastructureIds(infras);
            Entity<Application> appEntity = Entity.entity(
                    app,
                    Constants.INDIGOMIMETYPE);
            Response rs = target("/v1.0/applications").
                    request(Constants.INDIGOMIMETYPE).post(appEntity);
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
     * Tests the details retrieval.
     */
    @Test
    public final void testTaskDetails() {
        Task newTask = TestDataIT.createTask();
        newTask.setApplicationId(apps.get((int) (Math.random() * apps.size())));
        Response rs;
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(newTask, Constants.INDIGOMIMETYPE));
        rs = target("/v1.0/tasks/"
                + rs.readEntity(Application.class).getId()).
                request(Constants.INDIGOMIMETYPE).get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Task task = rs.readEntity(Task.class);
        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        Assert.assertNotNull(task.getDateCreated());
        Assert.assertNotNull(task.getLastChange());
        Assert.assertEquals(newTask.getDescription(), task.getDescription());
        Assert.assertEquals(newTask.getApplicationId(),
                task.getApplicationId());
        Transformer<TaskFile, String> transformer =
                new Transformer<TaskFile, String>() {
                    @Override
                    public String transform(final TaskFile file) {
                        return file.getName();
                    }
                };
        if (newTask.getInputFiles() != null) {
            Assert.assertNotNull(task.getInputFiles());
            Assert.assertEquals(
                    IterableUtils.toString(
                            newTask.getInputFiles(), transformer),
                    IterableUtils.toString(task.getInputFiles(), transformer));
        }
        if (newTask.getOutputFiles() != null) {
            Assert.assertNotNull(task.getOutputFiles());
            Assert.assertEquals(
                    IterableUtils.toString(
                            newTask.getOutputFiles(), transformer),
                    IterableUtils.toString(task.getOutputFiles(), transformer));
        }
        target("/v1.0/tasks/" + task.getId()).
                request().delete();
    }



    /**
     * Test task delete.
     */
    @Test
    public final void testTaskDelete() {
        Response rs;
        rs = target("/v1.0/tasks/" + UUID.randomUUID()).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());

        Task testTask = TestDataIT.createTask();
        testTask.setApplicationId(
                apps.get((int) (Math.random() * apps.size())));
        rs = target("/v1.0/tasks").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(testTask, Constants.INDIGOMIMETYPE));
        String id = rs.readEntity(Task.class).getId();
        rs = target("/v1.0/tasks/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                rs.getStatus());
        rs = target("/v1.0/tasks/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());
    }
}
