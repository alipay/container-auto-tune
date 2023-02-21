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
package org.eclipse.jifa.common.cache;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.eclipse.jifa.common.JifaException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class Handler implements MethodInterceptor {

    private final Cache cache;

    private final List<Method> cacheableMethods;

    public Handler(Class<?> target) {
        cache = new Cache();
        cacheableMethods = new ArrayList<>();

        try {
            Method[] methods = target.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Cacheable.class) != null) {
                    method.setAccessible(true);
                    int mod = method.getModifiers();
                    if (Modifier.isAbstract(mod) || Modifier.isFinal(mod) ||
                        !(Modifier.isPublic(mod) || Modifier.isProtected(mod))) {
                        throw new JifaException("Illegal method modifier: " + method);
                    }
                    cacheableMethods.add(method);
                }
            }
        } catch (Exception exception) {
            throw new JifaException(exception);
        }
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (cacheableMethods.contains(method)) {
            return cache.load(new Cache.CacheKey(method, args),
                              () -> {
                                  try {
                                      return proxy.invokeSuper(obj, args);
                                  } catch (Throwable throwable) {
                                      if (throwable instanceof RuntimeException) {
                                          throw (RuntimeException) throwable;
                                      }
                                      throw new JifaException(throwable);
                                  }
                              });
        }
        return proxy.invokeSuper(obj, args);
    }
}
