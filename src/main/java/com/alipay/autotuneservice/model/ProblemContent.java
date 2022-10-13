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
package com.alipay.autotuneservice.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author huoyuqi
 * @version ProblemContent.java, v 0.1 2022年06月24日 7:14 下午 huoyuqi
 */
@Data
public class ProblemContent {

    private String              problem_text;
    private Map<String, String> problem_pod;
    private String              problem_type;

    public static ProblemContent newInstance() {
        return new ProblemContent();
    }

    public ProblemContent bdProblem_text(String problem_text) {
        this.problem_text = problem_text;
        return this;
    }

    public ProblemContent bdProblem_type(String problem_type) {
        this.problem_type = problem_type;
        return this;
    }

    public ProblemContent bdProblem_pod(Map<String, String> problem_pod) {
        this.problem_pod = problem_pod;
        return this;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(problem_text) && StringUtils.isNotEmpty(problem_type);
    }

}