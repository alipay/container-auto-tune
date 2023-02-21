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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * convert reader to Measure
 *
 * @author dutianze
 * @version Converter.java, v 0.1 2022年01月06日 14:42 dutianze
 * @see Measure
 */
@Slf4j
public class Converter {

    // yyyy-MM-dd HH:mm:ss
    private static final Pattern DATE_TIME_REGEX = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$");

    // jvm info
    private static final Pattern JVM_INFO_REGEX1 = Pattern.compile("Full thread dump (?<jvm>\\w+) (25.312-b07 mixed mode):");
    private static final Pattern JVM_INFO_REGEX  = Pattern.compile("Full thread dump (?<jvm>.*):");

    // "T1" #11 prio=5 os_prio=31 tid=0x00007fc1d0897000 nid=0x9b23 waiting on condition [0x0000000306e06000]
    private static final Pattern THREAD_HEADER_REGEX = Pattern.compile(
            "\"(?<name>.*)\"\\s+(#(?<number>\\d+)\\s+)?(?<daemon>daemon\\s+)?prio=(?<priority>\\d+)(\\s+os_prio=(?<osPriority>\\d+))"
                    + "?\\s+tid=0x(?<threadId>\\w+)\\s+nid=0x(?<nativeId>\\w+)\\s+(?<state0>.*)"
                    + "\\s+\\[0x(?<conditionPointer>\\w+)]");

    private static final Pattern THREAD_HEADER_REGEX1 = Pattern.compile(
            "\"(?<name>.*)\".*(\\s+os_prio=(?<osPriority>\\d+))"
                    + "?\\s+tid=0x(?<threadId>\\w+)\\s+nid=0x(?<nativeId>\\w+)\\s+(?<state0>.*)");

    //    java.lang.Thread.State: WAITING (parking)
    private static final Pattern THEAD_STATE_REGEX = Pattern.compile("\\s+java\\.lang\\.Thread\\.State:\\s+(?<state>\\w+).*");

    // 	- parking to wait for  <0x000000076eb384f8> (a java.util.concurrent.locks.ReentrantLock$FairSync)
    private static final Pattern WAIT_FOR_REGEX = Pattern.compile(
            "\\t-\\s+.*wait for\\s+<0x(?<waitFor>.*)>\\s+\\(a\\s+(?<waitForDetails>.*)\\)");

    //  - locked <0x000000076ec1f1a8> (a io.netty.channel.nio.SelectedSelectionKeySet)
    private static final Pattern LOCKED_REGEX = Pattern.compile(
            "\\s+- locked\\s+<0x(?<locked>.*)>\\s+\\(a\\s+(?<lockDetails>.*)\\)"
    );

    // - waiting to lock <0x000000070f5defb8> (a java.lang.Object)
    private static final Pattern WAIT_TO_LOCK_REGX = Pattern.compile(
            "\\s+- waiting to lock\\s+<0x(?<waitToLocked>.*)>\\s+\\(a\\s+(?<lockDetails>.*)\\)"
    );

    // -waiting to lock <0x000000070f5defc8> (a java.lang.Object)
    private static final Pattern TRACES_REGEX = Pattern.compile(
            "\\t-.*"
    );

    private static final Pattern LOCKED_REGEX_test = Pattern.compile(
            "\\t-.locked"
    );

    //    Locked ownable synchronizers:
    private static final Pattern LOCKED_OBJECTS_REGEX = Pattern.compile("\\s+Locked ownable synchronizers:");

    // 	- None
    private static final Pattern NO_LOCKED_OBJECTS_REGEX = Pattern.compile("\\t-\\s+(?<none>None)");

    // 	- <0x000000076eb38450> (a java.util.concurrent.locks.ReentrantLock$FairSync)
    private static final Pattern HAVE_LOCKED_OBJECTS_REGEX = Pattern.compile(
            "\\t+-\\s+<0x(?<ownLock>.*)>\\s+\\(a (?<onwLockDetails>.*)\\)");

