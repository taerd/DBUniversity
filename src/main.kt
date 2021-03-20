fun main(){
    val db = DBHelper("university")
    db.createDatabase()
    db.run{
        ReadDataCsv("department.csv")
        ReadDataCsv("discipline.csv")
        ReadDataCsv("direction.csv")
        ReadDataCsv("curriculum.csv")
        ReadDataCsv("curriculum_subject.csv")
    }
    //db.test()
}