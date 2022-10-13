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
package com.alipay.autotuneservice.thread;

import com.alipay.autotuneservice.thread.Converter.PushBackBufferedReader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dutianze
 * @version Analyzer.java, v 0.1 2022年01月06日 14:36 dutianze
 */
@Slf4j
public class Analyzer {

    public List<Measure> measures = new ArrayList<>();

    public void readJStacks(String directory) throws IOException {
        Files.walk(Paths.get(directory)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                File stack = filePath.toFile();
                Date stackDate = new Date(stack.lastModified());

                PushBackBufferedReader br;
                try {
                    br = new PushBackBufferedReader(new FileReader(stack));
                    Converter converter = new Converter();
                    Measure measure = converter.parseJStack(br);
                    measure.date = stackDate;
                    measure.name = stack.getName();
                    measures.add(measure);
                } catch (Exception e) {
                    log.error("readJStacks error", e);
                }
            }
        });
        sortMeasuresByDate();
        log.info("Loaded " + measures.size() + " jStack files");
    }

    private void sortMeasuresByDate() {
        measures.sort((o1, o2) -> {
            long d1 = o1.date.getTime();
            long d2 = o2.date.getTime();
            return Long.compare(d1, d2);
        });
    }
}