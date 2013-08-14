package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.Interceptor;
import com.github.veithen.visualwas.connector.loader.AlternateClass;

/**
 * Defines the API used by {@link Feature} objects to configure a connector instance. Instances of
 * this instance are only valid during the invocation of
 * {@link Feature#configureConnector(ConnectorConfigurator)}.
 */
public interface ConnectorConfigurator {
    void addInterceptor(Interceptor interceptor);
    
    /**
     * Add the given alternate classes to the class mapper. These classes must be annotated with
     * {@link AlternateClass}. This method automatically locates alternate classes that are
     * dependencies of the specified classes. This is done recursively.
     * 
     * @param classes
     *            the alternate classes to add (and to scan for dependencies)
     */
    void addAlternateClasses(Class<?>... classes);
}
