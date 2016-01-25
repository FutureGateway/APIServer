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
import it.infn.ct.futuregateway.apiserver.resources.Application;
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
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
public class ApplicationServiceIT extends JerseyTest {

    /**
     * Infrastructure to associate with the applications.
     */
    private List<String> infra;



    @Override
    protected final javax.ws.rs.core.Application configure() {
        return new ResourceConfig(ApplicationService.class);
    }


    /**
     * Create infrastructures to associate the applications with.
     */
    @Before
    public final void prepareInfrastructure() {
        infra = new LinkedList<>();
        for (int i = 0;
                i < 1 + (int) TestData.MAX_ENTITIES_IN_LIST * Math.random();
                i++) {
            Entity<Infrastructure> infraEntity = Entity.entity(
                    TestData.createInfrastructure(),
                    Constants.INDIGOMIMETYPE);
            Response rs = target("/v1.0/infrastructures").
                    request(Constants.INDIGOMIMETYPE).post(infraEntity);
            infra.add(rs.readEntity(Infrastructure.class).getId());
        }
    }


    /**
     * Remove the infrastructures after the tests.
     */
    @After
    public final void cleanInfrastructure() {
        for (String id: infra) {
            target("/v1.0/infrastructures/" + id).
                request().delete();
        }
    }


    /**
     * Tests the details retrieval.
     */
    @Test
    public final void testApplicationDetails() {
        Application newApp = TestData.createApplication();
        newApp.setInfrastructureIds(infra);
        Response rs;
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(newApp, Constants.INDIGOMIMETYPE));
        rs = target("/v1.0/applications/"
                + rs.readEntity(Application.class).getId()).
                request(Constants.INDIGOMIMETYPE).get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Application app = rs.readEntity(Application.class);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.getId());
        Assert.assertNotNull(app.getDateCreated());
        if (newApp.getParameters() != null) {
            Assert.assertNotNull(app.getParameters());
            Assert.assertEquals(newApp.getParameters(), app.getParameters());
        } else {
            Assert.assertEquals(new LinkedList<Params>(), app.getParameters());
        }
        Assert.assertEquals(newApp.isEnabled(), app.isEnabled());
        Assert.assertEquals(newApp.getName(), app.getName());
        Assert.assertEquals(newApp.getDescription(), app.getDescription());
        Assert.assertEquals(newApp.getInfrastructureIds(),
                app.getInfrastructureIds());
        target("/v1.0/applications/" + app.getId()).
                request().delete();
    }



    /**
     * Tests the delete.
     */
    @Test
    public final void testApplicationDelete() {
        Response rs;
        rs = target("/v1.0/applications/" + UUID.randomUUID()).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());

        Application testApp = TestData.createApplication();
        testApp.setInfrastructureIds(infra);
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(testApp, Constants.INDIGOMIMETYPE));
        String id = rs.readEntity(Application.class).getId();
        rs = target("/v1.0/applications/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                rs.getStatus());
        rs = target("/v1.0/applications/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());
    }
}
