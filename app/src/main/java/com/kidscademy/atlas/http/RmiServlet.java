package com.kidscademy.atlas.http;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import js.json.Json;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.Types;

public class RmiServlet implements Servlet {
    private static final Log log = LogFactory.getLog(RmiServlet.class);

    private static final Map<String, Method> methodsCache = new HashMap<>();

    private final Json json;

    public RmiServlet() {
        log.trace("RmiServlet()");
        this.json = Classes.loadService(Json.class);
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        int pathSeparatorIndex = request.getRequestURI().lastIndexOf('/');
        int extensionSeparatorIndex = request.getRequestURI().lastIndexOf('.');
        String className = className(request.getRequestURI().substring(0, pathSeparatorIndex));
        className = className.substring(5);
        String methodName = request.getRequestURI().substring(pathSeparatorIndex + 1, extensionSeparatorIndex);

        Class<?> managedClass = null;

        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        try {
            managedClass = Class.forName(className, true, threadLoader);
        } catch (ClassNotFoundException unused) {
            log.error("Missing managed class |%s|.", className);
            return;
        }

        Method method = methodsCache.get(request.getRequestURI());
        if (method == null) {
            try {
                method = Classes.findMethod(managedClass, methodName);
            } catch (NoSuchMethodException e) {
                log.error("Missing managed method |%s#%s|.", className, methodName);
            }
            methodsCache.put(request.getRequestURI(), method);
        }

        Object managedInstance = managedClass.newInstance();
        Class<?> returnType = method.getReturnType();
        Object value = method.invoke(managedInstance, getArguments(request.getReader(), method.getParameterTypes()));

        response.setHeader("Connection", "close");

        if (Types.isVoid(returnType)) {
            response.setStatus(ResponseStatus.NO_CONTENT);
            response.setContentLength(0L);
            response.flush();
            return;
        }

        String body = json.stringify(value);
        response.setStatus(ResponseStatus.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        response.setContentLength(body.length());
        response.getStream().write(body.getBytes("UTF-8"));
        response.flush();
    }

    private Object[] getArguments(Reader reader, Class<?>[] parameterTypes) throws IOException {
        if (parameterTypes.length == 0) {
            return new Object[0];
        }
        return json.parse(reader, parameterTypes);
    }

    private static String className(String match) {
        StringBuilder className = new StringBuilder();
        char separator = '.';
        char c = match.charAt(1);
        for (int i = 1; ; ) {
            if (c == '/') {
                c = separator;
            }
            className.append(c);

            if (++i == match.length()) {
                break;
            }
            c = match.charAt(i);
            if (Character.isUpperCase(c)) {
                separator = '$';
            }
        }
        return className.toString();
    }
}
