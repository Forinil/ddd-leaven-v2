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

import javax.inject.Inject

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

import pl.com.bottega.ecommerce.system.saga.SagaInstance
import pl.com.bottega.ecommerce.system.saga.SagaManager
import pl.com.bottega.ecommerce.system.saga.annotations.LoadSaga
import pl.com.bottega.ecommerce.system.saga.annotations.SagaAction

import com.google.common.collect.HashMultimap

/**
 * @author Rafał Jamróz
 */
@Component
class SpringSagaRegistry : SagaRegistry, ApplicationListener<ContextRefreshedEvent> {
    override fun <T> createSagaInstance(sagaType: Class<out SagaInstance<*>>, sagaData: T): SagaInstance<T> {
        return beanFactory!!.getBean(sagaType) as SagaInstance<T>
    }

    private val loadersInterestedIn = HashMultimap.create<Class<*>, String>()

    @Inject
    private val beanFactory: ConfigurableListableBeanFactory? = null

    override fun getLoadersForEvent(event: Any): Collection<SagaManager<SagaInstance<Any>, *>> {
        val loadersBeansNames = loadersInterestedIn.get(event.javaClass)
        return loadersBeansNames
                .map<String?, SagaManager<*, *>?> { beanFactory!!.getBean(it, SagaManager::class.java) }
                .toList() as Collection<SagaManager<SagaInstance<Any>, *>>
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        loadersInterestedIn.clear()
        registerSagaLoaderBeans()
    }

    private fun registerSagaLoaderBeans() {
        val loadersNames = beanFactory!!.getBeanNamesForType(SagaManager::class.java)
        for (loaderBeanName in loadersNames) {
            val loaderBeanDefinition = beanFactory.getBeanDefinition(loaderBeanName)
            try {
                registerSagaLoader(Class.forName(loaderBeanDefinition.beanClassName), loaderBeanName)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
    }

    private fun registerSagaLoader(loaderClass: Class<*>, beanName: String) {
        for (method in loaderClass.methods) {
            if (method.getAnnotation(SagaAction::class.java) != null || method.getAnnotation(LoadSaga::class.java) != null) {
                val params = method.parameterTypes
                if (params.size == 1) {
                    loadersInterestedIn.put(params[0], beanName)
                } else {
                    throw RuntimeException("incorred event hadndler: " + method)
                }
            }
        }
    }

}
