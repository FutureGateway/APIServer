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

package it.infn.ct.futuregateway.apiserver.v1.resources;

import it.infn.ct.futuregateway.apiserver.utils.LinkJaxbAdapter;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

/**
 * The Infrastructure represents the remote infrastructure where application
 * can execute.
 * An infrastructure describes all the relevant information to allow the access
 * and execution of applications and services in a remote infrastructure.
 * <p>
 * Infrastructures are identified by a set of parameters which provide all the
 * information needed to allow the system execute application or call service
 * on behalf of the user. Parameter differs for different applications and these
 * are as follow.
 * <p>
 * Infrastructures are differentiate using the attribute <b><i>type</i></b>.
 * This can have one of the following values: <i>wsgram</i>, <i>gatekeeper</i>,
 * <i>wms</i>, <i>occi</i>, <i>unicore</i>, <i>ourgrid</i>,
 * <i>bes-genesis2</i>, <i>gos</i>, <i>ssh</i> and <i>tosca</i>. If omitted, it
 * will be evaluated from the parameter <b><i>jobservice</i></b>.
 * <p>
 * <dl>
 *  <dt><span class="strong">Common to all infrastructures</span></dt>
 *  <dd>
 *      <ul>
 *          <li>
 *              <b>type</b>: the type of infrastructure. This can have one of
 *              the following values: <i>wsgram</i>, <i>gatekeeper</i>,
 *              <i>wms</i>, <i>rocci</i>, <i>unicore</i>, <i>ourgrid</i>,
 *              <i>bes-genesis2</i>, <i>gos</i>, <i>ssh</i> and <i>tosca</i>.
 *              If omitted, it will be evaluated from the parameter
 *              <b><i>jobservice</i></b>.
 *          </li>
 *          <li><b>jobservice</b>: URI of the entity accepting jobs on the
 *          remote infrastructure. Generally, the protocol in the URI is not
 *          the communication protocol but identify the adaptor to use. E.g. to
 *          create a VM using rocci adaptor the URI will be like
 *          <i>rocci://&ltremote_server&gt:&ltremote_port&gt</i>
 *          </li>
 *      </ul>
 *  </dd>
 *
 *  <dt><span class="strong">gLite</span></dt>
 *  <dd>
 *      <ul>
 *          <li>
 *              <b>retrycount</b>: Number of re-submissions in case of failure.
 *              Default value is 3. Applicable only for type <i>wms</i>.
 *          </li>
 *          <li>
 *              <b>myproxyserver</b>: Hostname of a myproxyserver. Applicable
 *              only for type <i>wms</i>.
 *          </li>
 *          <li>
 *              <b>proxyurl</b>: URL of a valid proxy. It can be a local file
 *              to the server of remote file accessed using http(s) or other
 *              java enabled protocols. This has precedence to other parameters
 *              like the etokenserverurl with the related parameters for the
 *              query.
 *          </li>
 *
 *          <li>
 *              <b>etokenserverurl</b>: URL of the eToken Server. The URL can
 *              include everything but the query string specifying the proxy
 *              parameters (protocol, hostname, path, etc...). If some
 *              parameters in the query string do not change then can also be
 *              included in the url and the system will append the others.
 *          </li>
 *          <li>
 *              <b>etokenid</b>: The id of the token to retrieve from the eToken
 *              server.
 *          </li>
 *          <li><b>vo</b>: Name of the VO the proxy is binded to.</li>
 *          <li><b>voroles</b>: FQAN of the roles to integrate in the proxy</li>
 *          <li>
 *              <b>proxyrenewal</b>: A flag to indicate if the proxy renewal has
 *              to be enabled. Default is <i>false</i>.
 *          </li>
 *          <li>
 *              <b>disablevomsproxy</b>: A flag to indicate if the voms proxy
 *              has to be disabled. Dafault is <i>false</i>.
 *          </li>
 *          <li>
 *              <b>rfcproxy</b>: A flag indicating if the proxy has to be RFC
 *              compliant.
 *          </li>
 *      </ul>
 *  </dd>
 *
 *  <dt><span class="strong">OCCI</span></dt>
 *  <dd>
 *      <ul>
 *          <li>
 *              <b>sshpublickey</b>: Path to the public key to use for the ssh
 *              connection after the VM start. Default to
 *              <i>$HOME/sshkeys/id_rsa.pub</i>
 *          </li>
 *          <li>
 *              <b>sshprivatekey</b>: Path to the private key to use for the ssh
 *              connection after the VM start. Default to
 *              <i>$HOME/sshkeys/id_rsa</i>
 *          </li>
 *          <li>
 *              <b>proxyurl</b>: URL of a valid proxy. It can be a local file
 *              to the server of remote file accessed using http(s) or other
 *              java enabled protocols. This has precedence to other parameters
 *              like the etokenserverurl with the related parameters for the
 *              query.
 *          </li>
 *
 *          <li>
 *              <b>etokenserverurl</b>: URL of the eToken Server. The URL can
 *              include everything but the query string specifying the proxy
 *              parameters (protocol, hostname, path, etc...). If some
 *              parameters in the query string do not change then can also be
 *              included in the url and the system will append the others.
 *          </li>
 *          <li>
 *              <b>etokenid</b>: The id of the token to retrieve from the eToken
 *              server.
 *          </li>
 *          <li><b>vo</b>: Name of the VO the proxy is binded to.</li>
 *          <li><b>voroles</b>: FQAN of the roles to integrate in the proxy</li>
 *          <li>
 *              <b>proxyrenewal</b>: A flag to indicate if the proxy renewal has
 *              to be enabled. Default is <i>false</i>.
 *          </li>
 *          <li>
 *              <b>disablevomsproxy</b>: A flag to indicate if the voms proxy
 *              has to be disabled. Dafault is <i>false</i>.
 *          </li>
 *          <li>
 *              <b>rfcproxy</b>: A flag indicating if the proxy has to be RFC
 *              compliant.
 *          </li>
 *      </ul>
 *  </dd>
 *
 *  <dt><span class="strong">SSH</span></dt>
 *  <dd>
 *      <ul>
 *          <li><b>username</b>: Username for the ssh connection.</li>
 *          <li>
 *              <b>password</b>: Password for the associated username (in case
 *              the authentication is made with username/password).
 *          </li>
 *      </ul>
 *  </dd>
 * </dl>
 *
 * @author Marco Fargetta <marco.fargetta@ct.infn.it>
 */
@NamedQuery(name = "infrastructures.all",
        query = "SELECT i FROM Infrastructure i")

@Entity
@Table(name = "Infrastructure")

@InjectLinks({
    @InjectLink(value = "infrastructures/{id}", rel = "self"),
})

@XmlRootElement(name = "infrastructure")
@XmlAccessorType(XmlAccessType.FIELD)
public class Infrastructure extends AccessibleElements {

    /**
     * List of references.
     */
    @InjectLinks(value = {
        @InjectLink(value = "infrastructures/{id}", rel = "self")})
    @XmlElement(name = "_links")
    @XmlJavaTypeAdapter(value = LinkJaxbAdapter.class)
    private List<Link> links;


    /**
     * Initialise the id.
     *
     * The id for the task is generated with a random uuid. There is not a
     * collision control at this level. If the persistence failed because of
     * this Id is repeated the code at higher level should manage the situation
     * by replacing the Id.
     */
    @PrePersist
    private void generateId() {
        if (this.getId() == null || this.getId().isEmpty()) {
            this.setId(UUID.randomUUID().toString());
        }
    }

}
