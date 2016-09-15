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

package it.infn.ct.futuregateway.apiserver.inframanager;

import it.infn.ct.futuregateway.apiserver.utils.TestData;
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;

/**
 * Test the SussionBuilder.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class SessionBuilderTest {

    /**
     * Temporary file storing the proxy.
     */
    private final String fileName = "proxyFile.pem";

    /**
     * Fake proxy content.
     */
    private final String myProxy = "This is a test proxy";

    /**
     * Generates the proxy file.
     *
     * @throws IOException If the file cannot be created
     */
    @Before
    public final void createFile() throws IOException {
        Files.write(Paths.get(fileName), myProxy.getBytes("utf-8"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Removes the proxy file.
     *
     * @throws IOException If the file cannot be removed
     */
    @After
    public final void removeFile() throws IOException {
        Files.delete(Paths.get(fileName));
    }

    /**
     * Test of readRemoteProxy method, of class SessionBuilder.
     * This uses the parameter proxyurl.
     *
     * @throws Exception If the proxy cannot be read
     */
    @Test
    public final void testReadRemoteProxyProxyURL() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.GRID);
        Params proxy = new Params();
        proxy.setName("proxyurl");
        proxy.setValue(Paths.get(fileName).toUri().toString());
        t.getAssociatedInfrastructure().getParameters().add(proxy);
        SessionBuilder sb = new SessionBuilderImpl(
                t.getAssociatedInfrastructure());
        String retrievedProxy = sb.readRemoteProxy();
        assertEquals(myProxy, retrievedProxy);
    }


    /**
     * Test of readRemoteProxy method, of class SessionBuilder.
     * This uses the old parameters.
     *
     * @throws Exception If the proxy cannot be read
     */
    @Test
    public final void testReadRemoteProxyETokenURL() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.GRID);
        Params proxyParent = new Params();
        proxyParent.setName("etokenserverurl");
        proxyParent.setValue(
                Paths.get(fileName).toAbsolutePath().getParent()
                        .toUri().toString());
        t.getAssociatedInfrastructure().getParameters().add(proxyParent);
        Params proxy = new Params();
        proxy.setName("etokenid");
        proxy.setValue(Paths.get(fileName).getFileName().toString());
        t.getAssociatedInfrastructure().getParameters().add(proxy);
        SessionBuilder sb = new SessionBuilderImpl(
                t.getAssociatedInfrastructure());
        String retrievedProxy = sb.readRemoteProxy();
        assertEquals(myProxy, retrievedProxy);
    }


    /**
     * Test of readRemoteProxy method, of class SessionBuilder.
     * This uses the old parameters.
     *
     * @throws Exception If the proxy cannot be read
     */
    @Test(expected = InfrastructureException.class)
    public final void testReadRemoteProxyMissedURL() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.GRID);
        SessionBuilder sb = new SessionBuilderImpl(
                t.getAssociatedInfrastructure());
        String retrievedProxy = sb.readRemoteProxy();
        assertEquals(myProxy, retrievedProxy);
    }


    /**
     * Implements a concrete SessionBuilder.
     */
    public class SessionBuilderImpl extends SessionBuilder {

        /**
         * Build a session for the infrastructure.
         *
         * @param anInfra An infrastructure which has the params for the proxy
         */
        public SessionBuilderImpl(final Infrastructure anInfra) {
            super(anInfra);
        }

        @Override
        public final void createNewSession() throws InfrastructureException {
        }

        @Override
        public final String getVO() {
            return "test";
        }
    }
}
