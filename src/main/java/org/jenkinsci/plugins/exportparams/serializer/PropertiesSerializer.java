/*
 *  The MIT License
 *
 *  Copyright 2014 rinrinne All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jenkinsci.plugins.exportparams.serializer;

import hudson.EnvVars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * A serializer for Java properties.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class PropertiesSerializer implements Serializer {

    /**
     * Gets serialized string with properties format.
     * @inheritDoc
     * @param env the env.
     * @return serialized string.
     */
    public String serialize(EnvVars env) {
        Properties props = new Properties();
        for (Map.Entry<String,String> e : env.entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamWriter osw;
        try {
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        }
        String buf = null;
        try {
            doStore(props, osw);
            buf = os.toString(StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                os.close();
            } catch (Exception ex) {
                os = null;
            }
        }
        return buf;
    }

    /**
     * Store properties to output stream.
     *
     * @param props properties.
     * @param writer writer for output stream.
     * @throws IOException throw if any.
     */
    public void doStore(Properties props, Writer writer) throws IOException {
       props.store(writer, null);
    }
}
