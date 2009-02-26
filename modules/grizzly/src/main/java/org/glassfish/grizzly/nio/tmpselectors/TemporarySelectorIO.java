/*
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */

package org.glassfish.grizzly.nio.tmpselectors;

import org.glassfish.grizzly.Grizzly;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Level;
import org.glassfish.grizzly.Reader;
import org.glassfish.grizzly.Writer;
import org.glassfish.grizzly.nio.SelectorFactory;

/**
 *
 * @author oleksiys
 */
public class TemporarySelectorIO {
    protected TemporarySelectorPool selectorPool;

    private volatile Reader reader;
    private volatile Writer writer;

    public TemporarySelectorIO() {
        this(null, null);
    }

    public TemporarySelectorIO(Reader reader, Writer writer) {
        this(reader, writer, null);
    }

    public TemporarySelectorIO(Reader reader, Writer writer,
            TemporarySelectorPool selectorPool) {
        this.selectorPool = selectorPool;
    }

    public TemporarySelectorPool getSelectorPool() {
        return selectorPool;
    }

    public void setSelectorPool(TemporarySelectorPool selectorPool) {
        this.selectorPool = selectorPool;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
    protected void recycleTemporaryArtifacts(Selector selector,
            SelectionKey selectionKey) {
        
        if (selectionKey != null) {
            try {
                selectionKey.cancel();
            } catch (Exception e) {
                Grizzly.logger.log(Level.WARNING,
                        "Unexpected exception, when canceling the SelectionKey: " +
                        selectionKey, e);
            }
        }

        if (selector != null) {
            try {
                selector.selectNow();
                selectorPool.offer(selector);
            } catch (IOException e) {
                Grizzly.logger.log(Level.WARNING,
                        "Temporary Selector failure. Creating new one", e);
                try {
                    selectorPool.offer(SelectorFactory.instance().create());
                } catch (IOException ee) {
                    Grizzly.logger.log(Level.WARNING,
                            "Error creating new Selector", ee);
                }
            }
        }
    }
}
