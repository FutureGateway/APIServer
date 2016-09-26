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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadyTest {

    /**
     * Path to cache directory.
     */
    private static final String TMP_FOLDER = "/tmp";

    /**
     * Fake executor service.
     */
    @Mock
    private ExecutorService executorService;

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;

    /**
     * Fake Blocking queue.
     */
    @Mock
    private BlockingQueue<Task> blockingQueue;

    /**
     * Test of action method, of class Ready.
     */
    @Test
    public final void testAction() {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSH);
        task.setState(Task.STATE.READY);
        Mockito.when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));

        TaskState taskState;
        try {
            taskState = task.getStateManager();
            taskState.action(this.executorService, this.blockingQueue,
                    this.storage);
        } catch (TaskException ex) {
            Assert.fail("Tast failed for: " + ex.getMessage());
        }

        Assert.assertEquals("Task status did not change after submit",
                Task.STATE.READY, task.getState());
    }

    /**
     * Test of action method, of class Ready.
     */
    @Test
    public final void testActionWithNativeId() {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        task.setState(Task.STATE.READY);

        final List<Params> infraParams =
                task.getAssociatedInfrastructure().getParameters();
        task.setNativeId("["
                + Utilities.getParamterValue(infraParams, "jobservice")
                + "]-["
                + RandomStringUtils.randomAlphanumeric(TestData.IDLENGTH)
                + "]");

        Mockito.when(this.storage.getCachePath(eq(Storage.RESOURCE.TASKS),
                anyString(), anyString())).thenReturn(Paths.get(TMP_FOLDER));

        try {
            TaskState taskState = task.getStateManager();
            taskState.action(this.executorService, this.blockingQueue,
                    this.storage);
        } catch (TaskException ex) {
            Assert.fail("Tast failed for: " + ex.getMessage());
        }

        Assert.assertEquals("Task state changed.",
                        Task.STATE.SCHEDULED, task.getState());
    }

}
