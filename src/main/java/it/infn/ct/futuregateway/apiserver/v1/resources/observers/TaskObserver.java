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
 ***********************************************************************
 */
package it.infn.ct.futuregateway.apiserver.v1.resources.observers;

import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFile;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Observes the changes in a task and takes due actions.
 * This observer verify the status of the task after the changes and take the
 * corresponding actions to enable the following steps. As en example, if the
 * task is READY then the TaskObserber will schedule it.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public class TaskObserver implements Observer {

    /**
     * Logger object. Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskObserver.class);

    @Override
    public final void update(final Observable obs, final Object arg) {
        if (!(obs instanceof Task)) {
            log.error("Wrong abject associated with the oserver");
        }
        Task t = (Task) obs;
        if (t.getStatus().equals(Task.STATUS.WAITING)) {
            for (TaskFile tf: t.getInputFiles()) {
                if (tf.getStatus().equals(TaskFile.FILESTATUS.NEEDED)) {
                    return;
                }
            }
            t.setStatus(Task.STATUS.READY);
        }

    }

}
