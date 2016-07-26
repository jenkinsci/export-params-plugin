/*
 *  The MIT License
 *
 *  Copyright 2016 rinrinne All rights reserved.
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
package org.jenkinsci.plugins.exportparams;

import hudson.Extension;
import hudson.EnvVars;
import hudson.model.TaskListener;
import hudson.model.Run;

import java.io.IOException;
import java.util.List;

import hudson.model.EnvironmentContributor;

/*
 * An extension class to contribute params to build.
 */
@Extension
public class ExportParametersEnvironmentContributor extends EnvironmentContributor {

    @Override
    public void buildEnvironmentFor(Run r, EnvVars envs, TaskListener listener) throws IOException, InterruptedException {
        List<ExportParametersInvisibleAction> actions = r.getActions(ExportParametersInvisibleAction.class);
        for(ExportParametersInvisibleAction action : actions) {
            EnvVars param = action.getContributeParams();
            for (String key : param.keySet()) {
                envs.put(key, param.get(key, ""));
            }
        }
    }
}
