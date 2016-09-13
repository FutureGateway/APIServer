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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.ogf.saga.job.JobDescription;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class TestData {

    /**
     * Indicates the infrastructure parameters to associate with the task.
     * Task information are almost random
     */
    public enum TASKTYPE {
        /**
         * Task with no type or resource associated.
         */
        BASIC,
        /**
         * Task with SSH type and no resource.
         */
        SSH,
        /**
         * Task with SSH type and resource.
         */
        SSHFULL,
        /**
         * Task with WMS type and resource.
         */
        GRID,
        /**
         * Task with OCCI type and resource.
         */
        OCCI,
        /**
         * Task with TOSCA type and resource.
         */
        TOSCA
    }

    /**
     * Generated ID length.
     */
    private static final int IDLENGTH = 14;

    /**
     * Max length of random string as parameter value.
     */
    private static final int PROPERTYVALUEMAXLENGTH = 120;

    /**
     * Avoid the class be instantiable.
     */
    private TestData() {
    }

    /**
     * Create a fake task.
     *
     * @param type The type of infrastructure the task should be submitted
     * @return A task for test
     */
    public static Task createTask(final TASKTYPE type) {
        Random rand = new Random();
        Infrastructure i = new Infrastructure();
        i.setId(RandomStringUtils.randomAlphanumeric(IDLENGTH));
        i.setParameters(new LinkedList<Params>());
        List<Infrastructure> li = new LinkedList<>();
        li.add(i);

        Application a = new Application();
        a.setId(RandomStringUtils.randomAlphanumeric(IDLENGTH));
        a.setInfrastructures(li);
        List<Params> lap = new LinkedList<>();
        Params exec = new Params();
        exec.setName(JobDescription.EXECUTABLE);
        exec.setValue(RandomStringUtils.randomAlphanumeric(
                rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
        lap.add(exec);
        a.setParameters(lap);

        Task t = new Task();
        t.setId(RandomStringUtils.randomAlphanumeric(IDLENGTH));
        t.setApplicationDetail(a);
        t.setAssociatedInfrastructureId(i.getId());
        List<TaskFileInput> ltif = new LinkedList<>();
        TaskFileInput f = new TaskFileInput();
        f.setName(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
        f.setUrl(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
        f.setStatus(TaskFile.FILESTATUS.READY);
        ltif.add(f);
        t.setInputFiles(ltif);
        Params infraType = new Params();
        Params res = new Params();
        switch (type) {
            case BASIC:
                break;
            case GRID:
                infraType.setName("type");
                infraType.setValue("wms");
                t.getAssociatedInfrastructure().getParameters().add(infraType);
                res.setName("jobservice");
                res.setValue("wms://"
                        + RandomStringUtils.randomAlphanumeric(
                                rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
                t.getAssociatedInfrastructure().getParameters().add(res);
                break;
            case OCCI:
            case TOSCA:
                break;
            case SSHFULL:
                res.setName("jobservice");
                res.setValue("ssh://"
                        + RandomStringUtils.randomAlphanumeric(
                                rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
                t.getAssociatedInfrastructure().getParameters().add(res);
            case SSH:
                infraType.setName("type");
                infraType.setValue("ssh");
                t.getAssociatedInfrastructure().getParameters().add(infraType);
                Params user = new Params();
                user.setName("username");
                user.setValue(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
                t.getAssociatedInfrastructure().getParameters().add(user);
                Params pass = new Params();
                pass.setName("password");
                pass.setValue(RandomStringUtils.randomAlphanumeric(
                        rand.nextInt(PROPERTYVALUEMAXLENGTH) + 1));
                t.getAssociatedInfrastructure().getParameters().add(pass);
                break;
            default:
        }
        return t;
    }


}
