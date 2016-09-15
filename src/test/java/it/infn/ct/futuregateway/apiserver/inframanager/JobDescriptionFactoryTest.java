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
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.ogf.saga.job.JobDescription;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Test the JobDescriptionFactory.
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class JobDescriptionFactoryTest {

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;


    /**
     * Test of createJobDescription method, of class JobDescriptionFactory.
     * @throws Exception Impossible to perform the test
     */
    @Test
    public final void testCreateJobDescription() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.BASIC);
        when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));
        JobDescription jd = JobDescriptionFactory.createJobDescription(
                    t, storage);
        Properties prTask = Utilities.convertParamsToProperties(
            t.getApplicationDetail().getParameters());
        assertEquals("Executable not corresponding",
                prTask.getProperty(JobDescription.EXECUTABLE),
                jd.getAttribute(JobDescription.EXECUTABLE));
    }
}
