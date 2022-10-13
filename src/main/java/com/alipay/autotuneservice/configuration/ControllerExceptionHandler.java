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
package com.alipay.autotuneservice.configuration;

import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version ControllerExceptionHandler.java, v 0.1 2022年03月08日 18:01 dutianze
 */
@Slf4j
@RestControllerAdvice("com.alipay.autotuneservice.controller")
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Object handleMissingParams(MethodArgumentNotValidException e) {
        log.error("handleWebMvcException", e);
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String defaultMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" or "));
        return ServiceBaseResult.failureResult(HttpStatus.BAD_REQUEST.value(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("handleMissingServletRequestParameterException", e);
        return ServiceBaseResult.failureResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = { NullPointerException.class })
    public Object handleNullPointerException(NullPointerException e) {
        log.error("handleNullPointerException", e);
        return ServiceBaseResult.failureResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            e.toString());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = { RuntimeException.class })
    public Object handleRuntimeException(RuntimeException e) {
        log.error("handleRuntimeException", e);
        ResultCode resultCode = ResultCode.INTERNAL_SERVER_ERROR;
        if (e instanceof BizException) {
            resultCode = ((BizException) e).getResultCode();
        }
        return ServiceBaseResult.failureResult(resultCode.getCode(), e.getMessage());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, new CustomCollectionEditor(List.class));
    }
}