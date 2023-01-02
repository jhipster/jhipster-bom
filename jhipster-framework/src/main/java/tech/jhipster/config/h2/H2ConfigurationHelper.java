/*
 * Copyright 2016-2023 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.jhipster.config.h2;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * Utility class to configure H2 in development.
 * <p>
 * We don't want to include H2 when we are packaging for the "prod" profile and won't
 * actually need it, so we have to load / invoke things at runtime through reflection.
 */
public class H2ConfigurationHelper {

    /**
     * <p>createServer.</p>
     *
     * @return a {@link java.lang.Object} object.
     * @throws java.sql.SQLException if any.
     */
    public static Object createServer() throws SQLException {
        return createServer("9092");
    }

    /**
     * <p>createServer.</p>
     *
     * @param port a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.sql.SQLException if any.
     */
    public static Object createServer(String port) throws SQLException {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
            Method createServer = serverClass.getMethod("createTcpServer", String[].class);
            return createServer.invoke(null, new Object[]{new String[]{"-tcp", "-tcpAllowOthers", "-tcpPort", port}});

        } catch (ClassNotFoundException | LinkageError e) {
            throw new RuntimeException("Failed to load and initialize org.h2.tools.Server", e);

        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to get method org.h2.tools.Server.createTcpServer()", e);

        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to invoke org.h2.tools.Server.createTcpServer()", e);

        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof SQLException) {
                throw (SQLException) t;
            }
            throw new RuntimeException("Unchecked exception in org.h2.tools.Server.createTcpServer()", t);
        }
    }

    /**
     * Init the H2 console via H2's webserver when no servletContext {@link jakarta.servlet.ServletContext}
     * is available.
     */
    public static void initH2Console() {
        initH2Console("src/main/resources");
    }

    /**
     * Init the H2 console via H2's webserver when no servletContext {@link jakarta.servlet.ServletContext}
     * is available.
     *
     * @param propertiesLocation the location where to find .h2.server.properties
     */
    static void initH2Console(String propertiesLocation) {
        try {
            // We don't want to include H2 when we are packaging for the "prod" profile and won't
            // actually need it, so we have to load / invoke things at runtime through reflection.
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
            Method createWebServer = serverClass.getMethod("createWebServer", String[].class);
            Method start = serverClass.getMethod("start");

            Object server = createWebServer.invoke(null, new Object[]{new String[]{"-properties", propertiesLocation}});
            start.invoke(server);
        } catch (Exception  e) {
          throw new RuntimeException("Failed to start h2 webserver console", e);
        }
    }

    /**
     * <p>initH2Console.</p>
     *
     * @param servletContext a {@link jakarta.servlet.ServletContext} object.
     */
    public static void initH2Console(ServletContext servletContext) {
        try {
            // We don't want to include H2 when we are packaging for the "prod" profile and won't
            // actually need it, so we have to load / invoke things at runtime through reflection.
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet", true, loader);
            Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();

            ServletRegistration.Dynamic h2ConsoleServlet = servletContext.addServlet("H2Console", servlet);
            h2ConsoleServlet.addMapping("/h2-console/*");
            h2ConsoleServlet.setInitParameter("-properties", "src/main/resources/");
            h2ConsoleServlet.setLoadOnStartup(1);

        } catch (ClassNotFoundException | LinkageError | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to load and initialize org.h2.server.web.JakartaWebServlet", e);

        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Failed to instantiate org.h2.server.web.JakartaWebServlet", e);
        }
    }
}
