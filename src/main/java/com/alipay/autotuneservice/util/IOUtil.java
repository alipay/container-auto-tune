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
package com.alipay.autotuneservice.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.exception.ServerException;
import org.eclipse.jifa.gclog.model.GCModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author huoyuqi
 * @version IOUtil.java, v 0.1 2022年11月10日 3:28 下午 huoyuqi
 */
public class IOUtil {

    public static InputStream ConvertObjectToInputStream(GCModel object) {
        String args = JSON.toJSONString(object, SerializerFeature.WriteClassName);
        InputStream stream = new ByteArrayInputStream(args.getBytes());
        return stream;
    }

    public static GCModel ConvertInputStreamToObject(InputStream inputStream) {
        if (null == inputStream) {
            throw new ServerException(ResultCode.NOT_FOUND_IN_DB);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(b)) != -1) {
                result.write(b, 0, bytesRead);
            }
            String a = result.toString();
            GCModel gcModel = (GCModel) JSON.parse(a, Feature.SupportAutoType);
            return gcModel;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}