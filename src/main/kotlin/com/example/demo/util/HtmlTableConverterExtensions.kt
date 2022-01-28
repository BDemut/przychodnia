import com.example.demo.database.entities.*
import com.example.demo.util.HtmlTableBuilder


fun List<String>.toHtmlTable() : String {
    val tableBuilder = HtmlTableBuilder(edit = false, delete = false)
    forEach {
        tableBuilder.addRow(it)
    }
    return tableBuilder.build()
}
//
@JvmName("toHtmlTableSpecialization")
fun List<Specialization?>.toHtmlTable() : String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.name ?: "", it?.adder?.toTableName() ?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableDegree")
fun List<Degree?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.short ?: "", it?.full ?: "", it?.adder?.toTableName()?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableEquipment?")
fun List<Equipment?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.name ?: "", it?.adder?.toTableName() ?: "")
    }
    return tableBuilder.build()
}
//
@JvmName("toHtmlTableRoomWithEquipment?")
fun List<Room?>.toHtmlTable(buttons: Boolean = true): String {
    val tableBuilder = HtmlTableBuilder(buttons, buttons)
    forEach {
        tableBuilder.addRow(it?.number.toString(), it?.equipment?.map{ it.name }.toString(), it?.adder?.toTableName()?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableClinicWithRooms?")
fun List<Clinic?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.name ?: "", it?.rooms?.map { it.number }.toString(), it?.adder?.toTableName()?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableDoctorsClinicsSpecializations?")
fun List<Doctor?>.toHtmlTable(choosing: Boolean = false): String {
    val tableBuilder =
        if (choosing)
            HtmlTableBuilder(edit = false, delete = false, choose = true)
        else
            HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.pesel?: "", it?.fname ?: "", it?.lname ?: "",it?.degree?.short ?: "",it?.specializations?.map { it.name }.toString(), it?.clinics?.map{ it.name }.toString(), it?.adder?.toTableName() ?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableAdmin?")
fun List<Admin?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.pesel?: "", it?.fname ?: "", it?.lname ?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTablePatient?")
fun List<Patient?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder(edit = true, delete = true, choose = true)
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.pesel?: "", it?.fname ?: "", it?.lname ?: "", it?.adder?.toTableName() ?: "")
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableVisitsDoctorsPatientsServices?")
fun List<Visit?>.toHtmlTable(doctor: Boolean = false): String {
    if (doctor) {
        val tableBuilder = HtmlTableBuilder()
        forEach {
            tableBuilder.addRow(it?.id.toString(), it?.date.toString(), it?.time.toString(), it?.patient?.pesel ?: "", it?.patient?.fname ?: "", it?.patient?.lname ?: "", it?.services?.map{ it.name }.toString(), it?.note ?: "")
        }
        return tableBuilder.build()
    } else {
        val tableBuilder = HtmlTableBuilder(edit = false)
        forEach {
            tableBuilder.addRow(
                it?.id.toString(),
                it?.date.toString(),
                it?.time.toString(),
                it?.patient?.pesel ?: "",
                it?.patient?.fname ?: "",
                it?.patient?.lname ?: "",
                it?.doctor?.pesel ?: "",
                it?.doctor?.fname ?: "",
                it?.doctor?.lname ?: "",
                it?.services?.map { it.name }.toString(),
                it?.takenPlace.toString()
            )
        }
        return tableBuilder.build()
    }
}

@JvmName("toHtmlTableService?")
fun List<Service?>.toHtmlTable(): String {
    val tableBuilder = HtmlTableBuilder()
    forEach {
        tableBuilder.addRow(it?.id.toString(), it?.name ?: "", it?.price.toString(), it?.duration.toString())
    }
    return tableBuilder.build()
}

@JvmName("toHtmlTableResrevation?")
fun List<Reservation?>.toHtmlTable(doctorData: Boolean = false): String {
    if (doctorData) {
        val tableBuilder = HtmlTableBuilder(edit = false, delete = false)
        forEach {
            tableBuilder.addRow(
                it?.id.toString(),
                it?.dateFrom.toString(),
                it?.hourFrom.toString(),
                it?.until.toString(),
                it?.room?.number.toString(),
                it?.doctor?.fname ?: "",
                it?.doctor?.lname ?: ""
            )
        }
        return tableBuilder.build()
    } else {
        val tableBuilder = HtmlTableBuilder()
        forEach {
            tableBuilder.addRow(it?.id.toString(), it?.dateFrom.toString(), it?.hourFrom.toString(), it?.until.toString(), it?.room?.number.toString())
        }
        return tableBuilder.build()
    }
}