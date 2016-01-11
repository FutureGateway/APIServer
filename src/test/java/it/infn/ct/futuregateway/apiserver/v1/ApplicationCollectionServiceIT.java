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
import it.infn.ct.futuregateway.apiserver.v1.resources.Application;
import it.infn.ct.futuregateway.apiserver.v1.resources.ApplicationList;
import it.infn.ct.futuregateway.apiserver.v1.resources.Infrastructure;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for the Application collection.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class ApplicationCollectionServiceIT extends JerseyTest {

    /**
     * Infrastructure to associate with the applications.
     */
    private List<String> infra;



    @Override
    protected final javax.ws.rs.core.Application configure() {
        return new ResourceConfig(ApplicationCollectionService.class);
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
     * Test the collection list.
     */
    @Test
    public final void testListApplications() {
        Response rs;
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        ApplicationList lstAppEmpty =
                rs.readEntity(ApplicationList.class);
        Assert.assertNotNull(lstAppEmpty);
        Assert.assertEquals(new LinkedList<Application>(),
                lstAppEmpty.getApplications());

        List<Application> lstNewApp = new LinkedList<>();
        for (int i = 0;
                i < (int) (1 + Math.random() * TestData.MAX_ENTITIES_IN_LIST);
                i++) {
            Application newApp = TestData.createApplication();
            newApp.setInfrastructureIds(infra);
            lstNewApp.add(newApp);
            target("/v1.0/applications").request(Constants.INDIGOMIMETYPE).
                    post(Entity.entity(newApp, Constants.INDIGOMIMETYPE));
        }
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        ApplicationList lstApp = rs.readEntity(ApplicationList.class);
        Assert.assertNotNull(lstApp);
        Assert.assertEquals(lstNewApp.size(),
                lstApp.getApplications().size());
        for (Application remApp: lstApp.getApplications()) {
            target("/v1.0/applications/" + remApp.getId()).
                request().delete();
        }
    }


    /**
     * Test to add an application.
     */
    @Test
    public final void testAddApplication() {
        Application app = TestData.createApplication();
        Response rs;

        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(app, Constants.INDIGOMIMETYPE));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                rs.getStatus());

        app.setInfrastructureIds(infra);
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(app, Constants.INDIGOMIMETYPE));
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(),
                rs.getStatus());

        Application newApp = rs.readEntity(Application.class);
        Assert.assertNotNull(newApp);
        Assert.assertNotNull(newApp.getId());
        Assert.assertNotNull(newApp.getDateCreated());
        Assert.assertEquals(app.isEnabled(), newApp.isEnabled());
        Assert.assertEquals(app.getName(), newApp.getName());
        Assert.assertEquals(app.getDescription(), newApp.getDescription());
        Assert.assertEquals(app.getParameters(), newApp.getParameters());
        Assert.assertEquals(app.getInfrastructureIds(), infra);
        target("/v1.0/applications/" + newApp.getId()).
                request().delete();
    }


    /**
     * Try to remove the infrastructure when one or more applications exist.
     */
    @Test
    public final void testDeleteAssociatedInfrastructure() {
        Application app = TestData.createApplication();
        Response rs;

        app.setInfrastructureIds(infra);
        Entity<Application> appEntity = Entity.entity(app,
                Constants.INDIGOMIMETYPE);
        rs = target("/v1.0/applications").
                request(Constants.INDIGOMIMETYPE).post(appEntity);
        Application newApp = rs.readEntity(Application.class);
        rs = target("/v1.0/infrastructures/" + infra.get(0)).
                request(Constants.INDIGOMIMETYPE).delete();
        Assert.assertEquals(Response.Status.CONFLICT.getStatusCode(),
                rs.getStatus());
        target("/v1.0/applications/" + newApp.getId()).
                request(Constants.INDIGOMIMETYPE).delete();
    }
}