    //	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
    private static final Pattern STACK_TRACE_REGEX = Pattern.compile("\\t+at\\s+(?<methodFqn>.*)\\s?\\((?<fileLine>.*)\\)");

    /**
     * read all thread info from reader
     *
     * @param br PushBackBufferedReader
     * @return Measure
     * @throws IOException ex
     */
    public Measure parseJStack(PushBackBufferedReader br) throws IOException {
        Measure result = new Measure();
        // first line is date
        String line = br.readLine();
        Matcher dateTimeMatcher = DATE_TIME_REGEX.matcher(line);
        if (dateTimeMatcher.matches()) {
            result.setDateOfLog(dateTimeMatcher.group());
        }
        result.setJvmInfo(parseJvmInfo(br));
        // second line is jvm info
        ThreadInfo thread = parseThread(br);
        int i = 0;
        while (thread != null) {
            result.addThread(thread);
            thread = this.parseThread(br);
        }
        return result;
    }

    /**
     * 解析jvmInfo信息
     *
     * @param br
     */
    private String parseJvmInfo(PushBackBufferedReader br) {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                Matcher jvmInfoMatcher = JVM_INFO_REGEX.matcher(line);
                if (jvmInfoMatcher.matches()) {
                    return jvmInfoMatcher.group("jvm");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * read thread info from reader
     *
     * @param br PushBackBufferedReader
     * @return ThreadInfo
     * @throws IOException ex
     */
    private ThreadInfo parseThread(PushBackBufferedReader br) throws IOException {
        ThreadInfo thread = new ThreadInfo();
        String line;
        // first line is thread header info
        Matcher threadHead = null;
        while ((line = br.readLine()) != null) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            threadHead = THREAD_HEADER_REGEX1.matcher(line);
            if (threadHead.matches()) {
                break;
            }
        }
        if (line == null || !threadHead.matches()) {
            return null;
        }

        //判断是否是 daemon
        Matcher threadDaemon = THREAD_HEADER_REGEX.matcher(line);
        if (threadDaemon.matches()) {
            this.parseDaemonThreadHead(thread, threadDaemon);
        } else {
            this.parseThreadHead(thread, threadHead);
        }

        // left is trace tack or others or nothing
        while ((line = br.readLine()) != null) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            Matcher stateMatcher = THEAD_STATE_REGEX.matcher(line);
            if (stateMatcher.matches()) {
                this.parseThreadStatus(thread, stateMatcher);
            }
            // next line is next thread, push back
            if (THREAD_HEADER_REGEX1.matcher(line).matches()) {
                br.pushBack();
                return thread;
            }
            Matcher stackMatcher = STACK_TRACE_REGEX.matcher(line);
            if (stackMatcher.matches()) {
                thread.getStackTraces().add(line.replace("\t", ""));
                this.parseStacktraceItem(thread, stackMatcher);
                continue;
            }
            Matcher waitForMatcher = WAIT_FOR_REGEX.matcher(line);
            if (waitForMatcher.matches()) {
                thread.getStackTraces().add(line.replace("\t", ""));
                this.parseWaitFor(thread, waitForMatcher);
                continue;
            }

            Matcher lockMatcher = LOCKED_REGEX.matcher(line);
            if (lockMatcher.matches()) {
                thread.getStackTraces().add(line.replace("\t", ""));
                this.parseLock(thread, lockMatcher);
                continue;
            }

            Matcher waitToLockMatcher = WAIT_TO_LOCK_REGX.matcher(line);
            if (waitToLockMatcher.matches()) {
                thread.getStackTraces().add(line.replace("\t", ""));
                this.parseWaitToLock(thread, waitToLockMatcher);
                continue;
            }

            thread.getStackTraces().add(line.replace("\t", ""));

            if (NO_LOCKED_OBJECTS_REGEX.matcher(line).matches()) {
                break;
            }
        }
        // parse lock info of thread
        line = br.readLine();
        if (line != null) {
            // next line is next thread, push back
            if (THREAD_HEADER_REGEX1.matcher(line).matches()) {
                br.pushBack();
                return thread;
            }
            Matcher noLockMatcher = NO_LOCKED_OBJECTS_REGEX.matcher(line);
            if (noLockMatcher.matches()) {
                this.parseOwnedLockNone(thread, noLockMatcher);
            }
            Matcher haveLockMatcher = HAVE_LOCKED_OBJECTS_REGEX.matcher(line);
            if (haveLockMatcher.matches()) {
                this.parseOwnedLock(thread, haveLockMatcher);
            }
        }
        return thread;
    }

