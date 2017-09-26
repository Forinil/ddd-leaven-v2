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
package pl.com.bottega.ecommerce.system.infrastructure.events.impl

import java.lang.reflect.Method

import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

import pl.com.bottega.ddd.annotations.event.EventListener
import pl.com.bottega.ecommerce.system.infrastructure.events.SimpleEventPublisher
import pl.com.bottega.ecommerce.system.infrastructure.events.impl.handlers.AsynchronousEventHandler
import pl.com.bottega.ecommerce.system.infrastructure.events.impl.handlers.EventHandler
import pl.com.bottega.ecommerce.system.infrastructure.events.impl.handlers.SpringEventHandler
import pl.com.bottega.ecommerce.system.saga.SagaInstance

/**
 * Registers spring beans methods as event handlers in spring event publisher
 * (if needed).
 */
@Component
class EventListenerBeanPostProcessor : BeanPostProcessor, BeanFactoryAware {

    private var beanFactory: BeanFactory? = null
    private var eventPublisher: SimpleEventPublisher? = null

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean !is SagaInstance<*>) {
            for (method in bean.javaClass.methods) {
                val listenerAnnotation = method.getAnnotation(EventListener::class.java) ?: continue

                val eventType = method.parameterTypes[0]

                if (listenerAnnotation.asynchronous) {
                    //TODO just a temporary fake impl
                    val handler = AsynchronousEventHandler(eventType, beanName, method, beanFactory!!)
                    //TODO add to some queue
                    eventPublisher!!.registerEventHandler(handler)
                } else {
                    val handler = SpringEventHandler(eventType, beanName, method, beanFactory!!)
                    eventPublisher!!.registerEventHandler(handler)
                }
            }
        }
        return bean
    }

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        // do nothing
        return bean
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
        eventPublisher = beanFactory.getBean(SimpleEventPublisher::class.java)
    }
}
