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
package it.infn.ct.futuregateway.apiserver.inframanager.state;

import it.infn.ct.futuregateway.apiserver.inframanager.TaskException;
import it.infn.ct.futuregateway.apiserver.inframanager.Utilities;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import it.infn.ct.futuregateway.apiserver.utils.TestData;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadyTest {

    /**
     * Fake executor service.
     */
    @Mock
    private ExecutorService ec;

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;

    /**
     * Fake Blocking queue.
     */
    @Mock
    private BlockingQueue<Task> bq;

    /**
     * Test of action method, of class Ready.
     */
    @Test
    public final void testAction() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        t.setState(Task.STATE.READY);
        Mockito.when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));

        TaskState ts;
        try {
            ts = t.getStateManager();
            ts.action(ec, bq, storage);
        } catch (TaskException ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("Task status did not change after submit",
                Task.STATE.READY, t.getState());
    }

    /**
     * Test of action method, of class Ready.
     */
    @Test
    public final void testActionWithNativeId() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        t.setState(Task.STATE.READY);
        List<Params> infraParams =
                t.getAssociatedInfrastructure().getParameters();
        t.setNativeId("["
                + Utilities.getParamterValue(infraParams, "jobservice")
                + "]-["
                + RandomStringUtils.randomAlphanumeric(TestData.IDLENGTH)
                + "]");
        Mockito.when(storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get("/tmp"));

        TaskState ts;
        try {
            ts = t.getStateManager();
            ts.action(ec, bq, storage);
        } catch (TaskException ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("Task state changed.",
                        Task.STATE.SCHEDULED, t.getState());
    }

}