    /**
     * @param info
     * @param m
     * @see #
     */
    private void parseWaitToLock(ThreadInfo info, Matcher m) {
        info.setWaitToLock(m.group("waitToLocked"));
    }

    /**
     * @param info
     * @param m
     * @see # LOCKED_REGEX
     */
    private void parseLock(ThreadInfo info, Matcher m) {
        info.setLock(m.group("locked"));
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #THREAD_HEADER_REGEX
     */
    private void parseThreadHead(ThreadInfo info, Matcher m) {
        info.setName(m.group("name"));
        info.setOsPriority(Integer.parseInt(StringUtils.defaultIfEmpty(m.group("osPriority"), "-1")));
        info.setThreadId(Long.parseLong(m.group("threadId"), 16));
        info.setNativeId(Long.parseLong(m.group("nativeId"), 16));
        info.setState0(m.group("state0"));
    }

    private void parseDaemonThreadHead(ThreadInfo info, Matcher m) {
        info.setName(m.group("name"));
        info.setNumber(Integer.parseInt(StringUtils.defaultIfEmpty(m.group("number"), "-1")));
        info.setOsPriority(Integer.parseInt(StringUtils.defaultIfEmpty(m.group("osPriority"), "-1")));
        info.setDaemon(StringUtils.isNoneBlank(m.group("daemon")));
        info.setPriority(Integer.parseInt(m.group("priority")));
        info.setThreadId(Long.parseLong(m.group("threadId"), 16));
        info.setNativeId(Long.parseLong(m.group("nativeId"), 16));
        info.setState0(m.group("state0"));
        info.setConditionPointer(Long.parseLong(m.group("conditionPointer"), 16));
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #THEAD_STATE_REGEX
     */
    private void parseThreadStatus(ThreadInfo info, Matcher m) {
        info.setState(m.group("state"));
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #WAIT_FOR_REGEX
     */
    private void parseWaitFor(ThreadInfo info, Matcher m) {
        info.setWaitFor(Long.parseLong(m.group("waitFor"), 16));
        info.setWaitForDetails(m.group("waitForDetails"));
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #STACK_TRACE_REGEX
     */
    private void parseStacktraceItem(ThreadInfo info, Matcher m) {
        StacktraceItem item = new StacktraceItem();
        item.setMethodFqn(m.group("methodFqn"));
        item.setFileLine(m.group("fileLine"));
        info.addStacktraceItem(item);
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #NO_LOCKED_OBJECTS_REGEX
     */
    private void parseOwnedLockNone(ThreadInfo info, Matcher m) {
        info.setOwnLock(null);
        info.setOnwLockDetails(m.group("none"));
    }

    /**
     * @param info ThreadInfo
     * @param m    Matcher
     * @see #HAVE_LOCKED_OBJECTS_REGEX
     */
    private void parseOwnedLock(ThreadInfo info, Matcher m) {
        info.setOwnLock(Long.parseLong(m.group("ownLock"), 16));
        info.setOnwLockDetails(m.group("onwLockDetails"));
    }

    /**
     * Class to make java's buffered reader possibility to push back just read line
     */
    static public class PushBackBufferedReader extends BufferedReader {

        private volatile String  prevLine   = null;
        private volatile boolean pushedBack = false;

        public PushBackBufferedReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            if (pushedBack) {
                pushedBack = false;
                return prevLine;
            }

            prevLine = super.readLine();
            return prevLine;
        }

        public void pushBack() {
            pushedBack = true;
        }
    }

}