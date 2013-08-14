package com.github.veithen.visualwas.connector.loader;

public interface ClassMapper {
    ClassMapper NULL = new ClassMapper() {
        @Override
        public String getReplacementClass(String originalClass) {
            return null;
        }
        
        @Override
        public String getOriginalClass(String replacementClass) {
            return null;
        }
    };
    
    String getReplacementClass(String originalClass);
    String getOriginalClass(String replacementClass);
}
