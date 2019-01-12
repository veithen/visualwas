/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.connector.transport.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.feature.SOAPResponse;

public final class CannedResponse extends Response {
    private static OMMetaFactory domMetaFactory = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
    
    private final URL url;

    public CannedResponse(URL url) {
        this.url = url;
    }

    @Override
    CompletableFuture<SOAPResponse> produce(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final InputStream in = url.openStream();
                final SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(domMetaFactory, in, null).getSOAPEnvelope();
                return new SOAPResponse() {
                    @Override
                    public boolean isFault() {
                        return false;
                    }
                    
                    @Override
                    public SOAPEnvelope getEnvelope() {
                        return envelope;
                    }
                    
                    @Override
                    public void discard() throws IOException {
                        in.close();
                    }
                };
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }
}
