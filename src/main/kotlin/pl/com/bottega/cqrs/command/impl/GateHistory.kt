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

import java.util.Date
import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentHashMap

import pl.com.bottega.cqrs.annotations.Command

/**
 * Manages command execution history based on [Command] annotation attributes<br></br>
 * Commands that are annotated as unique=true are stored in this history.<br></br>
 * History checks if the same command (equals) is called again.<br></br>
 * <br></br>
 * Each command class has it's own entries in history - history length can be parameterized via constructor parameter.
 *
 * @author Slawek
 */
internal class GateHistory @JvmOverloads constructor(private val maxHistoryCapacity: Int = DEFAULT_MAX_HISTORY_CAPACITY) {

    /**
     * History model. Each command class has map of executions (command instance
     * and time)
     */
    private val history = ConcurrentHashMap<Class<*>, CommandExecutionsMap>()

    private open inner// TODO Sprawdzic czy nie musi byc concurrent (history jest, na tym
    // poziomie nie musi byc totalnej synchronizacji, to tylko rodzaj
    // cache)
    class CommandExecutionsMap : LinkedHashMap<Any, Date>() {
        override fun removeEldestEntry(eldest: Map.Entry<Any, Date>?): Boolean {
            return this.size > maxHistoryCapacity
        }
    }

    /**
     *
     * @param command
     * @return true if command is not a repetition, false if command is
     * repetition and should not be executed now
     */
    fun register(command: Any): Boolean {
        if (!isUnique(command))
            return true

        val lastRun = getFromHistory(command)

        // update history
        val now = Date()
        addToHistory(command, now)

        // first run, so go
        if (lastRun == null)
            return true

        val uniqueStorageTimeout = getUniqueStorageTimeout(command)
        // no timeout so by default it is duplicated
        if (uniqueStorageTimeout == 0L)
            return false

        val milisFromLastRun = now.time - lastRun.time
        return milisFromLastRun > uniqueStorageTimeout
    }

    private fun isUnique(command: Any): Boolean {
        if (!command.javaClass.isAnnotationPresent(Command::class.java))
            return false

        val commandAnnotation = command.javaClass.getAnnotation(
                Command::class.java)

        return commandAnnotation.unique
    }

    private fun getUniqueStorageTimeout(command: Any): Long {
        val commandAnnotation = command.javaClass.getAnnotation(
                Command::class.java)
        return commandAnnotation.uniqueStorageTimeout
    }

    private fun getFromHistory(command: Any): Date? {
        val executions = history[command.javaClass] ?: return null
        return executions[command]
    }

    private fun addToHistory(command: Any, executeDate: Date) {
        var executions: CommandExecutionsMap? = history[command.javaClass]
        if (executions == null) {
            executions = CommandExecutionsMap()
            history.put(command.javaClass, executions)
        }
        executions.put(command, executeDate)
    }

    companion object {

        private val DEFAULT_MAX_HISTORY_CAPACITY = 3
    }
}
