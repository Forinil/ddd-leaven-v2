/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.com.bottega.cqrs.annotations

import kotlin.annotation.Retention

import pl.com.bottega.cqrs.command.handler.CommandHandler

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Command(
        /**
         * Suggestion for a Server that this command may be run in asynchronous way.
         * <br></br>
         * If true than [CommandHandler] must return void - otherwise Serwer will throw an exception
         * @return
         */
        val asynchronous: Boolean = false,

        /**
         * Suggestion for a Server that this command should checked if the same command is sent again.<br></br>
         * If true than command class must implement equals and hashCode
         * @return
         */
        val unique: Boolean = false,

        /**
         * If unique is true than this property may specify maximum timeout in miliseconds before same command can be executed
         * @return
         */
        val uniqueStorageTimeout: Long = 0L)
