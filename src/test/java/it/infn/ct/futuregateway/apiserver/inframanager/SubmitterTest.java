/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.infn.ct.futuregateway.apiserver.inframanager;

import it.infn.ct.futuregateway.apiserver.utils.TestData;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mock;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class SubmitterTest {

    /**
     * Fake cache storage object.
     */
    @Mock
    private Storage storage;

    /**
     * Test of run method, of class Submitter.
     */
    @Test
    public final void testRun() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        t.setState(Task.STATE.WAITING);
        Submitter s = new Submitter(t, storage);
        try {
            s.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertEquals(Task.STATE.ABORTED, t.getState());
    }
}
