import java.io.File
import java.sql.*

/**
 * Класс помощник для базы данных
 * Будет создавать,связывать и пополнять бд
 * @param dbName - имя базы данных
 * @param address - адрес на котором работает бд "localhost" по умолчанию
 * @param port - порт на котором работает бд "3306" по умолчанию
 * @param user - логин для доступа к бд "root" по умолчанию
 * @param password - пароль для доступа к бд "root" по умолчанию
 */
class DBHelper(
    val dbName : String,
    val address : String = "localhost",
    val port : Int = 3306,
    val user : String = "root",
    val password : String = "root"
) {

    private var connection : Connection?=null
    private var statement: Statement? = null

    /**
     * Метод создания базы данных по шаблону
     */
    fun createDatabase(){
        connect()
        createTables()
    }

    /**
     * Метод создания базы данных с указанием файла, где есть sql-запросы
     * @param userdata - имя файла с запросами бд
     */
    fun createDatabase(userdata : String){
        connect()
        dropAllTables()
        createTablesFromDump(userdata)
    }

    /**
     * Метод подключения к бд  $dbName
     * Обращение к субд через statement (sql запросы)
     */
    private fun connect(){
        //Проверка на закрытое подключение.утверждение
        statement?.run{
            if (!isClosed) close()
        }
        var rep = 0
        //Попытка подключения к бд
        do {
            try {
                connection =
                        DriverManager.getConnection("jdbc:mysql://$address:$port/$dbName?serverTimezone=UTC",
                                user,
                                password
                        )
                statement =
                        DriverManager.getConnection("jdbc:mysql://$address:$port/$dbName?serverTimezone=UTC",
                                user,
                                password
                        ).createStatement()
            } catch (e: SQLSyntaxErrorException) {
                println("Ошибка подключения к бд ${dbName} : \n${e.toString()}")
                println("Попытка создания бд ${dbName}")
                val tstmt =
                        DriverManager.getConnection("jdbc:mysql://$address:$port/?serverTimezone=UTC", user, password)
                                .createStatement()
                tstmt.execute("CREATE SCHEMA `$dbName`")
                tstmt.closeOnCompletion()
                rep++
            }
        } while (statement == null && rep < 2)
    }

    /**
     * Метод для удаления таблиц,если они есть в бд
     */
    private fun dropAllTables(){
        println("Удаление всех таблиц в базе данных...")
        statement?.execute("DROP TABLE if exists `academic_performance`")
        statement?.execute("DROP TABLE if exists `student`")
        statement?.execute("DROP TABLE if exists `group`")
        statement?.execute("DROP TABLE if exists `curriculum_subject`")
        statement?.execute("DROP TABLE if exists `curriculum`")
        statement?.execute("DROP TABLE if exists `discipline`")
        statement?.execute("DROP TABLE if exists `department`")
        statement?.execute("DROP TABLE if exists `direction`")
        println("Все таблицы удалены.")
    }

    /**
     * Создание таблиц через готовые sql запросы в файле
     * @param userdata - название файла
     */
    private fun createTablesFromDump(userdata : String){
        println("Создание структуры базы данных из дампа...")
        try {
            var query = ""
            File(userdata).forEachLine {
                if(!it.startsWith("--") && it.isNotEmpty()){
                    query += it
                    if (it.endsWith(';')) {
                        statement?.addBatch(query)
                        query = ""
                    }
                }
            }
            statement?.executeBatch()
            println("Структура базы данных успешно создана.")
        }
        catch (e: SQLException){
            println(e.message)
        }
        catch (e: Exception){
            println(e.message)
        }
    }


    /**
     * Метод шаблонного создания таблицы
     * и ее связывания
     */
    private fun createTables() {
        //Разбитие запроса на батчи и объединение их в транзакцию (множество запросов)
        try{
            println("Создание структуры базы данных по шаблону...")
            statement?.run{
                addBatch("START TRANSACTION;")

                addBatch("DROP TABLE IF EXISTS `academic_performance`")
                addBatch("DROP TABLE IF EXISTS `student`")
                addBatch("DROP TABLE IF EXISTS  `group`")
                addBatch("DROP TABLE IF EXISTS `curriculum_subject`")

                addBatch("DROP TABLE IF EXISTS `curriculum`")
                addBatch("DROP TABLE IF EXISTS `direction`")

                addBatch("DROP TABLE IF EXISTS `discipline`")
                addBatch("DROP TABLE IF EXISTS `department`")

                //Конец транзакции
                addBatch("COMMIT")
                executeBatch()

                addBatch("START TRANSACTION;")


                addBatch("CREATE TABLE IF NOT EXISTS`student` (\n" +
                        "  `id_student` int NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                        "  `lastname` varchar(50) NOT NULL,\n" +
                        "  `firstname` varchar(50) NOT NULL,\n" +
                        "  `middlename` varchar(50) DEFAULT NULL,\n" +
                        "  `gender` set('М','Ж') NOT NULL,\n" +
                        "  `birthday` date NOT NULL,\n" +
                        "  `group` varchar(10) NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `group` (\n" +
                        "  `group_number` varchar(10) NOT NULL PRIMARY KEY,\n" +
                        "  `curriculum_id` int NOT NULL,\n" +
                        "  `qualification_number` set('бакалавр','магистр','специалитет') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `discipline` (\n" +
                        "  `subject_code` varchar(30) NOT NULL PRIMARY KEY,\n" +
                        "  `subject_name` varchar(50)  NOT NULL,\n" +
                        "  `department_code` int NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `direction` (\n" +
                        "  `direction_code` int NOT NULL PRIMARY KEY,\n" +
                        "  `direction_name` varchar(50) NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `department` (\n" +
                        "  `department_code` int NOT NULL PRIMARY KEY,\n" +
                        "  `department_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `curriculum_subject` (\n" +
                        "  `subject_id` int NOT NULL PRIMARY KEY,\n" +
                        "  `curriculum_number` int NOT NULL,\n" +
                        "  `subject_code` varchar(30) NOT NULL,\n" +
                        "  `semester` int UNSIGNED NOT NULL,\n" +
                        "  `number_of_hours` int UNSIGNED NOT NULL,\n" +
                        "  `reporting_form` set('зачет','экзамен','отсутствует') NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `curriculum` (\n" +
                        "  `curriculum_number` int NOT NULL PRIMARY KEY,\n" +
                        "  `year_study` year NOT NULL,\n" +
                        "  `direction_code` int NOT NULL\n" +
                        ")")


                addBatch("CREATE TABLE IF NOT EXISTS `academic_performance` (\n" +
                        "  `id_student` int NOT NULL  ,\n" +
                        "  `subject_id` int NOT NULL ,\n" +
                        "  `score` tinyint NOT NULL,\n" +
                        "  `try` set('1','2','3') NOT NULL\n" +
                        ")")

                //Конец транзакции
                addBatch("COMMIT")
                executeBatch()


                addBatch("START TRANSACTION;")
                addBatch("ALTER TABLE `academic_performance`\n" +
                        "  ADD PRIMARY KEY (`id_student`,`subject_id`),\n" +
                        "  ADD KEY  `academic_performance_ibfk_1` (`subject_id`)")

                //Связывание Таблиц
                addBatch("ALTER TABLE `academic_performance`\n" +
                        "  ADD CONSTRAINT `academic_performance_ibfk_1` FOREIGN KEY (`id_student`) REFERENCES `student` (`id_student`) ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                        "  ADD CONSTRAINT `academic_performance_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `curriculum_subject` (`subject_id`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                addBatch("ALTER TABLE `curriculum`\n" +
                        "  ADD CONSTRAINT `curriculum_direction` FOREIGN KEY (`direction_code`) REFERENCES `direction` (`direction_code`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                addBatch("ALTER TABLE `curriculum_subject`\n" +
                        "  ADD CONSTRAINT `curriculum_subject_ibfk_1` FOREIGN KEY (`curriculum_number`) REFERENCES `curriculum` (`curriculum_number`) ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                        "  ADD CONSTRAINT `curriculum_subject_ibfk_2` FOREIGN KEY (`subject_code`) REFERENCES `discipline` (`subject_code`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                addBatch("ALTER TABLE `discipline`\n" +
                        "  ADD CONSTRAINT `discipline_department` FOREIGN KEY (`department_code`) REFERENCES `department` (`department_code`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                addBatch("ALTER TABLE `group`\n" +
                        "  ADD CONSTRAINT `group_curriculum` FOREIGN KEY (`curriculum_id`) REFERENCES `curriculum` (`curriculum_number`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                addBatch("ALTER TABLE `student`\n" +
                        "  ADD CONSTRAINT `student_group` FOREIGN KEY (`group`) REFERENCES `group` (`group_number`) ON DELETE RESTRICT ON UPDATE CASCADE;")

                //Конец транзакции
                addBatch("COMMIT")
                executeBatch()
            }
            println("Структура базы данных успешно создана по шаблону!")
        }catch (e:SQLException){
            println(e.message)
        }

    }

    /**
     * Метод закрытия подключения.утверждения
     */
    fun disconnect() {
        statement?.close()
    }

    /**
     * Метод прочтения данных для таблицы из формата csv и ее запись
     * @param userdata - название таблицы,в которую нужно записать данные и названия файла,который нужно записать
     * Нужно менять путь к файлу $userdata !!
     */
    fun readDataCsv(userdata : String){
        val validData = userdata.split(".csv")
        println("Внесение всех данных в таблицу ${validData[0]}...")
        try{
            statement?.execute("LOAD DATA INFILE 'D:/programs/OpenServer/userdata/php_upload/${validData[0]}.csv'\n" +
                    "INTO TABLE `${validData[0]}`\n" +
                    "FIELDS TERMINATED BY ';' ENCLOSED BY '\"' ESCAPED BY '\\\\'\n" +
                    "LINES STARTING BY '' TERMINATED BY '\\n'")
            println("Данные успешно внесены в таблицу ${validData[0]}!")
        }catch (e:SQLException){
            println(e.message)
        }

    }

    /**
     * Метод заполнения таблицы через insert into, в каждое поле кортежа записываются данные
     * в формате "$name", где name - значение типа string, СУБД преобразует varchar в нужные форматы полей
     * @param userdata - название таблицы
     */
    fun fillTableFromCsv(userdata : String){
        val validData = userdata.split(".csv")
        println("Заполнение данными таблицы ${validData[0]}...")
        try{
            val bufferedData = File("data/"+validData[0]+".csv").bufferedReader()
            val requestTemplate = "INSERT INTO `${validData[0]}`" +
                    "("+ getDataFromTable(validData[0]) +") VALUES "
            while(bufferedData.ready()){
                var request = "$requestTemplate("
                val data = bufferedData.readLine().split(';')
                data.forEachIndexed { i, name ->
                    request += "\"$name\""
                    if(i<data.size -1) request+=','
                }
                request+=')'
                statement?.addBatch(request)
            }
            statement?.executeBatch()
            statement?.clearBatch()
            println("Таблица ${validData[0]} успешно заполнена!")
        } catch(e: Exception){
            println(e.toString())
        }

    }

    /**
     * Метод выдает данные из таблиц в субд (реализованно название атрибутов таблицы, закоментирован код для доставания типов атрибутов таблицы)
     * @param userdata название таблицы
     */
    private fun getDataFromTable(userdata : String): String{
        val query = "SHOW COLUMNS FROM `${userdata}`"
        val resultSet = statement?.executeQuery(query)
        var resultData = ""
        if (resultSet != null){
            //get column name
            while (resultSet.next()){
                resultData+= "`"+resultSet.getString(1)+"` ,"
            }
            resultData= resultData.substring(0,(resultData.length)-2)
            /*
            if(columnName){
                //get column name
                while (resultSet.next()){
                    resultData+= "`"+resultSet.getString(1)+"` ,"
                }
                resultData= resultData.substring(0,(resultData.length)-4)
            }
            else {
                //get type of column ( Not Worked now )
                while (resultSet.next()){
                    val currentField = resultSet.getString(1)
                    val validField = currentField.split("(")
                    println(validField)
                    println(map.get(validField[0]))

                    resultData+= map.get(validField[0])
                }
            }
             */
        }
        return resultData
    }
    fun getQuery(sql : String){
        val rs =statement?.executeQuery(sql)
        while(rs?.next()==true){
            println(rs.getString(1)+";"+rs.getString(2)+";"+rs.getString(3)+";"+rs.getString(4)+";"+rs.getString(5))

        }
    }
    //val map = mapOf("int" to Int,"varchar" to String,"set" to String)//date=?
    /*
    public fun test(){
        statement?.execute("INSERT INTO `curriculum_subject`(`subject_id`, `curriculum_number`, `subject_code`, `semestr`, `number_of_hours`, `reporting_form`) VALUES (1,1,1,1,72,\"test\"),(2,1,2,2,144,\"exam\")")
    }
     */
}