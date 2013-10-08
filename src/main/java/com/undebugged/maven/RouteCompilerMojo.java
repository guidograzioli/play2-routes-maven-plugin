/*
 * Copyright 2013 Guido Grazioli <guido.grazioli@gmail.com>
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

package com.undebugged.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import scala.collection.JavaConversions;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Compiles scala.html files to scala source files and compiles routes.
 *
 * @requiresDependencyResolution compile
 */
@Mojo(name="compile-routes",defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class RouteCompilerMojo extends AbstractMojo {

    @Parameter(defaultValue="${project}",required=true,readonly=true)
    private MavenProject project;

    /**
     * Location of the compiled routes.
     */
    @Parameter(property="project.build.directory",defaultValue="${project.build.directory}/generated-sources/play-templates",required=true)
    private File generatedSourcesDirectory;

    /**
     * Location of the play conf directory.
     */
    @Parameter(defaultValue="${project.basedir}/conf",required=true)
    private File confDirectory;

    public void execute() throws MojoExecutionException {
//        try {
            compileRoutes(absolutePath(confDirectory), absolutePath(generatedSourcesDirectory), project);
//        } catch (TemplateCompilationError e) {
//            String msg = String.format("Error in route %s:%s %s", e.source().getPath(), e.line(), e.message());
//            throw new MojoExecutionException(msg);
//        }
    }

    public static void compileRoutes(File confDirectory, File outputDir, MavenProject project) throws MojoExecutionException {

        project.addCompileSourceRoot(outputDir.getAbsolutePath());

        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (!created) throw new MojoExecutionException("Failed to create output directory");
        }

        RoutesCompiler routesCompiler = new RoutesCompiler();
        routesCompiler.compile(confDirectory, outputDir, new scala.collection.mutable.ArrayBuffer<String>());
    }

    /** Convert Files with relative paths to be relative from the project basedir. **/
    private File absolutePath(File file) {
        if (file.isAbsolute()) {
            return file;
        }
        return new File(project.getBasedir(), file.getPath());
    }
}
