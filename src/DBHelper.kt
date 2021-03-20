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
    //private var fileReader : BufferedReader?=null
    //private var line : String?=null
    private var statement: Statement? = null

    /**
     * Метод создания базы данных
     */
    fun createDatabase(){
        connect()
        createTables()
        //disconnect()
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
     * Метод закрытия подключения.утверждения
     */
    private fun disconnect() {
        statement?.close()
    }

    /**
     * Метод непосредственно создания таблицы
     * и ее связывания
     */
    private fun createTables() {
        //Разбитие запроса на батчи и объединение их в транзакцию (множество запросов)
        statement?.run{
            addBatch("START TRANSACTION;")

            addBatch("DROP TABLE IF EXISTS `academic_perfomance`")
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


            addBatch("CREATE TABLE `student` (\n" +
                    "  `id_student` int NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "  `lastname` varchar(50) NOT NULL,\n" +
                    "  `firstname` varchar(50) NOT NULL,\n" +
                    "  `middlename` varchar(50) DEFAULT NULL,\n" +
                    "  `group` varchar(10) NOT NULL,\n" +
                    "  `sex` set('male','female') NOT NULL,\n" +
                    "  `birthday` date NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `group` (\n" +
                    "  `group_number` varchar(10) NOT NULL PRIMARY KEY,\n" +
                    "  `curriculum_id` int NOT NULL,\n" +
                    "  `qualification_number` set('bachelor','master','graduate') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `discipline` (\n" +
                    "  `subject_code` int NOT NULL PRIMARY KEY,\n" +
                    "  `subject_name` varchar(50)  NOT NULL,\n" +
                    "  `department_code` int NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `direction` (\n" +
                    "  `direction_code` int NOT NULL PRIMARY KEY,\n" +
                    "  `direction_name` varchar(50) NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `department` (\n" +
                    "  `department_code` int NOT NULL PRIMARY KEY,\n" +
                    "  `department_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `curriculum_subject` (\n" +
                    "  `subject_id` int NOT NULL PRIMARY KEY,\n" +
                    "  `curriculum_number` int NOT NULL,\n" +
                    "  `subject_code` int NOT NULL,\n" +
                    "  `semestr` int UNSIGNED NOT NULL,\n" +
                    "  `number_of_hours` int UNSIGNED NOT NULL,\n" +
                    "  `reporting_form` set('test','differential_test','exam') NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `curriculum` (\n" +
                    "  `curriculum_number` int NOT NULL PRIMARY KEY,\n" +
                    "  `year_study` year NOT NULL,\n" +
                    "  `direction_code` int NOT NULL\n" +
                    ")")


            addBatch("CREATE TABLE `academic_perfomance` (\n" +
                    "  `id_student` int NOT NULL  ,\n" +
                    "  `subject_id` int NOT NULL ,\n" +
                    "  `score` tinyint NOT NULL,\n" +
                    "  `try` set('1','2','3') NOT NULL\n" +
                    ")")

            //Конец транзакции
            addBatch("COMMIT")
            executeBatch()


            addBatch("START TRANSACTION;")
            addBatch("ALTER TABLE `academic_perfomance`\n" +
                    "  ADD PRIMARY KEY (`id_student`,`subject_id`),\n" +
                    "  ADD KEY  `academic_perfomance_ibfk_1` (`subject_id`)")

            //Связывание Таблиц
            addBatch("ALTER TABLE `academic_perfomance`\n" +
                    "  ADD CONSTRAINT `academic_perfomance_ibfk_1` FOREIGN KEY (`id_student`) REFERENCES `student` (`id_student`) ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                    "  ADD CONSTRAINT `academic_perfomance_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `curriculum_subject` (`subject_id`) ON DELETE RESTRICT ON UPDATE CASCADE;")

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

    }
    public fun ReadDataCsv(userdata : String){
        val validData = userdata.split(".csv")
        statement?.execute("LOAD DATA INFILE 'd:/programs/openserver/userdata/php_upload/${validData[0]}.csv'\n" +
                "INTO TABLE `${validData[0]}`\n" +
                "FIELDS TERMINATED BY ';' ENCLOSED BY '\"' ESCAPED BY '\\\\'\n" +
                "LINES STARTING BY '' TERMINATED BY '\\n'")
    }
    public fun test(){
        statement?.execute("INSERT INTO `curriculum_subject`\n" +
                "(`subject_id`, `curriculum_number`,\n" +
                " `subject_code`, `semestr`,\n" +
                " `number_of_hours`, `reporting_form`) \n" +
                "VALUES (1,1,1,1,1,\"1\")")
    }


}
