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
package pl.com.bottega.cqrs.command.handler.spring

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.HashMap

import javax.inject.Inject

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

import pl.com.bottega.cqrs.command.handler.CommandHandler
import pl.com.bottega.cqrs.command.impl.RunEnvironment.HandlersProvider

@Component
class SpringHandlersProvider : HandlersProvider, ApplicationListener<ContextRefreshedEvent> {

    @Inject
    private val beanFactory: ConfigurableListableBeanFactory? = null

    private val handlers = HashMap<Class<*>, String>()

    @SuppressWarnings("unchecked")
    override fun getHandler(command: Any): CommandHandler<Any, *>? {
        val beanName = handlers[command.javaClass] ?: throw RuntimeException("command handler not found. Command class is " + command.javaClass)
        return beanFactory!!.getBean(beanName, CommandHandler::class.java) as CommandHandler<Any, *>
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        handlers.clear()
        val commandHandlersNames = beanFactory!!.getBeanNamesForType(CommandHandler::class.java)
        for (beanName in commandHandlersNames) {
            val commandHandler = beanFactory.getBeanDefinition(beanName)
            try {
                val handlerClass = Class.forName(commandHandler.beanClassName)
                handlers.put(getHandledCommandType(handlerClass), beanName)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }

        }
    }

    private fun getHandledCommandType(clazz: Class<*>): Class<*> {
        val genericInterfaces = clazz.genericInterfaces
        val type = findByRawType(genericInterfaces, CommandHandler::class.java)
        return type.actualTypeArguments[0] as Class<*>
    }

    private fun findByRawType(genericInterfaces: Array<Type>, expectedRawType: Class<*>): ParameterizedType {
        for (type in genericInterfaces) {
            if (type is ParameterizedType) {
                if (expectedRawType == type.rawType) {
                    return type
                }
            }
        }
        throw RuntimeException()
    }
}
