/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.example.lines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.BaseGenerator;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class Lines extends BaseGenerator {
    public static Lines from(Reader reader) {
        return new Lines(reader);
    }

    public static Lines from(File file) throws FileNotFoundException {
        return new Lines(new FileReader(file));
    }

    public Lines(Reader reader) {
        if(reader instanceof BufferedReader) {
            in = (BufferedReader)reader;
        } else {
            in = new BufferedReader(reader);
        }
    }
    
    public void run(UnaryProcedure proc) {
        try {
            for(String line = in.readLine(); line != null; line = in.readLine()) {
                proc.run(line);
            }
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new TunneledException(e);
        } finally {
            stop();
        }
    }

    public void stop() {
        super.stop();
        try {
            in.close();
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new TunneledException(e);
        }
    }

    private BufferedReader in = null;
    
    private class TunneledException extends RuntimeException {
        TunneledException(Exception e) {
            super(e.toString());
            exception = e;
        }        
        private Exception exception = null;
    }
}
