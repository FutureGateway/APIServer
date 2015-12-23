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
    public static Infrastructure crateInfrastructure() {
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
        infra.setParameters(params);
        return infra;
    }


    /**
     * Create a random infrastructure.
     * All data are randomly generated but the infrastructure which is not
     * included.
     *
     * @return The infrastructure
     */
    public static Application crateApplication() {
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
        app.setParameters(params);
        return app;
    }

    /**
     * Utility class cannot be allocated.
     */
    private TestData() { }
}
