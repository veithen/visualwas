package com.github.veithen.visualwas.connector.altclasses;

public interface AlternateClassesConfigurator {
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
