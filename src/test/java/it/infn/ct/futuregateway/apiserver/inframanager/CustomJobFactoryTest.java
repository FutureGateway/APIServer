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

import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test the CustomJobFactory.
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
        final Task task = TestData.createTask(TestData.TASKTYPE.BASIC);
        Mockito.when(this.storage.getCachePath(
                ArgumentMatchers.eq(Storage.RESOURCE.TASKS),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).
                thenReturn(Paths.get(TMP_FOLDER));
        CustomJobFactory.createJob(task, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has the type defined but the resource is not defined
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = NullPointerException.class)
    public final void testCreateJobWithTypeSSH() throws Exception {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSH);
        Mockito.when(this.storage.getCachePath(
                ArgumentMatchers.eq(Storage.RESOURCE.TASKS),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).
                thenReturn(Paths.get(TMP_FOLDER));
        CustomJobFactory.createJob(task, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has the type and resource defined
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = InfrastructureException.class)
    public final void testCreateGridJob() throws Exception {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        Mockito.when(this.storage.getCachePath(
                ArgumentMatchers.eq(Storage.RESOURCE.TASKS),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).
                thenReturn(Paths.get(TMP_FOLDER));
        CustomJobFactory.createJob(task, this.storage);
    }

    /**
     * Test of createJob method, of class CustomJobFactory.
     * The task has native id
     *
     * @throws Exception Impossible to perform the test
     */
    @Test(expected = InfrastructureException.class)
    public final void testCreateJobWithNativeJobId() throws Exception {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        Mockito.when(this.storage.getCachePath(
                ArgumentMatchers.eq(Storage.RESOURCE.TASKS),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).
                thenReturn(Paths.get(TMP_FOLDER));
        final Infrastructure infrastructure =
                task.getAssociatedInfrastructure();
        final List<Params> infraParams = infrastructure.getParameters();
        final String jobService = Utilities.getParameterValue(infraParams,
                TestData.PARAMJOBSERVICE);
        final String jobId = RandomStringUtils.randomAlphanumeric(
                TestData.IDLENGTH);
        task.setNativeId("[" + jobService + "]-[" + jobId + "]");
        CustomJobFactory.createJob(task, this.storage);
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
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        Mockito.when(this.storage.getCachePath(
                ArgumentMatchers.eq(Storage.RESOURCE.TASKS),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).
                thenReturn(Paths.get(TMP_FOLDER));
        final Infrastructure infrastructure =
                task.getAssociatedInfrastructure();
        final List<Params> infraParams = infrastructure.getParameters();
        final String jobService = Utilities.getParameterValue(infraParams,
                TestData.PARAMJOBSERVICE);
        task.setNativeId("[" + jobService + "]");
        CustomJobFactory.createJob(task, this.storage);
        Assert.fail("Job created even though it has a no valid native id.");
    }
}
