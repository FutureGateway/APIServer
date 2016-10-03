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

package it.infn.ct.futuregateway.apiserver.v1;

import it.infn.ct.futuregateway.apiserver.inframanager.MonitorQueue;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.resources.TaskFileInput;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.utils.TestData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * Test the TaskService.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    /**
     * Fake http request.
     */
    @Mock
    private HttpServletRequest request;

    /**
     * Fake servlet context request.
     */
    @Mock
    private ServletContext context;

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;

    /**
     * Fake entity manger factory.
     */
    @Mock
    private EntityManagerFactory emf;

    /**
     * Fake entity manger.
     */
    @Mock
    private EntityManager em;

    /**
     * Fake entity manger.
     */
    @Mock
    private EntityTransaction et;

    /**
     * Fake executor service.
     */
    @Mock
    private ExecutorService ec;

    /**
     * Fake monitor queue.
     */
    @Mock
    private MonitorQueue mq;

    /**
     * Fake form multipart body with input file.
     */
    @Mock
    private FormDataBodyPart formBodyP;

    /**
     * Fake form data content disposition.
     */
    @Mock
    private FormDataContentDisposition formContentDisp;

    /**
     * Create the tester.
     */
    public TaskServiceTest() {
    }

    /**
     * Test of getTaskDetails method, of class TaskService.
     * Gets task details with the correct ID
     */
    @Test
    public final void testGetTaskDetails() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        Mockito.when(this.em.find(
                ArgumentMatchers.eq(Task.class), ArgumentMatchers.anyString())).
                thenReturn(t);
        final TaskService ts = this.getTaskService();
        Task tDet = ts.getTaskDetails(t.getId());
        Assert.assertEquals("Task details does not match",
                t.getId(), tDet.getId());
    }

    /**
     * Test of getTaskDetails method, of class TaskService.
     * Gets task details with the wrong ID
     */
    @Test(expected = NotFoundException.class)
    public final void testGetTaskDetailsNoID() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        Mockito.when(this.em.find(
                ArgumentMatchers.eq(Task.class), ArgumentMatchers.anyString())).
                thenReturn(null);
        final TaskService ts = this.getTaskService();
        Task tDet = ts.getTaskDetails(t.getId());
        Assert.assertNull("Task details has to be null for a not existing task",
                tDet);
    }

    /**
     * Test of deleteTask method, of class TaskService.
     */
    @Test
    public final void testDeleteTask() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        Mockito.when(this.em.find(
                ArgumentMatchers.eq(Task.class), ArgumentMatchers.anyString()))
                .thenReturn(t);
        final TaskService ts = this.getTaskService();
        ts.deleteTask(t.getApplicationId());
        Mockito.verify(this.em).close();
        Mockito.verify(this.em).remove(t);
        Mockito.verify(this.et).commit();
    }

    /**
     * Test of deleteTask method, of class TaskService.
     * Delete with exception to check the correct close of the entity manager
     */
    @Test
    public final void testDeleteTaskException() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        Mockito.when(this.em.find(
                ArgumentMatchers.eq(Task.class), ArgumentMatchers.anyString())).
                thenReturn(t);
        Mockito.doThrow(new RuntimeException()).when(em).remove(
                ArgumentMatchers.eq(t));
        final TaskService ts = this.getTaskService();
        try {
            ts.deleteTask(t.getApplicationId());
        } catch (InternalServerErrorException isee) {
            Mockito.verify(this.em).close();
            Mockito.verify(this.em).remove(t);
        }
    }

    /**
     * Test of setInputFile method, of class TaskService.
     *
     * @throws IOException In case of problem with the mock storage
     * @throws ParseException If a problem with file exist
     */
    @Test
    public final void testSetInputFile() throws IOException, ParseException {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        t.setState(Task.STATE.WAITING);
        final TaskService ts = this.getTaskService();
        Mockito.when(this.em.find(
                ArgumentMatchers.eq(Task.class), ArgumentMatchers.anyString())).
                thenReturn(t);
        final List<FormDataBodyPart> lstFiles = new LinkedList<>();
        final String fileName =
                RandomStringUtils.randomAlphanumeric(TestData.IDLENGTH);
        final List<TaskFileInput> lTfi = new LinkedList<>();
        final TaskFileInput pFile = new TaskFileInput();
        pFile.setName(fileName);
        lTfi.add(pFile);
        final TaskFileInput fFile = new TaskFileInput();
        fFile.setName(RandomStringUtils.randomAlphanumeric(TestData.IDLENGTH));
        lTfi.add(fFile);
        t.setInputFiles(lTfi);
        lstFiles.add(this.formBodyP);
        Mockito.when(this.formBodyP.getFormDataContentDisposition()).
                thenReturn(this.formContentDisp);
        Mockito.when(this.formBodyP.getValueAs(InputStream.class)).
                thenReturn(new ByteArrayInputStream(
                        RandomStringUtils.randomAlphanumeric(
                                TestData.PROPERTYVALUEMAXLENGTH).getBytes()));
        Mockito.when(this.formContentDisp.getFileName()).thenReturn(fileName);
        try {
            Files.createDirectories(Paths.get(TestData.PATHTOTEMPSTORAGE));
        } catch (Exception e) {
            Assert.fail("Storage problem");
        }
        ts.setInputFile(fileName, lstFiles);
        t.getInputFiles().stream().forEach((tFile) -> {
            if (tFile.getName().equals(fileName)) {
                Assert.assertEquals("File status did not change after insert",
                        TaskFile.FILESTATUS.READY, tFile.getStatus());
            } else {
                Assert.assertEquals("File status changed without insert",
                        TaskFile.FILESTATUS.NEEDED, tFile.getStatus());
            }
        });
    }

    /**
     * Create a TaskService for test.
     * Add the mock and perform the basic customisation.
     *
     * @return A TaskService with provided mocks
     */
    private TaskService getTaskService() {
        TaskService ts = null;
        try {
            Mockito.when(this.em.getTransaction()).thenReturn(this.et);
            Mockito.when(this.emf.createEntityManager()).thenReturn(this.em);
            Mockito.when(this.context.getAttribute(Constants.MONITORQUEUE)).
                    thenReturn(this.mq);
            Mockito.when(this.context.getAttribute(Constants.SESSIONFACTORY)).
                    thenReturn(this.emf);
            Mockito.when(this.context.getAttribute(Constants.CACHEDIR)).
                    thenReturn(TestData.PATHTOTEMPSTORAGE);
            Mockito.when(this.request.getServletContext()).
                    thenReturn(this.context);
            ts = new TaskService();
            Field req = BaseService.class.getDeclaredField("request");
            req.setAccessible(true);
            req.set(ts, this.request);
        } catch (IllegalAccessException | IllegalArgumentException
                | NoSuchFieldException ex) {
            Assert.fail("Impossible to generate the TaskService");
        }
        Assert.assertNotNull(ts.getRequest());
        return ts;
    }

}
