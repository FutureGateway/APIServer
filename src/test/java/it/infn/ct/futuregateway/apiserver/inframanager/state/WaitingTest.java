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
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.resources.TaskFileInput;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import it.infn.ct.futuregateway.apiserver.utils.TestData;
import static it.infn.ct.futuregateway.apiserver.utils.TestData
        .PROPERTYVALUEMAXLENGTH;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class WaitingTest {

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
     * Test of action method, of class Waiting.
     */
    @Test
    public final void testAction() {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        task.setState(Task.STATE.WAITING);

        final Random rand = new Random();

        final List<TaskFileInput> ltif = new LinkedList<>();
        final TaskFileInput fileInput = new TaskFileInput();
        fileInput.setName(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
        fileInput.setUrl(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
        fileInput.setStatus(TaskFile.FILESTATUS.READY);
        ltif.add(fileInput);
        task.setInputFiles(ltif);

        final TaskState taskState;
        try {
            taskState = task.getStateManager();
            taskState.action(this.executorService, this.blockingQueue,
                    this.storage);
        } catch (TaskException ex) {
            Assert.fail("Tast failed for: " + ex.getMessage());
        }
        Assert.assertEquals("All files are redy", Task.STATE.READY,
                task.getState());
    }

    /**
     * Test of action method, of class Waiting.
     */
    @Test
    public final void testActionNoInputFile() {
        final Task task = TestData.createTask(TestData.TASKTYPE.SSHFULL);
        task.setState(Task.STATE.WAITING);

        task.setInputFiles(null);

        final TaskState taskState;
        try {
            taskState = task.getStateManager();
            taskState.action(this.executorService, this.blockingQueue,
                    this.storage);
        } catch (TaskException ex) {
            Assert.fail("Tast failed for: " + ex.getMessage());
        }
        Assert.assertEquals("No input files needed", Task.STATE.READY,
                task.getState());
    }

}
