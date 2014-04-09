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
package org.jenkinsci.plugins.exportparams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.exportparams.filter.Filter;
import org.jenkinsci.plugins.exportparams.filter.FilterFactory;
import org.jenkinsci.plugins.exportparams.model.FileType;
import org.jenkinsci.plugins.exportparams.serializer.Serializer;
import org.jenkinsci.plugins.exportparams.serializer.SerializerFactory;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

/**
 * A builder to export parameters to file.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class ExportParametersBuilder extends Builder {

    private final static String KEY_EXPORT_PARAMS_FILE = "EXPORT_PARAMS_FILE";
    private final static String KEY_EXPORT_PARAMS_FORMAT = "EXPORT_PARAMS_FORMAT";

    private final String filePath;
    private final String fileFormat;
    private final String keyPattern;
    private final boolean useRegexp;

    /**
     * Constructor.
     *
     * @param filePath the file path.
     * @param fileFormat the file format.
     * @param keyPattern the pattern of key.
     * @param useRegexp true if pattern in regular expression.
     */
    @DataBoundConstructor
    public ExportParametersBuilder(String filePath, String fileFormat, String keyPattern, boolean useRegexp) {
        this.filePath = filePath;
        this.fileFormat = fileFormat;
        this.keyPattern = keyPattern;
        this.useRegexp = useRegexp;
    }

    /**
     * Gets file path.
     * @return the file path.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets file format.
     * @return the file format.
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Gets key pattern.
     * @return the key pattern.
     */
    public String getKeyPattern() {
        return keyPattern;
    }

    /**
     * Whether use regexp or not.
     * @return true if use regexp.
     */
    public boolean isUseRegexp() {
        return useRegexp;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        if (StringUtils.isNotEmpty(filePath)) {
            String path;
            if (filePath.endsWith(".")) {
                path = filePath.substring(0, filePath.length() - 1);
            } else {
                path = filePath + "." + fileFormat;
            }

            FilePath paramFile = new FilePath(build.getWorkspace(), path);
            Filter filter = FilterFactory.createFilter(keyPattern, useRegexp);
            EnvVars env = filter.apply(build);

            Serializer serializer = SerializerFactory.createSerializer(fileFormat);
            if (serializer != null) {
                String buf = serializer.serialize(env);
                if (buf != null) {
                    try {
                        paramFile.delete();
                        paramFile.write(buf, CharEncoding.UTF_8);
                        listener.getLogger().println("Stored the below parameters into " + paramFile.getRemote());
                        for (String key : env.keySet()) {
                            listener.getLogger().println(key);
                        }
                        build.addAction(createParametersAction(paramFile.getRemote(), fileFormat));
                    } catch (Exception ex) {
                        listener.getLogger().println("Could not store parameters into " + paramFile.getRemote());
                    }
                }
            }
        }
        return true;
    }

    private ParametersAction createParametersAction(String filePath, String fileFormat) {
        List<ParameterValue> params = new ArrayList<ParameterValue>();
        params.add(new StringParameterValue(KEY_EXPORT_PARAMS_FILE, filePath));
        params.add(new StringParameterValue(KEY_EXPORT_PARAMS_FORMAT, fileFormat));
        return new ParametersAction(params);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * A implementation of Descliptor.
     *
     * @author rinrinne (rinrin.ne@gmail.com)
     */
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private final Collection<FileType> fileTypes = new ArrayList<FileType>() {
            private static final long serialVersionUID = 1L;
        {
           add(new FileType(Format.PROPERTIES));
           add(new FileType(Format.XML));
           add(new FileType(Format.JSON));
           add(new FileType(Format.YAML));
        }};

        @Override
        public String getDisplayName() {
            return Messages.PluginBuilderDescription();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Gets file types.
         * @return the collection of file type.
         */
        public Collection<FileType> getFileTypes() {
            return fileTypes;
        }
    }

}
