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
package org.jenkinsci.plugins.exportparams.filter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;

/**
 * A interface for filter.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public abstract class Filter {
    /**
     * Pattern.
     */
    protected final String pattern;

    /**
     * Constructor.
     *
     * @param pattern the pattern.
     */
    public Filter(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Gets pattern.
     *
     * @return the pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Gets parameters from action.
     *
     * @return the list of parameters.
     */
    public List<ParameterValue> getParameters(ParametersAction action) {
      Method m;
      try {
        m = ParametersAction.class.getMethod("getAllParameters", null);
      } catch (NoSuchMethodException e) {
        try {
          m = ParametersAction.class.getMethod("getParameters", null);
        } catch (NoSuchMethodException ex) {
          return Collections.emptyList();
        }
      }
      List<ParameterValue> params;
      try {
        params = (List<ParameterValue>) m.invoke(action, null);
      } catch(Exception e) {
        params = Collections.emptyList();
      }
      return params;
    }

    /**
     * Apply filter.
     *
     * @param build the build.
     * @return filtered parameters.
     */
    public abstract EnvVars apply(AbstractBuild<?, ?> build);
}
