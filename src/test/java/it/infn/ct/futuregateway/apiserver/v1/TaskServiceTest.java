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

import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class TaskServiceTest extends JerseyTest {
    
    @Override
    protected Application configure() {
        return new ResourceConfig(TaskService.class);
    }
    

    /**
     * Test id the task details return correctly
     */
    @Test
    public void taskDetails() {
        Response rs;

        rs = target("/v1.0/tasks/" + UUID.randomUUID().toString()).
                request(Constants.INDIGOMIMETYPE).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), rs.getStatus());

        Task ts = new Task();        
        ts.setApplicationId("test");
        Entity<Task> taskEntity = Entity.entity(ts, Constants.INDIGOMIMETYPE);
        rs = target("/v1.0/tasks").request(Constants.INDIGOMIMETYPE).
                post(taskEntity);
        Task newTask = rs.readEntity(Task.class);

        rs = target("/v1.0/tasks/" + newTask.getId()).
                request(Constants.INDIGOMIMETYPE).get();
        assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        
        Task recall = rs.readEntity(Task.class);
        assertEquals(newTask.getId(), recall.getId());
        assertEquals(newTask.getApplicationId(), recall.getApplicationId());
    }


    /**
     * Test id the task details return correctly
     */
    @Test
    public void taskDelete() {
        Response rs;

        Task ts = new Task();        
        ts.setApplicationId("test");
        Entity<Task> taskEntity = Entity.entity(ts, Constants.INDIGOMIMETYPE);
        rs = target("/v1.0/tasks").request(Constants.INDIGOMIMETYPE).
                post(taskEntity);
        Task newTask = rs.readEntity(Task.class);

        rs = target("/v1.0/tasks/" + newTask.getId()).
                request(Constants.INDIGOMIMETYPE).delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), rs.getStatus());
        
        rs = target("/v1.0/tasks/" + newTask.getId()).
                request(Constants.INDIGOMIMETYPE).delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), rs.getStatus());
    }
    
}
