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
import java.util.HashMap;
import java.util.Map;

@Mojo(name = "statistics", defaultPhase = LifecyclePhase.VERIFY)
public class StatisticsMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException {
        try {
            calculateStatistics(srcDir);
            System.out.println("files:" + files);
            System.out.println("code:" + codeLines);
            System.out.println("comment:" + commentLines);
            System.out.println("lines:" + lines);
        } catch (IOException e) {
            throw new MojoExecutionException("Error counting project lines ", e);
        }
    }

    /**
     * @param file a directory or a file
     * @throws IOException IO operations for file may lead to IOException
     */
    private void calculateStatistics(File file) throws IOException {
        if (!file.isDirectory()) {
            calculateFileStatistics(file);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    calculateStatistics(f);
                }
            }
        }
    }

    /**
     * for a single java file
     *
     * @param file a single java file
     * @throws IOException IO operations for file may lead to IOException
     */
    private void calculateFileStatistics(File file) throws IOException {
        // detect whether it is a valid java file
        if (file.getName().endsWith(".java")) {
            files++;

            FileReader reader = new FileReader(file);
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            boolean isInBlockComment = false;
            int curCharLoc;  // the current character's location in the line
            boolean haveComment, haveCode;

            while ((line = lineReader.readLine()) != null) {
                lines++;
                curCharLoc = 0;
                haveComment = false;
                haveCode = false;

                while (curCharLoc != line.length()) {
                    if (isInBlockComment) {
                        curCharLoc = skipBlockComment(line, curCharLoc);
                        haveComment = true;
                        if (curCharLoc == -1) {
                            curCharLoc = line.length();
                        } else {
                            isInBlockComment = false;
                        }
                    } else {
                        int index = indexOfComment(line, curCharLoc);

                        if (index == -1) {  // no comment until the line ending
                            if (isDelim(line, curCharLoc, line.length())) {
                                haveCode = true;
                            }
                            curCharLoc = line.length();
                        } else {
                            if (isDelim(line, curCharLoc, index - 1)) {
                                haveCode = true;
                            }

                            if (line.charAt(index + 1) == '/') {  // line comment
                                haveComment = true;
                                curCharLoc = line.length();
                            } else {  // block comment
                                haveComment = true;
                                isInBlockComment = true;
                                curCharLoc = index + 2;
                            }
                        }
                    }
                }

                if (haveCode) {
                    codeLines++;
                } else if (haveComment) {
                    commentLines++;
                }
            }
            lineReader.close();
        }
    }

    /**
     * @param line       the current line to be scanned
     * @param curCharLoc one of the following choice:
     *                   the begin location of the block comment, say the
     *                   location of / in the beginning '/*';
     *                   or 0 if it is not in the same line of the '/*'.
     * @return If the current block comment ends at the current line, return
     * the location after the comment. Otherwise return -1.
     */
    private int skipBlockComment(String line, int curCharLoc) {
        int index = line.indexOf("*/", curCharLoc);

        if (index == -1) {
            return -1;
        } else {
            return index + 2;
        }
    }

    /**
     * Find the first comment starting from the curCharLoc.
     *
     * @param line       the current line to be scanned
     * @param curCharLoc [0, line.length())
     * @return If finding the head of a comment, return the location.
     * Otherwise return -1.
     */
    private int indexOfComment(String line, int curCharLoc) {
        // determine location of all strings in the line
        Map<Integer, Integer> strLoc = new HashMap<>();
        int strBegin = curCharLoc, strEnd;
        while (strBegin != -1) {
            strBegin = indexOfQuotation(line, strBegin);
            if (strBegin != -1) {
                strEnd = indexOfQuotation(line, strBegin + 1);
                // We assume that quotations appear in pairs in the same line.
                strLoc.put(strBegin, strEnd);
                strBegin++;
            }
        }

        int lineCommentIndex = line.indexOf("//", curCharLoc),
                blockCommentIndex = line.indexOf("/*", curCharLoc);
        while (isInString(strLoc, lineCommentIndex)) {
            lineCommentIndex = line.indexOf("//", lineCommentIndex + 2);
        }
        while (isInString(strLoc, blockCommentIndex)) {
            blockCommentIndex = line.indexOf("/*", blockCommentIndex + 2);
        }

        int index = Math.min(lineCommentIndex, blockCommentIndex);
        if (index == -1) {
            index = Math.max(lineCommentIndex, blockCommentIndex);
        }

        return index;
    }

    private boolean isInString(
            Map<Integer, Integer> strings, int index) {
        for (Map.Entry<Integer, Integer> entry : strings.entrySet()) {
            if (index > entry.getKey() && index < entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    private int indexOfQuotation(String line, int curCharLoc) {
        int strIndex = line.indexOf("\"", curCharLoc);

        while (strIndex != -1) {
            if (line.charAt(strIndex - 1) == '\\' || line.charAt(strIndex - 1) == '\'') {
                strIndex = line.indexOf("\"", strIndex + 1);
            } else {
                break;
            }
        }

        return strIndex;
    }

    private boolean isDelim(String line, int begin, int end) {
        for (int i = begin; i < end; i++) {
            if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                return true;
            }
        }
        return false;
    }

    @Parameter(defaultValue = "${project.build.sourceDirectory../../}",
            readonly = true)
    private File srcDir;

    private int files = 0;  // how many java files in the project
    private int codeLines = 0;  // code lines that exclude empty lines
    private int commentLines = 0;  // how many lines are comments
    private int lines = 0;  // total lines
}