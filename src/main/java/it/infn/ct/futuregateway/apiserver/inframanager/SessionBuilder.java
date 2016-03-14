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

import it.infn.ct.futuregateway.apiserver.resources.Infrastructure;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.session.Session;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public abstract class SessionBuilder {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(SessionBuilder.class);

    /**
     * Parameters to use for the session.
     */
    private Properties params = new Properties();

    /**
     * Infrastructure requiring the session.
     */
    private Infrastructure infrastructure;

    /**
     * User requiring the session.
     */
    private String user;

    /**
     * Last created session.
     */
    private Session session;

    /**
     * Empty builder.
     * Parameters must be provided before the session is created.
     */
    public SessionBuilder() {
    }


    /**
     * Build a new session.
     * The session is specifically created for the infrastructure provided.
     * <p>
     * This is aquivalent to {@code GridSessionBuilder(anInfra, null)}
     *
     * @param anInfra The infrastructure associated with the configured session
     */
    public SessionBuilder(final Infrastructure anInfra) {
        this(anInfra, null);
    }

    /**
     * Build a new session.
     * The session is specifically created for the infrastructure provided.
     *
     * @param anInfra The infrastructure associated with the configured session
     * @param aUser The user requesting the session
     */
    public SessionBuilder(final Infrastructure anInfra,
            final String aUser) {
        this.infrastructure = anInfra;
        params = Utilities.convertParamsToProperties(anInfra.getParameters());
        this.user = aUser;
    }


    /**
     * Build a new session.
     *
     * @param someParams Parameters to use for the new session
     */
    public SessionBuilder(final Properties someParams) {
        this(someParams, null);
    }


    /**
     * Build a new session.
     *
     * @param someParams Parameters to use for the new session
     * @param aUser The user requesting the session
     */
    public SessionBuilder(final Properties someParams,
            final String aUser) {
        this.params = someParams;
        this.user = aUser;
    }


    /**
     * Build a new session.
     *
     * @param aTask The task requesting the creation of the session
     */
    public SessionBuilder(final Task aTask) {
        this(aTask.getAssociatedInfrastructure(), aTask.getUserName());
    }


    /**
     * Create a new session.
     * The session is created with the context derived from the infrastructure
     * parameters and stored inside the builder. A new session is created
     * on call so multiple call will return different sessions.
     *
     * @throws InfrastructureException In case there is a problem with the
     * infrastructure or the parameter are not correct for the context (bad or
     * missed values)
     */
    public abstract void createNewSession() throws InfrastructureException;

    /**
     * Retrieves the last created session.
     * If the session has not been created a new session is generated with the
     * method <code>createNewSession</code>. Multiple call will return the same
     * method unless a new session is explicitely requested.
     *
     * @return The session.
     * @throws InfrastructureException If a new session cannot be generated
     */
    public final Session getSession() throws InfrastructureException {
        if (session == null) {
            createNewSession();
        }
        return session;
    }

    /**
     * Set the session.
     *
     * @param aSession The new session
     * @return The SessionBuilder
     */
    protected final SessionBuilder setSession(final Session aSession) {
        this.session = aSession;
        return this;
    }

    /**
     * Read the proxy certificate from a remote location.
     * The location is retrieved from the parameters.
     *
     * @return A string representation of the proxy
     * @throws InfrastructureException If the proxy for the infrastructure
     * cannot be retrieved for problems with the parameters
     */
    protected final String readRemoteProxy() throws InfrastructureException {
        URL proxy;
        if (params.getProperty("proxyurl") == null
                && params.getProperty("etokenserverurl") == null) {
            throw new InfrastructureException("No proxy location in "
                    + "configuration parameters for " + infrastructure.getId());
        }
        if (params.getProperty("proxyurl") != null) {
            try {
                proxy = new URL(params.getProperty("proxyurl"));
            } catch (MalformedURLException mue) {
                throw new InfrastructureException("URL for the proxy is not "
                        + "valid, infrastructure " + infrastructure.getId()
                        + " is not accessible");
            }
        } else {
            try {
                URI etokenurl = new URI(params.getProperty("etokenserverurl"));
                StringBuilder queryURI = new StringBuilder();
                StringBuilder pathURI = new StringBuilder();
                String oldPath = etokenurl.getPath();
                if (oldPath != null) {
                    pathURI.append(oldPath);
                    if (!oldPath.endsWith("/")) {
                        pathURI.append('/');
                    }
                    pathURI.append(params.getProperty("etokenid", ""));
                } else {
                    pathURI.append('/')
                            .append(params.getProperty("etokenid", ""));
                }
                String oldQuery = etokenurl.getQuery();
                if (oldQuery != null) {
                    queryURI.append(oldQuery).append('&');
                }
                queryURI.append("voms=")
                        .append(params.getProperty("vo", ""))
                        .append(':')
                        .append(params.getProperty("voroles", ""))
                        .append('&');
                queryURI.append("proxy-renewal=")
                        .append(params.getProperty("proxyrenewal",
                                Defaults.PROXYRENEWAL))
                        .append('&');
                queryURI.append("disable-voms-proxy=")
                        .append(params.getProperty("disablevomsproxy",
                                Defaults.DISABLEVOMSPROXY))
                        .append('&');
                queryURI.append("rfc-proxy=")
                        .append(params.getProperty("rfcproxy",
                                Defaults.RFCPROXY))
                        .append('&');
                queryURI.append("cn-label=");
                if (user != null) {
                    queryURI.append("eToken:").append(user);
                }
                etokenurl = new URI(
                        etokenurl.getScheme(),
                        etokenurl.getUserInfo(),
                        etokenurl.getHost(),
                        etokenurl.getPort(),
                        pathURI.toString(),
                        queryURI.toString(),
                        etokenurl.getFragment());
                proxy = etokenurl.toURL();
            } catch (MalformedURLException | URISyntaxException use) {
                throw new InfrastructureException("etokenserverurl not "
                        + "properly configured for infrastructure "
                        + getInfrastructure().getId());
            }
        }
        StringBuilder strProxy = new StringBuilder();
        log.debug("Accessing the proxy " + proxy.toString());
        try {
            String lnProxy;
            BufferedReader fileProxy = new BufferedReader(
                    new InputStreamReader(proxy.openStream()));
            while ((lnProxy = fileProxy.readLine()) != null) {
                strProxy.append(lnProxy);
                strProxy.append("\n");
            }
        } catch (IOException ioer) {
            log.error("Impossible to retrieve the remote proxy certificate from"
                    + ": " + proxy.toString());
        }
        log.debug("Proxy:\n\n" + strProxy.toString() + "\n\n");
        return strProxy.toString();
    }


    /**
     * Retrieves the session parameters.
     *
     * @return The parameters
     */
    public final Properties getParams() {
        return params;
    }



    /**
     * Sets the parameter for the session.
     *
     * @param somePrams The parameters
     * @return The SessionBuilder
     */
    public final SessionBuilder setParams(final Properties somePrams) {
        this.params = somePrams;
        return this;
    }

    /**
     * Add additional parameters.
     *
     * @param aParam The new parameter
     * @return The SessionBuilder
     */
    public final SessionBuilder addParameter(final Params aParam) {
        this.params.setProperty(aParam.getName(), aParam.getValue());
        return this;
    }

    /**
     * Retrieves the user generating the session.
     *
     * @return The user identifier
     */
    public final String getUser() {
        return user;
    }

    /**
     * Sets the user for the session.
     *
     * @param aUser The user identifier
     * @return The SessionBuilder
     */
    public final SessionBuilder setUser(final String aUser) {
        this.user = aUser;
        return this;
    }

    /**
     * Retrieves the infrastructure associated to the session.
     *
     * @return The infrastructure
     */
    public final Infrastructure getInfrastructure() {
        if (infrastructure == null) {
            return new Infrastructure();
        }
        return infrastructure;
    }

    /**
     * Sets the infrastructure for the session.
     *
     * @param anInfrastructure The infrastructure
     * @return The SessionBuilder
     */
    public final SessionBuilder setInfrastructure(
            final Infrastructure anInfrastructure) {
        this.infrastructure = anInfrastructure;
        for (Params par: anInfrastructure.getParameters()) {
            params.setProperty(par.getName(), par.getValue());
        }
        return this;
    }


    /**
     * Retrieves the Virtual Organisation (VO).
     * The method analyse the parameters and if not present will check the
     * credentials (i.e. the proxy certificate) to retrieve the VO name.
     *
     * @return The VO name
     */
    public abstract String getVO();
}
