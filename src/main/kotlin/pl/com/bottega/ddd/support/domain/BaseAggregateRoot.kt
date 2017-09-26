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
package pl.com.bottega.ddd.support.domain

import javax.inject.Inject
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.MappedSuperclass
import javax.persistence.Transient
import javax.persistence.Version

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException

/**
 * @author Slawek
 */
@Component
@Scope("prototype")//created in domain factories, not in spring container, therefore we don't want eager creation
@MappedSuperclass
abstract class BaseAggregateRoot {

    @EmbeddedId
    @AttributeOverrides(AttributeOverride(name = "idValue", column = Column(name = "aggregateId", nullable = false)))
    lateinit var aggregateId: AggregateId
        protected set

    @Version
    private var version: Long = 0

    @Enumerated(EnumType.ORDINAL)
    private var aggregateStatus = AggregateStatus.ACTIVE

    @Transient
    @Inject
    protected var eventPublisher: DomainEventPublisher? = null

    val isRemoved: Boolean
        get() = aggregateStatus == AggregateStatus.ARCHIVE

    enum class AggregateStatus {
        ACTIVE, ARCHIVE
    }

    fun markAsRemoved() {
        aggregateStatus = AggregateStatus.ARCHIVE
    }

    protected fun domainError(message: String) {
        throw DomainOperationException(aggregateId, message)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (other is BaseAggregateRoot) {
            val another = other as BaseAggregateRoot?
            return another?.aggregateId == aggregateId
        }

        return false
    }

    override fun hashCode(): Int {
        return aggregateId.hashCode()
    }
}
