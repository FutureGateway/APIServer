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
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class InfrastructureServiceIT extends JerseyTest {


    @Override
    protected final Application configure() {
        return new ResourceConfig(InfrastructureService.class);
    }


    /**
     * Tests the details retrieval.
     */
    @Test
    public final void testInfrastructureDetails() {
        Infrastructure newInfra = TestData.crateInfrastructure();
        Response rs;
        rs = target("/v1.0/infrastructures").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(newInfra, Constants.INDIGOMIMETYPE));
        rs = target("/v1.0/infrastructures/"
                + rs.readEntity(Infrastructure.class).getId()).
                request(Constants.INDIGOMIMETYPE).get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), rs.getStatus());
        Infrastructure infra = rs.readEntity(Infrastructure.class);
        Assert.assertNotNull(infra);
        Assert.assertNotNull(infra.getId());
        Assert.assertNotNull(infra.getDateCreated());
        Assert.assertNotNull(infra.getParameters());
        Assert.assertTrue(infra.isEnabled());
        Assert.assertEquals(infra.getName(), newInfra.getName());
        Assert.assertEquals(infra.getDescription(), newInfra.getDescription());
        Assert.assertEquals(infra.getParameters(), newInfra.getParameters());
        target("/v1.0/infrastructures/" + infra.getId()).
                request().delete();
    }



    /**
     * Tests the delete.
     */
    @Test
    public final void testInfrastructureDelete() {
        Response rs;
        rs = target("/v1.0/infrastructures/" + UUID.randomUUID()).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());

        rs = target("/v1.0/infrastructures").
                request(Constants.INDIGOMIMETYPE).
                post(Entity.entity(TestData.crateInfrastructure(),
                        Constants.INDIGOMIMETYPE));
        String id = rs.readEntity(Infrastructure.class).getId();
        rs = target("/v1.0/infrastructures/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                rs.getStatus());
        rs = target("/v1.0/infrastructures/" + id).
                request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                rs.getStatus());
    }
}
