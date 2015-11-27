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

import it.infn.ct.futuregateway.apiserver.utils.Constants;
import it.infn.ct.futuregateway.apiserver.v1.resources.Task;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFile;
import it.infn.ct.futuregateway.apiserver.v1.resources.TaskFileInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * The TaskService provide the REST APIs for the task
 * as defined in the documentation.
 *
 * @see http://docs.csgfapis.apiary.io/#reference/v1.0/task
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@Path("/tasks/{id}")
public class TaskService extends BaseService {
    /**
     * Logger object.
     * Based on apache commons logging.
     */
    private final Log log = LogFactory.getLog(TaskService.class);

    /**
     * Retrieve the task details.
     * Task details include all the field a task consist of as described in the
     * documentation. This include all the information included in the task
     * collection and many others.
     *
     * @param id The task id. This is a path parameter retrieved from the url
     * @return The task
     */
    @GET
    @Produces(Constants.INDIGOMIMETYPE)
    public final Task getTaskDetails(@PathParam("id") final String id) {
        Task task;
        EntityManager em = getEntityManager();
        try {
            task = em.find(Task.class, id);
            log.error("Associated " + task.getInputFiles().size() + " files");
        } catch (RuntimeException re) {
            log.error("Impossible to retrieve the task list");
            log.error(re);
            throw new RuntimeException("Impossible to access the task list");
        } finally {
            em.close();
        }
        return task;
    }


    /**
     * Upload input files.
     * The method store input files for the specified task. Input files are
     * provided as a <i>multipart form data</i> using the field file. This can
     * contains multiple file using the html input attribute
     * <i>multiple="multiple"</i> which allows to associate multiple files with
     * a single field.
     *
     * @param id The task id retrieved from the url path
     * @param lstFiles List of file in the POST body
     */
    @Path("/input")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public final void setInputFile(@PathParam("id") final String id,
            @FormDataParam("file") final List<FormDataBodyPart> lstFiles) {
        if (lstFiles == null || lstFiles.isEmpty()) {
            throw new BadRequestException("Input not accessible!");
        }
        EntityManager em = getEntityManager();
        Task task = em.find(Task.class, id);
        if (task == null) {
            throw new NotFoundException("Task " + id + " does not exist");
        }
        for (FormDataBodyPart fdbp: lstFiles) {
            final String fName =
                    fdbp.getFormDataContentDisposition().getFileName();
            try {
                storeFile(id, fdbp.getValueAs(InputStream.class),
                        fName);
                EntityTransaction et = em.getTransaction();
                try {
                    et.begin();
                    TaskFileInput tfi = IterableUtils.find(task.getInputFiles(),
                            new Predicate<TaskFileInput>() {
                                @Override
                                public boolean evaluate(final TaskFileInput t) {
                                    return t.getName().equals(fName);
                                }
                            });
                    tfi.setStatus(TaskFile.FILESTATUS.READY);
                    et.commit();
                } catch (RuntimeException re) {
                    if (et != null && et.isActive()) {
                        et.rollback();
                    }
                    log.error(re);
                    log.error("Impossible to update the task");
                    em.close();
                    throw new InternalServerErrorException("Errore to update "
                            + "the task");
                }
            } catch (IOException ex) {
                log.error(ex);
                throw new InternalServerErrorException("Errore to store input "
                        + "files");
            }
        }
        em.close();
    }


    /**
     * Create the directory to store the input.
     * Create a directory inside the temporary store with path
     * "<taskId>/input". This is used to store the input file before the
     * submission.
     *
     * @param taskId The ID of the task to associate the files
     * @param input InputStream of the file to store
     * @param destinationName File destination
     * @throws IOException In case the file cannot be written
     */
    private void storeFile(final String taskId, final InputStream input,
            final String destinationName) throws IOException {
        java.nio.file.Path filePath =
                Paths.get(getCacheDirPath(), taskId, "input", destinationName);

        Files.createDirectories(filePath.getParent());
        Files.copy(input, filePath);
    }
}
