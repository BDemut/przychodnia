package com.example.demo.database

import java.lang.StringBuilder

class QueryBuilder(entityName: String, vararg join: String) {
    private val stringBuilder = StringBuilder("Select t from $entityName t ")

    init {
        join.forEach {
            stringBuilder.append("left join t.$it j$it ")
        }
        stringBuilder.append("where ")
    }

    fun addLike(columnName: String, parameter: String, join: String? = null): QueryBuilder {
        if (join != null)
            stringBuilder.append("j$join.$columnName like '%$parameter%' OR ")
        else
            stringBuilder.append("t.$columnName like '%$parameter%' OR ")
        return this
    }

    fun addEquals(columnName: String, parameter: String, join: String? = null): QueryBuilder {
        if (join != null)
            stringBuilder.append("j$join.$columnName = '$parameter' OR ")
        else
            stringBuilder.append("t.$columnName = '$parameter' OR ")
        return this
    }

    fun build() : String {
        stringBuilder.delete(stringBuilder.length-4, stringBuilder.length)
        return stringBuilder.toString()
    }
}