fun main(){
    val db = DBHelper("university")
    db.run{
        ReadDataCsv("department.csv")
        ReadDataCsv("d")
    }
}