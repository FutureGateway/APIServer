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

import it.infn.ct.futuregateway.apiserver.inframanager.gLite.GridSessionBuilder;
import it.infn.ct.futuregateway.apiserver.inframanager.gLite.ResourceDiscovery;
import it.infn.ct.futuregateway.apiserver.inframanager.occi.OCCISessionBuilder;
import it.infn.ct.futuregateway.apiserver.inframanager.ssh.SSHSessionBuilder;
import it.infn.ct.futuregateway.apiserver.resources.Params;
import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.url.URLFactory;

/**
 * Utility class to build a JobService.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class CustomJobFactory {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private static final Log LOG = LogFactory.getLog(SessionBuilder.class);

    /**
     * Avoid the class be instantiable.
     */
    private CustomJobFactory() { }

    /**
     * Create the JobService for the infrastructure.
     *
     * @param task The task requesting the JobService
     * @param store The storage managing the cache file
     * @return The JobService
     * @throws InfrastructureException If the infrastructure cannot be used for
     * some problem in the configuration or in the infrastructure
     * @throws BadParameterException The task cannot be submitted because some
     * parameters are missed or not correct
     * @throws DoesNotExistException exception
     */
    public static Job createJob(final Task task, final Storage store)
            throws InfrastructureException, BadParameterException,
            DoesNotExistException {
        List<Params> infraParams = Utilities.mergeParams(
                task.getAssociatedInfrastructure().getParameters(),
                task.getApplicationDetail().getParameters()
                );

        String jobServiceEP = Utilities.getParameterValue(
                    infraParams, "jobservice");
        String nativeID = null;
        if (task.getNativeId() != null) {
            final Pattern pattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");
            final Matcher matcher = pattern.matcher(task.getNativeId());
            if (matcher.find()) {
                jobServiceEP = matcher.group(1);
                nativeID = matcher.group(2);
            } else {
                final String msg = "Native id '" + nativeID + "' for task "
                        + task.getId() + " is not valid!";
                LOG.error(msg);
                throw new DoesNotExistException(msg);
            }
        }

        String infraType = Utilities.getParameterValue(infraParams, "type");
        if (infraType == null) {
            LOG.debug("Infrastructure "
                    + task.getAssociatedInfrastructure().getId()
                    + " has not 'type' defined");
            if (jobServiceEP == null || jobServiceEP.isEmpty()) {
                String msg = "Infrastructure "
                        + task.getAssociatedInfrastructure().getId()
                        + " has not 'type' or 'jobservice' defined";
                LOG.error(msg);
                throw new InfrastructureException(msg);
            }
            infraType = jobServiceEP.substring(0, infraType.indexOf(":"));
        }

        SessionBuilder sb;
        switch (infraType) {
            case "wsgram":
            case "gatekeeper":
            case "gLite":
            case "wms":
                sb = new GridSessionBuilder(
                        task.getAssociatedInfrastructure(), task.getUserName());
                ResourceDiscovery rd = new ResourceDiscovery(infraParams,
                        sb.getVO(), jobServiceEP);
                try {
                    jobServiceEP = rd.getJobResource(
                            ResourceDiscovery.ResourceType.WMS);
                } catch (NoResorucesAvailable nra) {
                    throw new InfrastructureException("No service resources"
                            + " available for the infrastructure "
                            + task.getAssociatedInfrastructureId());
                }
                break;
            case "rocci":
            case "occi":
                sb = new OCCISessionBuilder(
                        task.getAssociatedInfrastructure(), task.getUserName());
                break;
            case "unicore":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            case "ourgrid":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            case "bes-genesis2":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            case "gos":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            case "ssh":
                sb = new SSHSessionBuilder(
                        task.getAssociatedInfrastructure(), task.getUserName());
                break;
            case "openstack":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            case "tosca":
                throw new UnsupportedOperationException("Infrastructures "
                        + infraType + " not yet supported");
            default:
                throw new InfrastructureException("Infrastructure type '"
                        + infraType + "' not supported");
        }
        try {
            final JobService jobService = JobFactory.createJobService(
                    System.getProperty("saga.factory", Defaults.SAGAFACTORY),
                    sb.getSession(),
                    URLFactory.createURL(
                            System.getProperty("saga.factory",
                                    Defaults.SAGAFACTORY),
                            jobServiceEP));
            Job job;
            if (nativeID == null) {
                final JobDescription jobDescription =
                        JobDescriptionFactory.createJobDescription(task, store);
                job = jobService.createJob(jobDescription);
            } else {
                job = jobService.getJob(nativeID);
            }
            return job;
        } catch (AuthenticationFailedException | AuthorizationFailedException
                | IncorrectURLException | NoSuccessException
                | NotImplementedException | PermissionDeniedException
                | TimeoutException ex) {
            LOG.error(ex);
            throw new InfrastructureException("Impossibile to generate a job "
                    + "for the infrastructure "
                    + task.getAssociatedInfrastructureId());
        }
    }
}
