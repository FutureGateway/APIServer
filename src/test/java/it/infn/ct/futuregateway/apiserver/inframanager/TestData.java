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

import it.infn.ct.futuregateway.apiserver.resources.Application;
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.resources.TaskFileInput;
import it.infn.ct.futuregateway.apiserver.storage.LocalStorage;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.ogf.saga.job.JobDescription;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class TestData {

    /**
     * Folder name length.
     */
    private static final int STORAGE_PATH_RANDOM_FOLDER_NAME_LENGTH = 12;
    /**
     * Avoid the class be instantiable.
     */
    private TestData() {
    }

    /**
     * Create a fake task.
     *
     * @return A task for test
     */
    public static Task createTask() {
        Infrastructure i = new Infrastructure();
        i.setId("testInfra");
        i.setParameters(new LinkedList<Params>());
        List<Infrastructure> li = new LinkedList<>();
        li.add(i);

        Application a = new Application();
        a.setId("testApp");
        a.setInfrastructures(li);
        List<Params> lap = new LinkedList<>();
        Params exec = new Params();
        exec.setName(JobDescription.EXECUTABLE);
        exec.setValue("myBinary");
        lap.add(exec);
        a.setParameters(lap);

        Task t = new Task();
        t.setId("testTask");
        t.setApplicationDetail(a);
        t.setAssociatedInfrastructureId(i.getId());
        List<TaskFileInput> ltif = new LinkedList<>();
        TaskFileInput f = new TaskFileInput();
        f.setName("aFileName");
        f.setUrl("aFileLocalPath");
        f.setStatus(TaskFile.FILESTATUS.READY);
        ltif.add(f);
        t.setInputFiles(ltif);
        return t;
    }


    /**
     * Create a fake storage.
     *
     * @return A storage for test
     */
    public static Storage createStorage() {
        return new LocalStorage("/tmp/APIServiceTest/"
                + RandomStringUtils.random(
                        STORAGE_PATH_RANDOM_FOLDER_NAME_LENGTH));
    }
}
