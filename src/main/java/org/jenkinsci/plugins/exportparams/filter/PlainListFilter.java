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

import java.util.Arrays;
import java.util.Collection;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;

/**
 * A class to filter parameters by plain list.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class PlainListFilter extends Filter {

    private final Collection<String> prefs;

    /**
     * Constructor.
     *
     * @param pattern the pattern.
     */
    public PlainListFilter(String pattern) {
        super(pattern);
        String ptn = pattern;
        if (pattern == null) {
            ptn = "";
        }
        this.prefs = Arrays.asList(ptn.split(","));
    }

    /**
     * Gets the list of prefix.
     *
     * @return the list of prefix.
     */
    public Collection<String> getPrefixes() {
        return prefs;
    }

    @Override
    public EnvVars apply(AbstractBuild<?, ?> build) {
        EnvVars env = new EnvVars();
        ParametersAction action = build.getAction(ParametersAction.class);
        if (action != null) {
            for (ParameterValue param : getParameters(action)) {
                if (prefs.size() == 0) {
                    param.buildEnvVars(build, env);
                } else {
                    for (String pref : prefs) {
                        if (param.getName().startsWith(pref)) {
                            param.buildEnvVars(build, env);
                            break;
                        }
                    }
                }
            }
        }
        return env;
    }

}
