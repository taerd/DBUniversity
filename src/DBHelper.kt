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
     * Метод создания базы данных
     */
    fun createDatabase(){
        connect()
        createTables()
        disconnect()
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
     */
    private fun createTables() {
        //Разбитие запроса на батчи и объединение их в транзакцию (множество запросов)
        statement?.run{
            addBatch("START TRANSACTION;")

            addBatch("DROP TABLE IF EXISTS `student`")
            addBatch("CREATE TABLE `student` (\n" +
                    "  `id_student` int NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "  `lastname` varchar(40) NOT NULL,\n" +
                    "  `firstname` varchar(40) NOT NULL,\n" +
                    "  `middlename` varchar(40) DEFAULT NULL,\n" +
                    "  `group` varchar(10) NOT NULL,\n" +
                    "  `sex` set('male','female') NOT NULL,\n" +
                    "  `birthday` date NOT NULL\n" +
                    ")")

            addBatch("DROP TABLE IF EXISTS  `group`")
            addBatch("CREATE TABLE `group` (\n" +
                    "  `group_number` varchar(10) NOT NULL PRIMARY KEY,\n" +
                    "  `curriculum_id` int NOT NULL,\n" +
                    "  `qualification_number` set('bachelor','master','graduate') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL\n" +
                    ")")

            addBatch("DROP TABLE IF EXISTS `discipline`")
            addBatch("CREATE TABLE `discipline` (\n" +
                    "  `subject_code` int NOT NULL PRIMARY KEY,\n" +
                    "  `subject_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                    "  `department_code` int NOT NULL\n" +
                    ")")

        }
    }


    /*
    init{
        try{
            connection =
                DriverManager.getConnection(//статический метод getConnection возвращает объект типа connection
                    "jdbc:mysql://$address:$port/$dbName?serverTimezone=UTC",//ссылка для доступа( протокол доступа jdbc..)
                    user,
                    password
                )
            /**
             * Демонстрационная часть
             */
            val s = connection?.createStatement() //создание утверждения
            CreateDB(connection!!)
            /*
            var sql_create ="create table if not exists `students` (\n" +
                    "    `id` int primary key auto_increment,\n" +
                    "    `lastname` varchar(40) not null,\n" +
                    "    `firstname` varchar(40) not null,\n" +
                    "    `middlename` varchar(40)  null,\n" +
                    "    `group` varchar(10) not null,\n" +
                    "    `sex` set('male','female') not null,\n" +
                    "    `birthday` date not null,\n" +
                    "    index `name` (`lastname`,`firstname`,`middlename`)\n" +
                    "                                      )"
            s?.execute(sql_create)

            sql_create="create table if not exists `groups` (\n" +
                    "    `group_number` varchar(10) primary key,\n" +
                    "    `curriculum_id` int not null,\n" +
                    "    `qualification_number` set('1','2','3','4') not null\n" +
                    ");"
            s?.execute(sql_create)
            sql_create="create table if not exists `qualification`(\n" +
                    "    `number_qualification` int(1) not null primary key ,\n" +
                    "    `name` SET('bachelor','`s degree master','`s degree postgraduate study') NOT NULL\n" +
                    ");"
            s?.execute(sql_create)
            sql_create="create table if not exists `direction`(\n" +
                    "    `code_number` int primary key,\n" +
                    "    `name` varchar(30) not null\n" +
                    ");"

             */
            //s?.execute ("delete from `test`")

/*
            (1..10).forEach{
                val sql_insert = "insert into `test` (text_field,int_field) values ('ТекСтовОе ПоЛе :)',$it)"
                s?.execute(sql_insert)//Добавление записей в таблицу
            }
*/
            //Защита от sql инъекций
            /*
            (1..10).forEach{
                val ps = connection?.prepareStatement(
                    "insert into `test` (text_field,int_field) values (?,?)"
                )
                ps?.setString(1,"Здесь было текстовое поле")
                ps?.setInt(2,it)
                val rows = ps?.executeUpdate()//количество строк подходящих под запрос
                println(rows)
            }
             */
        }catch (e: SQLException){
            println("Ошибка создания таблицы: \n${e.toString()}")
        }
    }
    */
    /*
    fun CreateDB(connection : Connection){
        val s=connection.createStatement()

        //Создание таблицы студенты
        var sql_create ="create table if not exists `student` (\n" +
                "    `id_student` int primary key auto_increment,\n" +
                "    `lastname` varchar(40) not null,\n" +
                "    `firstname` varchar(40) not null,\n" +
                "    `middlename` varchar(40)  null,\n" +
                "    `group` varchar(10) not null,\n" +
                "    `sex` set('male','female') not null,\n" +
                "    `birthday` date not null,\n" +
                "    index `name` (`lastname`,`firstname`,`middlename`)\n" +
                "                                      );"
        s.execute(sql_create)

        //Создание таблицы группа
        sql_create="create table if not exists `group` (\n" +
                "    `group_number` varchar(10) primary key,\n" +
                "    `curriculum_id` int not null,\n" +
                "    `qualification_number` set('1','2','3','4') not null\n" +
                ");"
        s.execute(sql_create)

        //Создание таблицы квалификации
        sql_create="create table if not exists `qualification`(\n" +
                "    `number_qualification` int(1) not null primary key ,\n" +
                "    `name_qualification` SET('bachelor','`s degree master','`s degree postgraduate study') NOT NULL\n" +
                ");"
        s.execute(sql_create)

        //Создание таблицы направления
        sql_create="create table if not exists `direction`(\n" +
                "    `direction_code` int primary key,\n" +
                "    `direction_name` varchar(30) not null\n" +
                ");"
        s.execute(sql_create)

        sql_create="CREATE TABLE if not exists `discipline` (\n" +
                "`lesson_code` INT NOT NULL AUTO_INCREMENT primary key ,\n" +
                "`lesson_name` VARCHAR(30) NOT NULL ,\n" +
                "`department_code` INT NOT NULL \n"+
                ");"
        s.execute(sql_create)

        sql_create="create table if not exists `department` (\n"+
                "`department_code` int  auto_increment primary key,\n"+
                "`department_name` varchar(30) not null \n"+
                ");"
        s.execute(sql_create)

        sql_create="create table if not exists `curriculum` (\n"+
                "`id_curriculum` int auto_increment primary key,\n"+
                "`year_study` int(4) not null,\n"+
                "`direction_code` int not null \n"+
                ");"
        s.execute(sql_create)
        /*
        sql_create="create table if not exists `curriculum_lessons` (\n"+
                "`id` int primary key auto_increment,\n"+
                "`id_curriculum` int not null ,\n"+
                "`lesson_code` int not null , \n"+
                "`semestr` "
         */
    }
    fun ConnectDB( connection: Connection){
        val sql_connect="alter table `students` add foreign key (`group`) references `groups`(`group_number`) on delete restrict on update cascade"
        val s=connection.createStatement()
        s.execute(sql_connect)
    }
     */
}
