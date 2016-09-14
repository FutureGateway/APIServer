/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.infn.ct.futuregateway.apiserver.inframanager;

import it.infn.ct.futuregateway.apiserver.utils.TestData;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.nio.file.Paths;
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
        when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));
        Job job = CustomJobFactory.createJob(t, storage);
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
        when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));
        Job job = CustomJobFactory.createJob(t, storage);
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
        when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));
        Job job = CustomJobFactory.createJob(t, storage);
    }
}
