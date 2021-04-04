import java.io.File

fun main(){

    val db = DBHelper("university")

    /*
    Создание бд по шаблону
    db.createDatabase()
    */

    //Создание бд из дампа
    db.createDatabase("university.sql")

    db.run{
        /*
        Второй способ заполнения таблицы (быстрее чем fill)
        readDataCsv("department.csv")
        readDataCsv("discipline.csv")
        readDataCsv("direction.csv")
        readDataCsv("curriculum.csv")
        */

        fillTableFromCsv("department.csv")
        fillTableFromCsv("discipline.csv")
        fillTableFromCsv("direction.csv")
        fillTableFromCsv("curriculum.csv")
        fillTableFromCsv("curriculum_subject.csv")
        fillTableFromCsv("group.csv")
        fillTableFromCsv("student.csv")
        fillTableFromCsv("academic_performance.csv")
    }

    val sql_query ="SELECT `student`.`group`, `lastname`,`firstname`, `middlename`,`money` FROM `student`,(\n" +
            "    SELECT `id_student`,min(result),\n" +
            "    (case\n" +
            "        when min(result)=5  then 3100\n" +
            "        when min(result)=4  then 2100\n" +
            "        when min(result)=3 then 0\n" +
            "        else 0 end\n" +
            "    )as money\n" +
            "    \n" +
            "    FROM (\n" +
            "        SELECT `id_student`, `score`, `try`, `reporting_form`,\n" +
            "        (CASE\n" +
            "         WHEN `reporting_form` = \"экзамен\" AND `score` > 86 THEN 5\n" +
            "         WHEN `reporting_form` = \"экзамен\" AND `score` > 70 THEN 4\n" +
            "         WHEN `reporting_form` = \"экзамен\" AND `score` > 55 THEN 3\n" +
            "         WHEN `reporting_form` = \"зачет\" AND `score` > 55 THEN 5\n" +
            "         ELSE 0 END\n" +
            "        ) AS `result`\n" +
            "        \n" +
            "        FROM `academic_performance`, `curriculum_subject`,(\n" +
            "            SELECT `id_student` as id, `current_semester` FROM `student` ,(\n" +
            "                SELECT `year_study`, `group`.`group_number`, \n" +
            "                2*(YEAR(NOW()) - `year_study`) - (CASE WHEN MONTH(NOW()) > 6 THEN 1 ELSE 0 END) AS `current_semester`\n" +
            "                FROM `group`, `curriculum` WHERE `group`.`curriculum_id` = `curriculum`.`curriculum_number`\n" +
            "            ) AS `group_cs`\n" +
            "            WHERE `student`.`group`= `group_cs`.`group_number`) AS `tb0` \n" +
            "        WHERE `academic_performance`.`id_student` = `tb0`.`id`\n" +
            "        AND `curriculum_subject`.`subject_id` = `academic_performance`.`subject_id`\n" +
            "        AND `tb0`.`current_semester` = `curriculum_subject`.`semester` + 1\n" +
            "    )AS `tb1` GROUP BY `id_student`\n" +
            ")AS `tb2` \n" +
            "WHERE `student`.`id_student` = `tb2`.`id_student` ORDER BY `student`.`group`, `lastname`,`firstname`, `middlename`"
    println("Выполнение запроса...")
    println("")
    db.getQuery(sql_query)
    db.disconnect()
}

/**
 * функция для изменения даты из конкретного файла Марины
 */
/*
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
 */