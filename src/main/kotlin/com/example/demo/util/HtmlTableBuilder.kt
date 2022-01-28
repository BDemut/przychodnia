package com.example.demo.util

class HtmlTableBuilder(var edit: Boolean = true, var delete: Boolean = true, var choose: Boolean = false) {
    private var htmlTable = StringBuilder()

    fun addRow(vararg columnValues: String): HtmlTableBuilder {
        htmlTable.append("<tr>")
        columnValues.forEach { htmlTable.append("<td>$it</td>") }
        if (edit)
            htmlTable.append("<td class=\"table-buttons\"><button type=\"button\" onclick=\"edit('${columnValues[0]}')\">EDYTUJ</button></td>")
        if (delete)
            htmlTable.append("<td class=\"table-buttons\"><button type=\"button\" onclick=\"clickRemove('${columnValues[0]}')\">USUŃ</button></td>")
        if (choose)
            htmlTable.append("<td class=\"table-buttons\"><button type=\"button\" onclick=\"choose('${columnValues[0]}')\">WYBIERZ</button></td>")
        htmlTable.append("</tr>")
        return this
    }

    fun build(): String {
        return htmlTable.toString()
    }
}