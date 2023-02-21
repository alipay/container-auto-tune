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

import org.eclipse.jifa.gclog.util.FourConsumer;
import org.eclipse.jifa.gclog.util.GCLogUtil;
import org.eclipse.jifa.gclog.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ParseRule {
    /**
     * parse rule itself should be stateless, so that it can be shared by threads
     *
     * @param parser  to save any parse result and state
     * @param text    text to parse
     * @param context provide addition information. or enable acceptRule to pass some result to doParse if return true
     * @return true if this rule can parse the text
     */
    boolean doParse(AbstractGCLogParser parser, ParseRuleContext context, String text);

    class ParseRuleContext {
        public static final String UPTIME = "uptime";
        public static final String GCID = "gcid";
        public static final String EVENT = "event";

        private Map<String, Object> map;

        public void put(String key, Object value) {
            map.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) map.getOrDefault(key, null);
        }

        public ParseRuleContext() {
            this.map = new HashMap<>();
        }
    }

    // here we encapsulate some common types of rules

    // e.g as to "Heap: 1g" then prefix is "Heap" or "Heap:", value is "1g"
    class PrefixAndValueParseRule implements ParseRule {
        private String prefix;
        private FourConsumer<AbstractGCLogParser, ParseRuleContext, String, String> consumer;

        public PrefixAndValueParseRule(String prefix, FourConsumer<AbstractGCLogParser, ParseRuleContext, String, String> consumer) {
            this.prefix = prefix;
            this.consumer = consumer;
        }

        @Override
        public boolean doParse(AbstractGCLogParser parser, ParseRuleContext context, String text) {
            if (!text.startsWith(prefix)) {
                return false;
            }
            consumer.accept(parser, context, prefix, GCLogUtil.parseValueOfPrefix(text, prefix));
            return true;
        }
    }

    class RegexParseRules implements ParseRule {
        private Pattern pattern;
        TriConsumer<AbstractGCLogParser, ParseRuleContext, Matcher> consumer;

        public RegexParseRules(String pattern, TriConsumer<AbstractGCLogParser, ParseRuleContext, Matcher> consumer) {
            this.pattern = Pattern.compile(pattern);
            this.consumer = consumer;
        }

        @Override
        public boolean doParse(AbstractGCLogParser parser, ParseRuleContext context, String text) {
            Matcher matcher = pattern.matcher(text);
            if (!matcher.matches()) {
                return false;
            }
            consumer.accept(parser, context, matcher);
            return false;
        }
    }

    class FixedContentParseRule implements ParseRule {
        private String content;
        private BiConsumer<AbstractGCLogParser, ParseRuleContext> consumer;

        public FixedContentParseRule(String content, BiConsumer<AbstractGCLogParser, ParseRuleContext> consumer) {
            this.content = content;
            this.consumer = consumer;
        }

        @Override
        public boolean doParse(AbstractGCLogParser parser, ParseRuleContext context, String text) {
            if (!text.equals(content)) {
                return false;
            }
            consumer.accept(parser, context);
            return true;
        }
    }
}
