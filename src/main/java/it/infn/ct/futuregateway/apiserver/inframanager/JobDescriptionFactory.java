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

import it.infn.ct.futuregateway.apiserver.resources.Task;
import it.infn.ct.futuregateway.apiserver.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.storage.Storage;
import it.infn.ct.futuregateway.apiserver.utils.Constants;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;

/**
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
public final class JobDescriptionFactory {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private static final Log LOG = LogFactory.getLog(
            JobDescriptionFactory.class);

    /**
     * Avoid the class be instantiable.
     */
    private JobDescriptionFactory() { }


    /**
     * Create a job description for a task.
     * The job description is created using the parameters in the task.
     * If mandatory parameters are wrong, missed or create problems an
     * exception is raised. For not mandatory parameters the job description
     * will be created in any case (a LOG will notify the problem).
     *
     * @param task The task requiring the job description
     * @param store The storage managing the temporary files
     * @return The job description
     * @throws NoSuccessException Impossible to generate the description
     * @throws NotImplementedException Not implemented for this kind of task
     * @throws BadParameterException A mandatory parameter is wrong or missed
     */
    public static JobDescription createJobDescription(
            final Task task, final Storage store)
            throws NoSuccessException, NotImplementedException,
            BadParameterException {
        JobDescription jd = JobFactory.createJobDescription(
                System.getProperty("saga.factory", Defaults.SAGAFACTORY));
        Properties prTask = Utilities.convertParamsToProperties(
                task.getAssociatedInfrastructure().getParameters());
        prTask = Utilities.convertParamsToProperties(
                task.getApplicationDetail().getParameters(), prTask);

        String arguments = IterableUtils.toString(task.getArguments(),
                new Transformer<String, String>() {
                    @Override
                    public String transform(final String s) {
                        return s;
                    }
                },
                ";", "", "");
        prTask.setProperty(JobDescription.ARGUMENTS, arguments);
        try {
            setRequiredParam(jd, prTask, JobDescription.EXECUTABLE);
            if (prTask.containsKey(JobDescription.FILETRANSFER)) {
                setRequiredParam(jd, prTask, JobDescription.FILETRANSFER, true);
            } else {
                List<String> fTransf = new ArrayList<>();
                if (task.getInputFiles() != null) {
                    String path = store.getCachePath(
                            Storage.RESOURCE.TASKS, task.getId(),
                            Constants.INPUTFOLDER).toString();
                    for (TaskFile tf: task.getInputFiles()) {
                        fTransf.add(path
                                + FileSystems.getDefault().getSeparator()
                                + tf.getName() + ">" + tf.getName());
                    }
                }
                if (task.getOutputFiles() != null) {
                    String path = store.getCachePath(
                            Storage.RESOURCE.TASKS, task.getId(),
                            Constants.OUTPUTFOLDER).toString();
                    for (TaskFile tf: task.getOutputFiles()) {
                        fTransf.add(tf.getName() + "<" + path
                                + FileSystems.getDefault().getSeparator()
                                + tf.getName());
                    }
                }
                if (fTransf.isEmpty()) {
                    String path = store.getCachePath(
                            Storage.RESOURCE.TASKS, task.getId(),
                            Constants.OUTPUTFOLDER).toString();
                    fTransf.add(Defaults.OUTPUT + "<" + path
                                + FileSystems.getDefault().getSeparator()
                            + Defaults.OUTPUT);
                }
                jd.setVectorAttribute(JobDescription.FILETRANSFER,
                        fTransf.toArray(new String[fTransf.size()]));
            }
        } catch (AuthenticationFailedException | AuthorizationFailedException
                | BadParameterException | DoesNotExistException
                | IncorrectStateException | NoSuccessException
                | NotImplementedException | PermissionDeniedException
                | TimeoutException ex) {
             LOG.error("Problem with the parameter '"
                     + JobDescription.EXECUTABLE
                     + "': " + ex.getMessage());
             throw new BadParameterException(ex.getMessage());
        }
        setOptionalParam(jd, prTask, JobDescription.ARGUMENTS, true);
        setOptionalParam(jd, prTask, JobDescription.SPMDVARIATION);
        setOptionalParam(jd, prTask, JobDescription.TOTALCPUCOUNT);
        setOptionalParam(jd, prTask, JobDescription.NUMBEROFPROCESSES);
        setOptionalParam(jd, prTask, JobDescription.PROCESSESPERHOST);
        setOptionalParam(jd, prTask, JobDescription.THREADSPERPROCESS);
        setOptionalParam(jd, prTask, JobDescription.ENVIRONMENT, true);
        setOptionalParam(jd, prTask, JobDescription.WORKINGDIRECTORY);
        setOptionalParam(jd, prTask, JobDescription.INTERACTIVE);
        setOptionalParam(jd, prTask, JobDescription.INPUT);
        setOptionalParam(jd, prTask, JobDescription.OUTPUT, Defaults.OUTPUT);
        setOptionalParam(jd, prTask, JobDescription.ERROR, Defaults.ERROR);
        setOptionalParam(jd, prTask, JobDescription.CLEANUP);
        setOptionalParam(jd, prTask, JobDescription.JOBSTARTTIME);
        setOptionalParam(jd, prTask, JobDescription.WALLTIMELIMIT);
        setOptionalParam(jd, prTask, JobDescription.TOTALCPUTIME);
        setOptionalParam(jd, prTask, JobDescription.TOTALPHYSICALMEMORY);
        setOptionalParam(jd, prTask, JobDescription.CPUARCHITECTURE);
        setOptionalParam(jd, prTask, JobDescription.OPERATINGSYSTEMTYPE);
        setOptionalParam(jd, prTask, JobDescription.CANDIDATEHOSTS, true);
        setOptionalParam(jd, prTask, JobDescription.QUEUE);
        setOptionalParam(jd, prTask, JobDescription.JOBPROJECT);
        setOptionalParam(jd, prTask, JobDescription.JOBCONTACT, true);
        return null;
    }


    /**
     * Sets an optional parameter in the job description.
     * Similar behaviour of {@code setOptionalParam(desc, prop, name, false)}
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     */
    private static void setOptionalParam(
            final JobDescription desc, final Properties prop,
            final String name) {
        setOptionalParam(desc, prop, name, null, false);
     }


    /**
     * Sets an optional parameter in the job description.
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     * @param multi True if the properties has multiple values, false otherwise.
     * If there are multiple values these are separated by ',' or ';'
     */
    private static void setOptionalParam(
            final JobDescription desc, final Properties prop,
            final String name, final boolean multi) {
        setOptionalParam(desc, prop, name, null, multi);
    }

    /**
     * Sets an optional parameter in the job description.
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     * @param dValue The default value of the parameter if not present
     */
    private static void setOptionalParam(
            final JobDescription desc, final Properties prop,
            final String name, final String dValue) {
        setOptionalParam(desc, prop, name, dValue, false);
    }

    /**
     * Sets an optional parameter in the job description.
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     * @param dValue The default value of the parameter if not present
     * @param multi True if the properties has multiple values, false otherwise.
     * If there are multiple values these are separated by ',' or ';'
     */
    private static void setOptionalParam(
            final JobDescription desc, final Properties prop,
            final String name, final String dValue, final boolean multi) {
        String value = prop.getProperty(name, dValue);
        if (value != null) {
            try {
                if (multi) {
                    desc.setVectorAttribute(name, value.split(",|;"));
                } else {
                    desc.setAttribute(name, value);
                }
            } catch (NotImplementedException | AuthenticationFailedException
                    | AuthorizationFailedException | PermissionDeniedException
                    | IncorrectStateException | BadParameterException
                    | DoesNotExistException | TimeoutException
                    | NoSuccessException ex) {
                LOG.warn("Problem with the attribute '" + name + "': "
                        + ex.getMessage());
            }
        }
    }


    /**
     * Sets a required parameter in the job description.
     * Similar behaviour of {@code setRequiredParam(desc, prop, name, false)}
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     * @throws AuthenticationFailedException JSAGA generated error
     * @throws AuthorizationFailedException JSAGA generated error
     * @throws BadParameterException JSAGA generated error or the parameter as
     * empty or null value
     * @throws DoesNotExistException JSAGA generated error
     * @throws IncorrectStateException JSAGA generated error
     * @throws NoSuccessException JSAGA generated error
     * @throws NotImplementedException JSAGA generated error
     * @throws PermissionDeniedException JSAGA generated error
     * @throws TimeoutException  JSAGA generated error
     */
    private static void setRequiredParam(
            final JobDescription desc, final Properties prop, final String name)
            throws AuthenticationFailedException, AuthorizationFailedException,
            BadParameterException, DoesNotExistException,
            IncorrectStateException, NoSuccessException,
            NotImplementedException, PermissionDeniedException,
            TimeoutException {
        setRequiredParam(desc, prop, name, false);
    }



    /**
     * Sets a required parameter in the job description.
     *
     * @param desc The job description to modify
     * @param prop The set of properties for the job
     * @param name The name of the properties to add
     * @param multi True if the properties has multiple values, false otherwise.
     * If there are multiple values these are separated by ',' or ';'
     * @throws AuthenticationFailedException JSAGA generated error
     * @throws AuthorizationFailedException JSAGA generated error
     * @throws BadParameterException JSAGA generated error or the parameter as
     * empty or null value
     * @throws DoesNotExistException JSAGA generated error
     * @throws IncorrectStateException JSAGA generated error
     * @throws NoSuccessException JSAGA generated error
     * @throws NotImplementedException JSAGA generated error
     * @throws PermissionDeniedException JSAGA generated error
     * @throws TimeoutException  JSAGA generated error
     */
    private static void setRequiredParam(
            final JobDescription desc, final Properties prop,
            final String name, final boolean multi)
            throws AuthenticationFailedException, AuthorizationFailedException,
            BadParameterException, DoesNotExistException,
            IncorrectStateException, NoSuccessException,
            NotImplementedException, PermissionDeniedException,
            TimeoutException {
        String value = prop.getProperty(name);
        if (value == null) {
            throw new BadParameterException(
                    "Missing required attribute: " + name);
        }
        try {
            if (multi) {
                desc.setVectorAttribute(name, value.split(",|;"));
            } else {
                desc.setAttribute(name, value);
            }
        } catch (NotImplementedException | AuthenticationFailedException
                | AuthorizationFailedException | PermissionDeniedException
                | IncorrectStateException | BadParameterException
                | DoesNotExistException | TimeoutException
                | NoSuccessException ex) {
            throw ex;
        }
    }
}
