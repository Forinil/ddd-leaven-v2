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
/**
 *
 */
package pl.com.bottega.cqrs.command.impl

import javax.inject.Inject

import org.springframework.stereotype.Component

import pl.com.bottega.cqrs.command.handler.CommandHandler

/**
 * @author Slawek
 */
@Component
class RunEnvironment {

    @Inject
    private val handlersProvider: HandlersProvider? = null

    interface HandlersProvider {
        fun getHandler(command: Any): CommandHandler<Any, *>?
    }

    fun run(command: Any): Any? {
        val handler = handlersProvider!!.getHandler(command)

        //You can add Your own capabilities here: dependency injection, security, transaction management, logging, profiling, spying, storing commands, etc

        //You can add Your own capabilities here

        return handler!!.handle(command)
    }

}
