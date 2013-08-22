package com.github.veithen.visualwas.connector.mapped;

public interface ClassMappingConfigurator {
    /**
     * Add the given mapped classes to the class mapper. These classes must be annotated with
     * {@link MappedClass}. This method automatically locates mapped classes that are dependencies
     * of the specified classes. This is done recursively.
     * 
     * @param classes
     *            the mapped classes to add (and to scan for dependencies)
     */
    void addMappedClasses(Class<?>... classes);
}
