import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBHelper(
    val dbName : String,
    val address : String = "localhost",
    val port : Int = 3306,
    val user : String = "root",
    val password : String = "root"
) {
    private var connection : Connection?=null
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
}
