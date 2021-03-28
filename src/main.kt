import java.io.File

fun main(){

    //changeData() для Студентов менял дату

    val db = DBHelper("university")
    db.createDatabase()

    db.run{
        readDataCsv("department.csv")
        readDataCsv("discipline.csv")
        readDataCsv("direction.csv")
        readDataCsv("curriculum.csv")
        fillTableFromCsv("curriculum_subject.csv")
        fillTableFromCsv("group.csv")
        fillTableFromCsv("student.csv")
        fillTableFromCsv("academic_performance.csv")

    }
    db.disconnect()
}

/**
 * функция для изменения даты из конкретного файла Марины
 */
fun changeData(){
    val bufferedData = File("d:/programs/openserver/userdata/php_upload/student.csv").bufferedReader()
    //var newdata = mutableListOf<String>()
    val newfile =  File("student2").bufferedWriter()
    while (bufferedData.ready()){
        var data= bufferedData.readLine().split(";").toMutableList()
        val result = data[5].split(".")
        data[5]=result[2]+"-"+result[1]+"-"+result[0]
        val newdata = (data).toString().substring(1,(data).toString().length-1).split(',')
        newfile.write(newdata[0]+";")
        for (i in 1..newdata.size-2) {
            newfile.write((newdata[i]+";").substring(1,(newdata[i]+";").length))
        }
        newfile.write(newdata[newdata.size-1].substring(1,newdata.size))
        newfile.newLine()
    }
    newfile.close()
}