/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.jifa.gclog.parser;

import org.eclipse.jifa.common.JifaException;
import org.eclipse.jifa.common.listener.ProgressListener;
import org.eclipse.jifa.gclog.model.GCModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class GCLogAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCLogAnalyzer.class);
    private File file;
    private ProgressListener listener;

    // max length in hotspot
    private final int MAX_SINGLE_LINE_LENGTH = 2048;

    public GCLogAnalyzer(File file, ProgressListener listener) {
        this.file = file;
        this.listener = listener;
    }

    public GCModel parse() throws Exception {
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            listener.beginTask("Paring " + file.getName(), 1000);
            listener.sendUserMessage(ProgressListener.Level.INFO, "Reading gc log file.", null);
            GCLogParserFactory logParserFactory = new GCLogParserFactory();
            br.mark(GCLogParserFactory.MAX_ATTEMPT_LINE * MAX_SINGLE_LINE_LENGTH);
            GCLogParser parser = logParserFactory.getParser(br);
            br.reset();
            // first read original info from log file
            GCModel model = parser.parse(br);
            if (model.isEmpty()) {
                throw new JifaException("Fail to parse gclog. Is this really a gc log?\"");
            }
            // then calculate derived info for query from original info
            listener.worked(500);
            listener.sendUserMessage(ProgressListener.Level.INFO, "Calculating information from original data.", null);
            model.calculateDerivedInfo(listener);
            return model;
        } catch (Exception e) {
            LOGGER.info("fail to parse gclog {}: {}", file.getName(), e.getMessage());
            throw e;
        }
    }
}
