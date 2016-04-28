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

package it.infn.ct.futuregateway.apiserver.inframanager.occi;

import it.infn.ct.futuregateway.apiserver.inframanager.SessionBuilder;
import it.infn.ct.futuregateway.apiserver.inframanager.InfrastructureException;
import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

/**
 * Build a valid session for the requested infrastructure.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class OCCISessionBuilder extends SessionBuilder {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(OCCISessionBuilder.class);

    /**
     * This class cannot be instantiated.
     */
    public OCCISessionBuilder() {
        super();
    }


    /**
     * Build a new session.
     * The session is specifically created for the infrastructure provided.
     * <p>
     * This is aquivalent to {@code SessionBuilder(anInfra, null)}
     *
     * @param anInfra The infrastructure associated with the configured session
     */
    public OCCISessionBuilder(final Infrastructure anInfra) {
        this(anInfra, null);
    }

    /**
     * Build a new session.
     * The session is specifically created for the infrastructure provided.
     *
     * @param anInfra The infrastructure associated with the configured session
     * @param aUser The user requesting the session
     */
    public OCCISessionBuilder(final Infrastructure anInfra,
            final String aUser) {
        super(anInfra, aUser);
    }


    /**
     * Build a new session.
     *
     * @param someParams Parameters to use for the new session
     */
    public OCCISessionBuilder(final Properties someParams) {
        this(someParams, null);
    }


    /**
     * Build a new session.
     *
     * @param someParams Parameters to use for the new session
     * @param aUser The user requesting the session
     */
    public OCCISessionBuilder(final Properties someParams,
            final String aUser) {
        super(someParams, aUser);
    }


    /**
     * Create a new session.
     * The session is create with the context derived from the infrastructure
     * parameters.
     *
     * @throws InfrastructureException In case there is a problem with the
     * infrastructure or the parameter are not correct for the context (bad or
     * missed values)
     */
    @Override
    public void createNewSession()
            throws InfrastructureException {
        Session newSession;
        try {
            newSession = SessionFactory.createSession(
                    System.getProperty("saga.factory", Defaults.SAGAFACTORY),
                    false);
        } catch (NoSuccessException nse) {
            log.error("Impossible to generate a new session.");
            log.error(nse);
            throw new InfrastructureException("New sessions cannot be created");
        }
        log.debug("Create a new OCCI session");
        try {
            Context context = ContextFactory.createContext(
                    System.getProperty("saga.factory", Defaults.SAGAFACTORY),
                    "rocci");
            context.setAttribute(Context.USERPROXY,
                    readRemoteProxy());
            context.setAttribute(Context.USERID, "root");
            context.setAttribute(Context.USERCERT,
                    getParams().getProperty("sshpublickey",
                            Defaults.SSHPUBLICKEY));
            context.setAttribute(Context.USERKEY,
                    getParams().getProperty("sshprivatekey",
                            Defaults.SSHPRIVATEKEY));

            newSession.addContext(context);
        } catch (BadParameterException | IncorrectStateException
                | TimeoutException | DoesNotExistException
                | NotImplementedException | NoSuccessException ex) {
            log.error("Impossible to create a valid context for "
                    + getInfrastructure().getId());
            log.error(ex);
            throw new InfrastructureException("Impossible to open a session in"
                    + " the infratructure");
        } catch (AuthenticationFailedException | AuthorizationFailedException
                | PermissionDeniedException autherr) {
            log.error("Impossible to log-in to the infrastructure");
            log.error(autherr);
            throw new InfrastructureException("Impossible to open a session in"
                    + " the infratructure");
        }
        setSession(newSession);
    }


    @Override
    public String getVO() {
        throw new UnsupportedOperationException("Not supported in OCCI based "
                + "infrastructures");
    }
}
