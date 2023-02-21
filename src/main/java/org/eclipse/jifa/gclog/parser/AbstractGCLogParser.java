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

import org.eclipse.jifa.gclog.event.Safepoint;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.GCModelFactory;
import org.eclipse.jifa.gclog.parser.ParseRule.ParseRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.List;

import static org.eclipse.jifa.gclog.util.Constant.MS2S;

public abstract class AbstractGCLogParser implements GCLogParser {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractGCLogParser.class);

    private GCModel model;
    private GCLogParsingMetadata metadata;

    public GCLogParsingMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(GCLogParsingMetadata metadata) {
        this.metadata = metadata;
    }

    protected GCModel getModel() {
        return model;
    }

    // for the sake of performance, will try to use less regular expression
    public final GCModel parse(BufferedReader br) throws Exception {
        model = GCModelFactory.getModel(metadata.getCollector());
        model.setLogStyle(metadata.getStyle());
        String line;
        while ((line = br.readLine()) != null) {
            try {
                if (line.length() > 0) {
                    doParseLine(line);
                }
            } catch (Exception e) {
                LOGGER.debug("fail to parse \"{}\", {}", line, e.getMessage());
            }
        }
        try {
            endParsing();
        } catch (Exception e) {
            LOGGER.debug("fail to parse \"{}\", {}", line, e.getMessage());
        }

        return model;
    }

    protected abstract void doParseLine(String line);

    protected void endParsing() {
    }

    // return true if text can be parsed by any rule
    // order of rules matters
    protected boolean doParseUsingRules(AbstractGCLogParser parser, ParseRuleContext context, String text, List<ParseRule> rules) {
        for (ParseRule rule : rules) {
            if (rule.doParse(parser, context, text)) {
                return true;
            }
        }
        return false;
    }

    // Total time for which application threads were stopped: 0.0001215 seconds, Stopping threads took: 0.0000271 seconds
    protected void parseSafepointStop(double uptime, String s) {
        int begin = s.indexOf("stopped: ") + "stopped: ".length();
        int end = s.indexOf(" seconds, ");
        double duration = Double.parseDouble(s.substring(begin, end)) * MS2S;
        begin = s.lastIndexOf("took: ") + "took: ".length();
        end = s.lastIndexOf(" seconds");
        double timeToEnter = Double.parseDouble(s.substring(begin, end)) * MS2S;
        Safepoint safepoint = new Safepoint();
        // safepoint is printed at the end of it
        safepoint.setStartTime(uptime - duration);
        safepoint.setDuration(duration);
        safepoint.setTimeToEnter(timeToEnter);
        getModel().addSafepoint(safepoint);
    }
}
