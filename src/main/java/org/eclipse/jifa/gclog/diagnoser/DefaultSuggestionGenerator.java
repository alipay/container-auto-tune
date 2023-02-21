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
package org.eclipse.jifa.gclog.diagnoser;

import org.eclipse.jifa.common.JifaException;
import org.eclipse.jifa.common.util.ErrorUtil;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.util.I18nStringView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_CPU_TIME;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_FAST_PROMOTION;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_LIVE_OBJECTS;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_MEMORY_LEAK;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_METASPACE;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_REFERENCE_GC;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.CHECK_SYSTEM_GC;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.DISABLE_SYSTEM_GC;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.ENLARGE_METASPACE;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.INCREASE_CONC_GC_THREADS;
import static org.eclipse.jifa.gclog.diagnoser.SuggestionType.INCREASE_Z_ALLOCATION_SPIKE_TOLERANCE;

// This class generates common suggestions when we can not find the exact cause of problem.
public class DefaultSuggestionGenerator extends SuggestionGenerator {
    private AbnormalPoint ab;

    public DefaultSuggestionGenerator(GCModel model, AbnormalPoint ab) {
        super(model);
        this.ab = ab;
    }

    private static Map<AbnormalType, Method> rules = new HashMap<>();

    static {
        initializeRules();
    }

    private static void initializeRules() {
        Method[] methods = DefaultSuggestionGenerator.class.getDeclaredMethods();
        for (Method method : methods) {
            GeneratorRule annotation = method.getAnnotation(GeneratorRule.class);
            if (annotation != null) {
                method.setAccessible(true);
                int mod = method.getModifiers();
                if (Modifier.isAbstract(mod) || Modifier.isFinal(mod)) {
                    throw new JifaException("Illegal method modifier: " + method);
                }
                rules.put(AbnormalType.getType(annotation.value()), method);
            }
        }
    }

    @GeneratorRule("metaspaceFullGC")
    private void metaspaceFullGC() {
        addSuggestion(CHECK_METASPACE);
        addSuggestion(ENLARGE_METASPACE);
        fullGCSuggestionCommon();
    }

    @GeneratorRule("systemGC")
    private void systemGC() {
        addSuggestion(CHECK_SYSTEM_GC);
        addSuggestion(DISABLE_SYSTEM_GC);
        suggestOldSystemGC();
        fullGCSuggestionCommon();
    }

    @GeneratorRule("outOfMemory")
    private void outOfMemory() {
        addSuggestion(CHECK_MEMORY_LEAK);
        suggestEnlargeHeap(false);
    }

    @GeneratorRule("allocationStall")
    private void allocationStall() {
        addSuggestion(CHECK_MEMORY_LEAK);
        suggestEnlargeHeap(true);
        addSuggestion(INCREASE_CONC_GC_THREADS);
        addSuggestion(INCREASE_Z_ALLOCATION_SPIKE_TOLERANCE);
    }

    @GeneratorRule("heapMemoryFullGC")
    private void heapMemoryFullGC() {
        addSuggestion(CHECK_MEMORY_LEAK);
        addSuggestion(CHECK_FAST_PROMOTION);
        suggestStartOldGCEarly();
        fullGCSuggestionCommon();
    }

    @GeneratorRule("longYoungGCPause")
    private void longYoungGCPause() {
        addSuggestion(CHECK_LIVE_OBJECTS);
        addSuggestion(CHECK_CPU_TIME);
        addSuggestion(CHECK_REFERENCE_GC);
        suggestCheckEvacuationFailure();
        suggestShrinkYoungGen();
    }

    public List<I18nStringView> generate() {
        if (ab.getType() == null) {
            return result;
        }
        Method rule = rules.getOrDefault(ab.getType(), null);
        if (rule != null) {
            try {
                rule.invoke(this);
            } catch (Exception e) {
                ErrorUtil.shouldNotReachHere();
            }
        }
        return result;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface GeneratorRule {
        String value();
    }
}
