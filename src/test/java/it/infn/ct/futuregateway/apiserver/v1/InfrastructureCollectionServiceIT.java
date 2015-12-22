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
import it.infn.ct.futuregateway.apiserver.v1.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.v1.resources.InfrastructureList;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for the Infrastructure collection.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class InfrastructureCollectionServiceIT extends JerseyTest {

    @Override
    protected final Application configure() {
        return new ResourceConfig(InfrastructureCollectionService.class);
    }


    /**
     * Test the collection list.
     */
    @Test
    public final void testListInfrastructures() {
        Response rs;
        rs = target("/v1.0/infrastructures").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        InfrastructureList lstInfraEmpty =
                rs.readEntity(InfrastructureList.class);
        Assert.assertNotNull(lstInfraEmpty);
        Assert.assertEquals(new LinkedList<Infrastructure>(),
                lstInfraEmpty.getInfrastructures());

        List<Infrastructure> lstNewInfra = new LinkedList<>();
        for (int i = 0;
                i < (int) (1 + Math.random() * TestData.MAX_ENTITIES_IN_LIST);
                i++) {
            Infrastructure newInfra = TestData.crateInfrastructure();
            lstNewInfra.add(newInfra);
            target("/v1.0/infrastructures").request(Constants.INDIGOMIMETYPE).
                    post(Entity.entity(newInfra, Constants.INDIGOMIMETYPE));
        }
        rs = target("/v1.0/infrastructures").
                request(Constants.INDIGOMIMETYPE).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Assert.assertNotNull(rs.getLinks());
        InfrastructureList lstInfra = rs.readEntity(InfrastructureList.class);
        Assert.assertNotNull(lstInfra);
        Assert.assertEquals(lstInfra.getInfrastructures().size(),
                lstNewInfra.size());
        for (Infrastructure remInfra: lstInfra.getInfrastructures()) {
            target("/v1.0/infrastructures/" + remInfra.getId()).
                request().delete();
        }
    }


    /**
     * Test to add an infrastructure.
     */
    @Test
    public final void testAddInfrastructure() {
        Infrastructure infra = TestData.crateInfrastructure();
        Entity<Infrastructure> infraEntity = Entity.entity(infra,
                Constants.INDIGOMIMETYPE);
        Response rs = target("/v1.0/infrastructures").
                request(Constants.INDIGOMIMETYPE).post(infraEntity);
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(),
                rs.getStatus());
        Infrastructure newInfra = rs.readEntity(Infrastructure.class);
        Assert.assertNotNull(newInfra);
        Assert.assertNotNull(newInfra.getId());
        Assert.assertNotNull(newInfra.getDateCreated());
        Assert.assertNotNull(newInfra.getParameters());
        Assert.assertEquals(infra.isEnabled(), newInfra.isEnabled());
        Assert.assertEquals(infra.getName(), newInfra.getName());
        Assert.assertEquals(infra.getDescription(), newInfra.getDescription());
        Assert.assertEquals(infra.getParameters(), newInfra.getParameters());
        target("/v1.0/infrastructures/" + newInfra.getId()).
                request().delete();
    }
}
