/**
 * *********************************************************************
 * Copyright (c) 2015: Istituto Nazionale di Fisica Nucleare (INFN), Italy
 * Consorzio COMETA (COMETA), Italy
 *
 * See http://www.infn.it and and http://www.consorzio-cometa.it for details on
 * the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 **********************************************************************
 */
package it.infn.ct.futuregateway.apiserver.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web application lifecycle listener.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@WebListener
public class PersistenceListener implements ServletContextListener {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(PersistenceListener.class);

    /**
     * EntityManagerFactory for the JPA.
     */
    private EntityManagerFactory entityManagerFactory;

    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        log.info("Creation of the Hibernate SessionFactory for the context");
        entityManagerFactory = Persistence.createEntityManagerFactory(
                "it.infn.ct.futuregateway.apiserver"
        );
        sce.getServletContext().setAttribute(
                "SessionFactory", entityManagerFactory
        );
        System.out.println("Ciccio non va");
        log.info("Created the Hibernate SessionFactory for the context");
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            log.info("Released the Persistence Manager for the context");
        }
    }
}
