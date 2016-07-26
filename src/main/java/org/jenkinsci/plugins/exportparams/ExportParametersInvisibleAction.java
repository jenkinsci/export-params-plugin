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

import hudson.EnvVars;

import hudson.model.InvisibleAction;

/*
 * An Action class to contribute EnvVars implicitly.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class ExportParametersInvisibleAction extends InvisibleAction {

    private final static String KEY_EXPORT_PARAMS_FILE = "EXPORT_PARAMS_FILE";
    private final static String KEY_EXPORT_PARAMS_FORMAT = "EXPORT_PARAMS_FORMAT";

    private final String filePath;
    private final String fileFormat;

    /*
     * Constructor.
     *
     * @param filePath the file path.
     * @param fileFormat the file format.
     */
    public ExportParametersInvisibleAction(String filePath, String fileFormat) {
        super();
        this.filePath = filePath;
        this.fileFormat = fileFormat;
    }

    /*
     * Gets file path.
     *
     * @return the file path.
     */
    public String getFilePath() {
        return filePath;
    }

    /*
     * Gets file format.
     *
     * @return the file format.
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /*
     * Gets params to contribute as EnvVars.
     *
     * @return the params stored into EnvVars.
     */
    public EnvVars getContributeParams() {
        EnvVars envs = new EnvVars();
        envs.put(KEY_EXPORT_PARAMS_FILE, filePath);
        envs.put(KEY_EXPORT_PARAMS_FORMAT, fileFormat);
        return envs;
    }
}
