/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.infn.ct.futuregateway.apiserver.inframanager;

import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.ogf.saga.job.JobDescription;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class JobDescriptionFactoryTest {

    /**
     * Ciccio.
     */
    @Mock
    private Storage storage;


    /**
     * Test of createJobDescription method, of class JobDescriptionFactory.
     * @throws Exception Impossible to perform the test
     */
    @Test
    public final void testCreateJobDescription() throws Exception {
        Task t = TestData.createTask();
        Storage s = TestData.createStorage();
        JobDescription jd = null;
        when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));
        try {
            jd = JobDescriptionFactory.createJobDescription(
                    t, storage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Properties prTask = Utilities.convertParamsToProperties(
            t.getApplicationDetail().getParameters());
        assertEquals("Executable not corresponding",
                prTask.getProperty(JobDescription.EXECUTABLE),
                jd.getAttribute(JobDescription.EXECUTABLE));
    }
}
