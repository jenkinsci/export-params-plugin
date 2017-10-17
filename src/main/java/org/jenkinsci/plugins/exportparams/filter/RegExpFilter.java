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

import java.util.regex.Pattern;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;

/**
 * A class to filter parameters by plain list.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class RegExpFilter extends Filter {

    /**
     * Constructor.
     *
     * @param pattern the pattern.
     */
    public RegExpFilter(String pattern) {
        super(pattern);
    }

    @Override
    public EnvVars apply(AbstractBuild<?, ?> build) {
        EnvVars env = new EnvVars();
        Pattern regExp;
        try {
            regExp = Pattern.compile(pattern);
        } catch (Exception ex) {
            regExp = Pattern.compile(".*");
        }

        ParametersAction action = build.getAction(ParametersAction.class);
        if (action != null) {
            for (ParameterValue param : action.getParameters()) {
                if (regExp.matcher(param.getName()).matches()) {
                    param.buildEnvironment(build, env);
                }
            }
        }
        return env;
    }
}
