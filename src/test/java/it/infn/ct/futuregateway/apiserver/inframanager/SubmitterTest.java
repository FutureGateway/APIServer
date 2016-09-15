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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test the Submitter.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class SubmitterTest {

    /**
     * Test of run method, of class Submitter.
     */
    @Test
    public final void testRun() {
        Task t = TestData.createTask(TestData.TASKTYPE.SSH);
        t.setState(Task.STATE.WAITING);
        Submitter s = new Submitter(t, null);
        try {
            s.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertEquals(Task.STATE.ABORTED, t.getState());
    }
}
