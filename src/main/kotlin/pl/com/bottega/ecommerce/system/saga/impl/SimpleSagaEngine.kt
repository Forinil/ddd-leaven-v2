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
package pl.com.bottega.ecommerce.system.saga.impl

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.NoResultException

import org.springframework.stereotype.Component

import pl.com.bottega.ecommerce.system.infrastructure.events.SimpleEventPublisher
import pl.com.bottega.ecommerce.system.infrastructure.events.impl.handlers.EventHandler
import pl.com.bottega.ecommerce.system.saga.SagaEngine
import pl.com.bottega.ecommerce.system.saga.SagaInstance
import pl.com.bottega.ecommerce.system.saga.SagaManager
import pl.com.bottega.ecommerce.system.saga.annotations.LoadSaga
import pl.com.bottega.ecommerce.system.saga.annotations.SagaAction

/**
 * @author Rafał Jamróz
 */
@Component
class SimpleSagaEngine @Inject
constructor(private val sagaRegistry: SagaRegistry, private val eventPublisher: SimpleEventPublisher) : SagaEngine {

    @PostConstruct
    fun registerEventHandler() {
        eventPublisher.registerEventHandler(SagaEventHandler(this))
    }

    override fun handleSagasEvent(event: Any) {
        val loaders = sagaRegistry.getLoadersForEvent(event)
        for (loader in loaders) {
            val sagaInstance = loadSaga(loader, event)
            invokeSagaActionForEvent(sagaInstance, event)
            if (sagaInstance.isCompleted) {
                loader.removeSaga(sagaInstance)
            }
        }
    }

    private fun loadSaga(loader: SagaManager<SagaInstance<Any>, *>, event: Any): SagaInstance<Any> {
        val sagaType = determineSagaTypeByLoader(loader)
        var sagaData = loadSagaData(loader, event)
        if (sagaData == null) {
            sagaData = loader.createNewSagaData()
        }
        return sagaRegistry.createSagaInstance(sagaType, sagaData!!)
    }

    // TODO determine saga type more reliably
    private fun determineSagaTypeByLoader(loader: SagaManager<SagaInstance<Any>, *>): Class<out SagaInstance<Any>> {
        val type = (loader.javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0]
        return type as Class<out SagaInstance<Any>>
    }

    /**
     * TODO handle exception in more generic way
     */
    private fun loadSagaData(loader: SagaManager<*, *>, event: Any): Any? {
        val loaderMethod = findHandlerMethodForEvent(loader.javaClass, event)
        try {
            return loaderMethod.invoke(loader, event)
        } catch (e: InvocationTargetException) {
            // NRE is ok here, it means that saga hasn't been started yet
            return if (e.targetException is NoResultException) {
                null
            } else {
                throw RuntimeException(e)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    private fun invokeSagaActionForEvent(saga: SagaInstance<Any>, event: Any) {
        val eventHandler = findHandlerMethodForEvent(saga.javaClass, event)
        try {
            eventHandler.invoke(saga, event)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    private fun findHandlerMethodForEvent(type: Class<*>, event: Any): Method {
        for (method in type.methods) {
            if (method.getAnnotation(SagaAction::class.java) != null || method.getAnnotation(LoadSaga::class.java) != null) {
                if (method.parameterTypes.size == 1 && method.parameterTypes[0].isAssignableFrom(event.javaClass)) {
                    return method
                }
            }
        }
        throw RuntimeException("no method handling " + event.javaClass)
    }

    private class SagaEventHandler(private val sagaEngine: SagaEngine) : EventHandler {

        override fun canHandle(event: Any): Boolean {
            return true
        }

        override fun handle(event: Any) {
            sagaEngine.handleSagasEvent(event)
        }
    }
}
