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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.jifa.gclog.event.TimedEvent;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.util.I18nStringView;

import java.util.Comparator;
import java.util.List;

import static org.eclipse.jifa.gclog.diagnoser.AbnormalSeverity.NONE;
import static org.eclipse.jifa.gclog.diagnoser.AbnormalType.LAST_TYPE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalPoint {
    private AbnormalType type;
    private TimedEvent site;
    private AbnormalSeverity severity;

    public static final AbnormalPoint LEAST_SERIOUS = new AbnormalPoint(LAST_TYPE, null, NONE);

    public static final Comparator<AbnormalPoint> compareByImportance = (ab1, ab2) -> {
        if (ab1.severity != ab2.severity) {
            return ab1.severity.ordinal() - ab2.severity.ordinal();
        } else if (ab1.type != ab2.type) {
            return ab1.type.getOrdinal() - ab2.type.getOrdinal();
        }
        return 0;
    };

    public List<I18nStringView> generateDefaultSuggestions(GCModel model) {
        return new DefaultSuggestionGenerator(model, this).generate();
    }
}
