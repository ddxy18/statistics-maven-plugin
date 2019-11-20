package com.dxy.plugins;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

@Mojo(name = "counter", defaultPhase = LifecyclePhase.VERIFY)
public class MyMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
    private File srcDir;

    @Parameter(defaultValue = "${project.build.testSourceDirectory}", readonly = true)
    private File testDir;

    public void execute() throws MojoExecutionException {
        File srcMain = srcDir;
        File srcTest = testDir;

        try {
            System.out.println("src lines:" + countDirLines(srcMain));
            System.out.println("test lines:" + countDirLines(srcTest));
        } catch (IOException e) {
            throw new MojoExecutionException("Error counting project lines ", e);
        }
    }

    private int countDirLines(File file) throws IOException {
        if (!file.isDirectory()) {
            return countFileLines(file);
        } else {
            int lines = 0;
            File[] files = file.listFiles();
            // 排除对空文件夹的计数
            if (files != null) {
                for (File f : files) {
                    lines += countDirLines(f);
                }
                return lines;
            }
            return 0;
        }
    }

    /*
     * 对非文件夹的File对象的计数
     *
     * @param file 必须是非文件夹对象
     */
    private int countFileLines(File file) throws IOException {
        FileReader reader = new FileReader(file);
        LineNumberReader lineReader = new LineNumberReader(reader);
        int lines = 0;
        while (lineReader.readLine() != null) {
            lines++;
        }
        lineReader.close();
        return lines;
    }
}
