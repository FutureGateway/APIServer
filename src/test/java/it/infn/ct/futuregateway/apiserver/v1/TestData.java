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

import it.infn.ct.futuregateway.apiserver.v1.resources.Application;
import it.infn.ct.futuregateway.apiserver.v1.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.v1.resources.Params;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFileInput;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFileOutput;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * The TestData class contains all the data registered in the DB during the
 * integration tests.
 * The provided methods allow to generate all the resources to store in the DB
 * and use for the the tests.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class TestData {

    /**
     * Max number of entities to add in a random list for test.
     */
    public static final int MAX_ENTITIES_IN_LIST = 10;

    /**
     * Max number of entities to add in a random list for test.
     */
    public static final int MAX_STRING_LENGTH = 10;

    /**
     * Max number of entities to add in a random list for test.
     */
    public static final int MAX_DESC_LENGTH = 60;

    /**
     * Random generator for the boolean.
     */
    private static Random rnd = new Random();



    /**
     * Create a random infrastructure.
     * Data are randomly generated.
     *
     * @return The infrastructure
     */
    public static Infrastructure createInfrastructure() {
        Infrastructure infra = new Infrastructure();
        infra.setName(RandomStringUtils.randomAlphanumeric(
                (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
        infra.setDescription(RandomStringUtils.randomAlphanumeric(
                (int) (1 + (Math.random() * MAX_DESC_LENGTH))));
        infra.setEnabled(rnd.nextBoolean());
        List<Params> params = new LinkedList();
        for (int i = 0; i < (int) (Math.random() * MAX_ENTITIES_IN_LIST); i++) {
            Params p = new Params();
            p.setDescription(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_DESC_LENGTH))));
            p.setName(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            p.setValue(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            params.add(p);
        }
        if (!params.isEmpty()) {
            infra.setParameters(params);
        }
        return infra;
    }


    /**
     * Create a random infrastructure.
     * All data are randomly generated but the infrastructure which is not
     * included.
     *
     * @return The infrastructure
     */
    public static Application createApplication() {
        Application app = new Application();
        app.setName(RandomStringUtils.randomAlphanumeric(
                (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
        app.setDescription(RandomStringUtils.randomAlphanumeric(
                (int) (1 + (Math.random() * MAX_DESC_LENGTH))));
        app.setEnabled(rnd.nextBoolean());
        List<Params> params = new LinkedList();
        for (int i = 0; i < (int) (Math.random() * MAX_ENTITIES_IN_LIST); i++) {
            Params p = new Params();
            p.setDescription(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_DESC_LENGTH))));
            p.setName(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            p.setValue(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            params.add(p);
        }
        if (!params.isEmpty()) {
            app.setParameters(params);
        }
        return app;
    }


    /**
     * Create a random task.
     * All data are randomly generated but the application.
     *
     * @return The task
     */
    public static Task createTask() {
        Task task = new Task();
        List<String> someArgs = new LinkedList<>();
        for (int i = 0; i < (int) (Math.random() * MAX_ENTITIES_IN_LIST); i++) {
            someArgs.add(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
        }
        task.setArguments(someArgs);
        task.setDescription(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_DESC_LENGTH))));
        List<TaskFileInput> inputs = new LinkedList<>();
        for (int i = 0; i < (int) (Math.random() * MAX_ENTITIES_IN_LIST); i++) {
            TaskFileInput in = new TaskFileInput();
            in.setName(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            inputs.add(in);
        }
        if (!inputs.isEmpty()) {
            task.setInputFiles(inputs);
        }
        List<TaskFileOutput> outputs = new LinkedList<>();
        for (int i = 0; i < (int) (Math.random() * MAX_ENTITIES_IN_LIST); i++) {
            TaskFileOutput out = new TaskFileOutput();
            out.setName(RandomStringUtils.randomAlphanumeric(
                    (int) (1 + (Math.random() * MAX_STRING_LENGTH))));
            outputs.add(out);
        }
        if (!outputs.isEmpty()) {
            task.setOutputFiles(outputs);
        }
        return task;
    }


    /**
     * Utility class cannot be allocated.
     */
    private TestData() { }
}
