/*
 * Copyright (C) 2020 Brian Norman
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

package com.bnorm.react

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class ReactFunctionGradlePlugin : KotlinCompilerPluginSupportPlugin {
  override fun apply(target: Project): Unit = with(target) {
    extensions.create("kotlinReactFunction", ReactFunctionGradleExtension::class.java)
  }

  override fun getCompilerPluginId(): String = "com.bnorm.kotlin-react-function"

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = "com.bnorm.react",
    artifactId = "kotlin-react-function-plugin",
    version = "0.1.0-SNAPSHOT"
  )

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    val project = kotlinCompilation.target.project
//    val extension = project.extensions.getByType(ReactFunctionGradleExtension::class.java)
//    return project.provider {
//      extension.functions.map {
//        SubpluginOption(key = "function", value = it)
//      }
//    }
    return project.provider { emptyList() }
  }
}
