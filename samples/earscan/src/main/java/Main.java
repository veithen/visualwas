/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.security.BasicAuthCredentials;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;
import com.github.veithen.visualwas.repoclient.ConfigRepository;
import com.github.veithen.visualwas.repoclient.DocumentContentSource;
import com.github.veithen.visualwas.repoclient.RepositoryClientFeature;

public class Main {
    private static final Set<String> internalApps = new HashSet<>(Arrays.asList("commsvc", "WebSphereWSDM", "isclite", "OTiS", "ibmasyncrsp"));
    
    private static void scanArchive(InputStream in, boolean isWar, List<String> messages) throws Exception {
        JarInputStream jar = new JarInputStream(in);
        JarEntry entry;
        List<WebServiceImplementation> wsImplementations = new ArrayList<WebServiceImplementation>();
        WSDLResources wsdlResources = new WSDLResources();
        while ((entry = jar.getNextJarEntry()) != null) {
            String name = entry.getName();
            if ((!isWar || name.startsWith("WEB-INF/classes/")) && name.endsWith(".class")) {
                new ClassReader(jar).accept(new WebServiceAnnotationExtractor(wsImplementations), 0);
            } else if (!isWar && name.startsWith("META-INF/wsdl/")
                    || isWar && name.startsWith("WEB-INF/wsdl/")) {
                wsdlResources.add(name, IOUtils.toByteArray(jar));
            }
        }
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);
        for (WebServiceImplementation wsImplementation : wsImplementations) {
            String className = wsImplementation.getClassName();
            String wsdlLocation = wsImplementation.getWsdlLocation();
            if (wsdlLocation == null) {
                messages.add("[" + className + "] No wsdlLocation specified");
            } else {
                try {
                    Definition definitions = wsdlReader.readWSDL(wsdlResources.getWsdl(wsdlLocation));
                    QName serviceQName = new QName(wsImplementation.getTargetNamespace(), wsImplementation.getServiceName());
                    Service service = definitions.getService(serviceQName);
                    if (service == null) {
                        messages.add("[" + className + "] Service " + serviceQName + " not found in WSDL");
                    } else if (service.getPort(wsImplementation.getPortName()) == null) {
                        messages.add("[" + className + "] Port " + wsImplementation.getPortName() + " not found in service " + serviceQName);
                    }
                } catch (WSDLNotFoundException ex) {
                    messages.add("[" + className + "] Document not found or in wrong place: " + ex.getName());
                }
            }
        }
    }

    private static void scan(File earFile, List<String> messages) throws Exception {
        Set<String> ejbJars = new HashSet<>();
        Set<String> wars = new HashSet<>();
        
        JarInputStream ear = new JarInputStream(new FileInputStream(earFile));
        try {
            JarEntry entry;
            while ((entry = ear.getNextJarEntry()) != null) {
                if (entry.getName().equals("META-INF/application.xml")) {
                    OMElement app = OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.STANDALONE, ear).getDocumentElement();
                    for (Iterator it = app.getChildrenWithLocalName("module"); it.hasNext(); ) {
                        OMElement module = ((OMElement)it.next()).getFirstElement();
                        String type = module.getLocalName();
                        if (type.equals("ejb")) {
                            ejbJars.add(module.getText());
                        } else if (type.equals("web")) {
                            wars.add(((OMElement)module.getChildrenWithLocalName("web-uri").next()).getText());
                        } else {
                            System.err.println("Unknown module type: " + type);
                        }
                    }
                    break;
                }
            }
        } finally {
            ear.close();
        }
        
        ear = new JarInputStream(new FileInputStream(earFile));
        try {
            JarEntry entry;
            while ((entry = ear.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (ejbJars.contains(name)) {
                    scanArchive(ear, false, messages);
                } else if (wars.contains(name)) {
                    scanArchive(ear, true, messages);
                }
            }
        } finally {
            ear.close();
        }
    }

    private static void scanAll(Connector connector) throws Exception {
        String cell = connector.getServerMBean().getKeyProperty("cell");
        ConfigRepository configRepository = connector.getAdapter(ConfigRepository.class);
        for (String appResource : configRepository.listResourceNames("cells/" + cell + "/applications", 2, 1)) {
            String appName = appResource.substring(appResource.lastIndexOf('/')+1, appResource.length()-4);
            // Skip internal WebSphere applications
            if (internalApps.contains(appName)) {
                continue;
            }
            File earFile = File.createTempFile("app-" + appName, ".ear");
            try {
                DocumentContentSource source = configRepository.extract("cells/" + cell + "/applications/" + appName + ".ear/" + appName + ".ear");
                InputStream in = source.getSource().getInputStream();
                try {
                    FileOutputStream out = new FileOutputStream(earFile);
                    try {
                        IOUtils.copy(in, out);
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
                List<String> messages = new ArrayList<String>();
                scan(earFile, messages);
                if (!messages.isEmpty()) {
                    System.out.println(appName);
                    for (String message : messages) {
                        System.out.println("  " + message);
                    }
                }
            } finally {
                earFile.delete();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ConnectorConfiguration.Builder configBuilder = ConnectorConfiguration.custom();
        boolean securityEnabled;
        Attributes attributes = new Attributes();
        if (args.length > 2) {
            securityEnabled = true;
            attributes.set(Credentials.class, new BasicAuthCredentials(args[2], args[3]));
            configBuilder.setTransportConfiguration(TransportConfiguration.custom().setTrustManager(new DummyTrustManager()).build());
        } else {
            securityEnabled = false;
        }
        configBuilder.addFeatures(RepositoryClientFeature.INSTANCE);
        Endpoint endpoint = new Endpoint(args[0], Integer.parseInt(args[1]), securityEnabled);
        scanAll(ConnectorFactory.getInstance().createConnector(endpoint, configBuilder.build(), attributes));
    }
}
