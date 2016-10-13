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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;

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
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

/**
 * A builder to export parameters to file.
 *
 * @author rinrinne (rinrin.ne@gmail.com)
 */
public class ExportParametersBuilder extends Builder {

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
                        paramFile.act(new ParametersExporter(buf));
                        listener.getLogger().println("Stored the below parameters into " + paramFile.getRemote());
                        for (String key : env.keySet()) {
                            listener.getLogger().println(key);
                        }
                        build.addAction(new ExportParametersInvisibleAction(paramFile.getRemote(), fileFormat));
                    } catch (Exception ex) {
                        listener.getLogger().println("Could not store parameters into " + paramFile.getRemote());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * A class to export parameters to a file.
     *
     * @author rinrinne (rinrin.ne@gmail.com)
     */
    private static final class ParametersExporter implements FileCallable<Void> {

        private static final long serialVersionUID = 1;
        private final String buffer;

        /**
         * Constructor.
         *
         * @param parameters the string that contanins parameters.
         */
        public ParametersExporter(String parameters) {
            this.buffer = parameters;
        }

        @Override
        public Void invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
            if (file.exists()) {
                file.delete();
            }
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), CharEncoding.UTF_8));
            bw.write(buffer);
            bw.close();
            return null;
        }
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
