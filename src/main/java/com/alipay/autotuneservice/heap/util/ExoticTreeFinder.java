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
package com.alipay.autotuneservice.heap.util;

import org.eclipse.mat.query.IResultTree;

import java.util.List;
import java.util.function.Function;

import static org.springframework.util.Assert.notNull;

// find elements in this exotic tree
// MAT's APIs really astonished me, I'm climbing the s*** mountains of unbelievable awful smell;
// 2020-12-11
public class ExoticTreeFinder {
    private final IResultTree                               tree;
    private       BinFunction<IResultTree, Object, Integer> predicate;
    private       Function<Object, List<?>>                 getChildrenCallback;

    public ExoticTreeFinder(IResultTree tree) {
        notNull(tree);
        this.tree = tree;
    }

    public ExoticTreeFinder setGetChildrenCallback(Function<Object, List<?>> getChildrenCallback) {
        this.getChildrenCallback = getChildrenCallback;
        return this;
    }

    public ExoticTreeFinder setPredicate(BinFunction<IResultTree, Object, Integer> predicate) {
        this.predicate = predicate;
        return this;
    }

    public List<?> findChildrenOf(int parentNodeId) {
        Object targetParentNode = null;
        try {
            targetParentNode = findTargetParentNodeImpl(tree.getElements(), parentNodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (targetParentNode != null) {
            return getChildrenCallback.apply(targetParentNode);
        }
        return null;
    }

    public Object findTargetParentNode(int parentNodeId) {
        try {
            return findTargetParentNodeImpl(tree.getElements(), parentNodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object findTargetParentNodeImpl(List<?> nodes, int parentNodeId) throws Exception {
        if (nodes == null) {
            return null;
        }

        for (Object node : nodes) {
            Integer nodeId = predicate.apply(tree, node);
            if (nodeId != null && nodeId == parentNodeId) {
                return node;
            }
        }

        for (Object node : nodes) {
            List<?> children = getChildrenCallback.apply(node);
            if (children != null) {
                Object targetParentNode = findTargetParentNodeImpl(children, parentNodeId);
                if (targetParentNode != null) {
                    return targetParentNode;
                }
            }
        }

        return null;
    }

    public interface BinFunction<A, B, R> {
        R apply(A a, B b) throws Exception;
    }
}
