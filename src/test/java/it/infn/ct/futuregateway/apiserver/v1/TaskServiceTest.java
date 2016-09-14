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
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.utils.TestData;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test the TaskService.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
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
     * Test of getTaskDetails method, of class TaskService.
     * Gets task details with the correct ID
     *
     * @throws Exception If the request cannot be generated.
     */
    @Test
    public final void testGetTaskDetails() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(em.find(eq(Task.class), anyString())).thenReturn(t);
        when(emf.createEntityManager()).thenReturn(em);
        when(context.getAttribute(Constants.SESSIONFACTORY)).thenReturn(emf);
        when(request.getServletContext()).thenReturn(context);
        TaskService ts = new TaskService();
        Field req = BaseService.class.getDeclaredField("request");
        req.setAccessible(true);
        req.set(ts, request);
        assertNotNull(ts.getRequest());
        Task tDet = ts.getTaskDetails(t.getId());
        assertEquals(t.getId(), tDet.getId());
        assertEquals(t.getApplicationId(), tDet.getApplicationId());
    }

    /**
     * Test of getTaskDetails method, of class TaskService.
     * Gets task details with the wrong ID
     *
     * @throws Exception If the request cannot be generated.
     */
    @Test(expected = NotFoundException.class)
    public final void testGetTaskDetailsNoID() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(em.find(eq(Task.class), anyString())).thenReturn(null);
        when(emf.createEntityManager()).thenReturn(em);
        when(context.getAttribute(Constants.SESSIONFACTORY)).thenReturn(emf);
        when(request.getServletContext()).thenReturn(context);
        TaskService ts = new TaskService();
        Field req = BaseService.class.getDeclaredField("request");
        req.setAccessible(true);
        req.set(ts, request);
        assertNotNull(ts.getRequest());
        Task tDet = ts.getTaskDetails(t.getId());
    }

    /**
     * Test of deleteTask method, of class TaskService.
     *
     * @throws Exception If the request cannot be generated.
     */
    @Test
    public final void testDeleteTask() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(em.find(eq(Task.class), anyString())).thenReturn(t);
        when(em.getTransaction()).thenReturn(et);
        when(emf.createEntityManager()).thenReturn(em);
        when(context.getAttribute(Constants.SESSIONFACTORY)).thenReturn(emf);
        when(context.getAttribute(Constants.CACHEDIR))
                .thenReturn("/tmp/FGAPI/test");
        when(request.getServletContext()).thenReturn(context);
        TaskService ts = new TaskService();
        Field req = BaseService.class.getDeclaredField("request");
        req.setAccessible(true);
        req.set(ts, request);
        assertNotNull(ts.getRequest());
        ts.deleteTask(t.getApplicationId());
        verify(em).close();
        verify(em).remove(t);
        verify(et).commit();
    }

    /**
     * Test of deleteTask method, of class TaskService.
     * Delete with exception to check the correct close of the entity manager
     *
     * @throws Exception If the request cannot be generated.
     */
    @Test
    public final void testDeleteTaskException() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(em.find(eq(Task.class), anyString())).thenReturn(t);
        when(em.getTransaction()).thenReturn(et);
        doThrow(new RuntimeException()).when(em).remove(eq(t));
        when(emf.createEntityManager()).thenReturn(em);
        when(context.getAttribute(Constants.SESSIONFACTORY)).thenReturn(emf);
        when(context.getAttribute(Constants.CACHEDIR))
                .thenReturn("/tmp/FGAPI/test");
        when(request.getServletContext()).thenReturn(context);
        TaskService ts = new TaskService();
        Field req = BaseService.class.getDeclaredField("request");
        req.setAccessible(true);
        req.set(ts, request);
        assertNotNull(ts.getRequest());
        try {
            ts.deleteTask(t.getApplicationId());
        } catch (InternalServerErrorException isee) {
        }
        verify(em).close();
        verify(em).remove(t);
    }

    /**
     * Test of setInputFile method, of class TaskService.
     *
     * @throws Exception If the request cannot be generated.
     */
    @Test
    public final void testSetInputFile() throws Exception {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        when(em.find(eq(Task.class), anyString())).thenReturn(t);
        when(em.getTransaction()).thenReturn(et);
        doThrow(new RuntimeException()).when(em).remove(eq(t));
        when(emf.createEntityManager()).thenReturn(em);
        when(context.getAttribute(Constants.SESSIONFACTORY)).thenReturn(emf);
        when(context.getAttribute(Constants.CACHEDIR))
                .thenReturn("/tmp/FGAPI/test");
        when(request.getServletContext()).thenReturn(context);
        TaskService ts = new TaskService();
        Field req = BaseService.class.getDeclaredField("request");
        req.setAccessible(true);
        req.set(ts, request);
        assertNotNull(ts.getRequest());
    }
}
