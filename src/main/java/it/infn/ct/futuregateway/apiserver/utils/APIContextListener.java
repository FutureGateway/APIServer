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

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class APIContextListener implements ServletContextListener {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(APIContextListener.class);

    /**
     * EntityManagerFactory for the JPA.
     */
    private EntityManagerFactory entityManagerFactory;

    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        log.info("Creation of the Hibernate SessionFactory for the context");
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    "it.infn.ct.futuregateway.apiserver.container"
            );
//            Context ctx = new InitialContext();
//            DataSource ds = (DataSource)
//                    ctx.lookup("java:comp/env/jdbc/FutureGatewayDB");
//            Connection conn = ds.getConnection();
        } catch (Exception ex) {
            log.info("Resource 'jdbc/FutureGatewayDB' not defined in the "
                    + "context. The server will use its default DB on file.");
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    "it.infn.ct.futuregateway.apiserver.app"
            );
        }
        sce.getServletContext().setAttribute(
                "SessionFactory", entityManagerFactory
        );
        log.info("Created the Hibernate SessionFactory for the context");
        String path = sce.getServletContext().getInitParameter("CacheDir");
        if (path == null || path.isEmpty()) {
            path = sce.getServletContext().getRealPath("/")
                    + ".." + FileSystems.getDefault().getSeparator()
                    + ".." + FileSystems.getDefault().getSeparator()
                    + "FutureGatewayData";
        }
        sce.getServletContext().setAttribute("CacheDir", path);
        try {
            Files.createDirectories(Paths.get(path));
            log.info("Cache dir enabled");
        } catch (FileAlreadyExistsException faee) {
            log.debug("Message for '" + path + "':" + faee.getMessage());
            log.info("Cache dir enabled");
        } catch (Exception e) {
            log.error("Impossible to initialise the temporary store");
        }

        int threadPoolSize = Constants.DEFAULTTHREADPOOLSIZETIMES;
        try {
            threadPoolSize = Integer.parseInt(sce.getServletContext().
                getInitParameter("SubmissioneThreadPoolSize"));
        } catch (NumberFormatException nfe) {
            log.info("Parameter 'SubmissioneThreadPoolSize' has a wrong value"
                    + " or it is not present. Default value 10 is used");
        }

//        ThreadPoolExecutor tpe = new ThreadPoolExecutor(
//                threadPoolSize,
//                Constants.MAXIMUMTHREADPOOLSIZETIMES * threadPoolSize,
//                Constants.MAXIMUMTHREADIDLELIFE,
//                TimeUnit.MINUTES,
//                new LinkedBlockingQueue<Runnable>());
//        sce.getServletContext().setAttribute("SubmissionThreadPool", tpe);
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
    }
}
