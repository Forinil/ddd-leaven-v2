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
package pl.com.bottega.cqrs.query

import java.io.Serializable
import java.util.Collections

class PaginatedResult<T> : Serializable {
    val items: List<T>
    val pageSize: Int
    val pageNumber: Int
    val pagesCount: Int
    val totalItemsCount: Int

    constructor(pageNumber: Int, pageSize: Int) {
        this.pageNumber = pageNumber
        this.pageSize = pageSize
        items = emptyList()
        pagesCount = 0
        totalItemsCount = 0
    }

    constructor(items: List<T>, pageNumber: Int, pageSize: Int, totalItemsCount: Int) {
        this.items = items
        this.pageNumber = pageNumber
        this.pageSize = pageSize
        this.pagesCount = countPages(pageSize, totalItemsCount)
        this.totalItemsCount = totalItemsCount
    }

    private fun countPages(size: Int, itemsCount: Int): Int {
        return Math.ceil(itemsCount.toDouble() / size).toInt()
    }
}