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

import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.utils.TestData;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.ogf.saga.job.Job;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomJobFactoryTest {

    /**
     * Path to cache directory.
     */
    private static final String TMP_FOLDER = "/tmp";

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has not the type or the resource defined
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = InfrastructureException.class)
    public final void testCreateJobNoTypeNoRes() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.BASIC);
        when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));
        Job job = CustomJobFactory.createJob(t, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has the type defined but the resource is not defined
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = NullPointerException.class)
    public final void testCreateJobWithTypeSSH() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));
        Job job = CustomJobFactory.createJob(t, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has the type and resource defined
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = InfrastructureException.class)
    public final void testCreateGridJob() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));
        Job job = CustomJobFactory.createJob(t, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has native id
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = InfrastructureException.class)
    public final void testCreateJobWithNativeJobId() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));
        final List<Params> infraParams =
                t.getAssociatedInfrastructure().getParameters();
        t.setNativeId("["
                + Utilities.getParamterValue(infraParams, "jobservice")
                + "]-["
                + RandomStringUtils.randomAlphanumeric(TestData.IDLENGTH)
                + "]");
        Job job = CustomJobFactory.createJob(t, this.storage);
        Assert.fail("Remote service not called by JSAGA.");
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has wrong native id
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = NullPointerException.class)
    public final void testCreateJobWithWrongNativeJobId() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));
        final List<Params> infraParams =
                t.getAssociatedInfrastructure().getParameters();
        t.setNativeId("["
                + Utilities.getParamterValue(infraParams, "jobservice")
                + "]");
        Job job = CustomJobFactory.createJob(t, this.storage);
        Assert.fail("Job created even though it has a no valid native id.");
    }
}
