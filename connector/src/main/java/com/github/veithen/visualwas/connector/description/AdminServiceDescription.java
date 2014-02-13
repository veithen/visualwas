/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.connector.description;

import java.io.IOException;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Operation;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.mapped.ClassMappingFeature;

/**
 * Description of a set of operations defined by WebSphere's admin service.
 * <p>
 * The server side of the SOAP JMX connector is basically a SOAP service (called admin service) that
 * exposes a set of operations. On the client side these operations are invoked through a dynamic
 * proxy that implements a set of interfaces that declare methods corresponding to the operations of
 * the admin service.
 * <p>
 * This interface encapsulates the descriptions of a subset of the operations defined by the admin
 * service. These descriptions are derived from a Java interface declaring the methods corresponding
 * to these SOAP operations. This Java interface will be added to the dynamic proxy when the client
 * side connector is created. Each method of the interface must satisfy the following requirements:
 * <ol>
 * <li>The method is annotated with {@link Operation @Operation} or the method name matches the SOAP
 * operation name.
 * <li>The method parameters are annotated with {@link Param @Param}.
 * <li>The method must declare {@link IOException}.
 * </ol>
 * <p>
 * As mentioned earlier, an instance of this interface describes a subset of the operations defined
 * by the admin service. The reason is that some of these operations (e.g. the operations related to
 * JMX notifications) refer to WebSphere specific classes. The code supporting these operations is
 * isolated into distinct {@link Feature features} because it requires {@link ClassMappingFeature
 * class mapping}. This only works if a feature is allowed to contribute admin service descriptions.
 * 
 * @see AdminServiceDescriptionFactory#createDescription(Class)
 * @see Configurator#addAdminServiceDescription(AdminServiceDescription)
 * @see AdminService#DESCRIPTION
 */
public interface AdminServiceDescription {
    Class<?> getInterface();
    OperationDescription getOperation(String name);
}
