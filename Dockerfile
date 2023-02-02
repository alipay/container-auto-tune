#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM azul/zulu-openjdk-alpine:8u322-8.60.0.21

ARG JAR_FILE=target/container-auto-tune-0.0.1-SNAPSHOT.jar
ARG STORAGE_PATH=storage/

WORKDIR /opt/app

RUN apk update \
    && apk upgrade \
    && apk add gcompat \
    && apk add curl

ENV LD_PRELOAD=/lib/libgcompat.so.0

COPY ${JAR_FILE} autotune.jar
#COPY ${STORAGE_PATH} /opt/app/
ENTRYPOINT ["java","-jar","-verbose:gc","-XX:+UseConcMarkSweepGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps","-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/opt/app/heapdump.hprof","-XX:+PrintGC", "-Xloggc:/opt/app/gc.log", "-Xms1024m", "-Xmx1024m", "-XX:MaxMetaspaceSize=512m", "-XX:MetaspaceSize=512m", "autotune.jar"]