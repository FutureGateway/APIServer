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

import it.infn.ct.futuregateway.apiserver.inframanager.MonitorQueue;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.concurrent.ExecutorService;

/**
 * Abstract state for the task.
 * Based on state pattern
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 * @author Mario Torrisi <mario.torrisi@ct.infn.it>
 */
public abstract class TaskState {

    /**
     * Perform the action connected with the task.
     *
     * @param anExecutorService Executor performing active action on the task
     * @param aMonitorQueue Queue for monitored tasks
     * @param aStorage Cache storage
     */
    public abstract void action(
            ExecutorService anExecutorService,
            MonitorQueue aMonitorQueue,
            Storage aStorage);
}
