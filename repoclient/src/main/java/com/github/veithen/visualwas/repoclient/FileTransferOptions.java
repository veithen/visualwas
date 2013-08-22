package com.github.veithen.visualwas.repoclient;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.altclasses.AlternateClass;

@AlternateClass("com.ibm.ws.management.filetransfer.client.FileTransferOptionsImpl")
public class FileTransferOptions implements Serializable {
    private static final long serialVersionUID = 2117824156845393319L;

    private boolean compress;
    private boolean deleteOnCompletion;
    
    public boolean isCompress() {
        return compress;
    }
    
    public void setCompress(boolean compress) {
        this.compress = compress;
    }
    
    public boolean isDeleteOnCompletion() {
        return deleteOnCompletion;
    }
    
    public void setDeleteOnCompletion(boolean deleteOnCompletion) {
        this.deleteOnCompletion = deleteOnCompletion;
    }
}
