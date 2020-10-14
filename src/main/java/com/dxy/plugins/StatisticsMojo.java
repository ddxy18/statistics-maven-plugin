/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dxy.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

@Mojo(name = "statistics", defaultPhase = LifecyclePhase.VERIFY)
public class StatisticsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
    private File srcDir;

    public void execute() throws MojoExecutionException {
        try {
            System.out.println("lines:" + dirLines(srcDir));
        } catch (IOException e) {
            throw new MojoExecutionException("Error counting project lines ", e);
        }
    }

    /**
     * for a directory
     *
     * @param file a directory
     * @return all lines in *.java files in a directory
     * @throws IOException IO operations for file
     */
    private int dirLines(File file) throws IOException {
        if (!file.isDirectory()) {
            return fileLines(file);
        } else {
            int lines = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    lines += dirLines(f);
                }
            }
            return lines;
        }
    }

    /**
     * for a single java file
     *
     * @param file a single java file
     * @return lines in the file
     * @throws IOException IO operations for file
     */
    private int fileLines(File file) throws IOException {
        int lines = 0;

        if (file.getName().contains(".java")) {  // must be a valid java file
            FileReader reader = new FileReader(file);
            LineNumberReader lineReader = new LineNumberReader(reader);

            while (lineReader.readLine() != null) {
                lines++;
            }
            lineReader.close();
        }

        return lines;
    }
}
