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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONException;

import org.jenkinsci.plugins.exportparams.model.Parameter;

import hudson.EnvVars;

/**
 * A serializer for JSON.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class JSONSerializer implements Serializer {

    /**
     * Gets serialized string with properties format.
     * {@inheritDoc}
     *
     * @param env the env.
     * @return serialized string.
     */
    public String serialize(EnvVars env) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        for (Map.Entry<String,String> e : env.entrySet()) {
            Parameter param = new Parameter();
            param.key = e.getKey();
            param.value = e.getValue();
            params.add(param);
        }
        JSON json = net.sf.json.JSONSerializer.toJSON(params);
        String buf = null;
        if (json != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStreamWriter osw;
            try {
                osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                json.write(osw);
                osw.flush();
                ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                StringBuilder builder = new StringBuilder();

                char[] charBuf = new char[1024];
                int numRead;

                while (0 <= (numRead = reader.read(charBuf))) {
                    builder.append(charBuf, 0, numRead);
                }
                buf = builder.toString();
                is.close();
            } catch (Exception ex) {
                buf = null;
            } finally {
                try {
                    os.close();
                } catch (Exception ex) {
                    os = null;
                }
            }
        }
        return buf;
    }

}
