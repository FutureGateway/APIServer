/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.infn.ct.futuregateway.apiserver.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.ws.rs.NameBinding;
import javax.ws.rs.core.Response;

/**
 * Status annotation for rest method.
 * Allow to specify a http status code different from the default 200 reported
 * by many operation using JAX-RS. The method should be annotated with
 * <code>@Status(<status_value>)</code>.
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Status {

    /**
     * Define the annotation value.
     * The annotation requires a Reponse.Status type
     *
     * @return The status defined in the annotation
     * @see javax.ws.rs.core.Response.Status
     */
    Response.Status value() default Response.Status.OK;
}
