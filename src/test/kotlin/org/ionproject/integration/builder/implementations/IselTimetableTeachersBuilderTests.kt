package org.ionproject.integration.builder.implementations

import java.time.LocalDateTime
import org.ionproject.integration.model.internal.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IselTimetableTeachersBuilderTests {
    companion object {
        private val jsonData = "[{\"extraction_method\":\"lattice\",\"top\":113.945274,\"left\":53.875286,\"width\":489.7746276855469,\"height\":480.4400329589844,\"right\":543.6499,\"bottom\":594.3853,\"data\":[[{\"top\":0.0,\"left\":0.0,\"width\":0.0,\"height\":0.0,\"text\":\"\"},{\"top\":0.0,\"left\":0.0,\"width\":0.0,\"height\":0.0,\"text\":\"\"},{\"top\":113.945274,\"left\":124.572914,\"width\":69.8450698852539,\"height\":15.439292907714844,\"text\":\"Segunda\"},{\"top\":113.945274,\"left\":194.41798,\"width\":69.87287902832031,\"height\":15.439292907714844,\"text\":\"Terça\"},{\"top\":113.945274,\"left\":264.29086,\"width\":69.84829711914062,\"height\":15.439292907714844,\"text\":\"Quarta\"},{\"top\":113.945274,\"left\":334.13916,\"width\":69.84783935546875,\"height\":15.439292907714844,\"text\":\"Quinta\"},{\"top\":113.945274,\"left\":403.987,\"width\":69.88223266601562,\"height\":15.439292907714844,\"text\":\"Sexta\"},{\"top\":113.945274,\"left\":473.86923,\"width\":69.78067016601562,\"height\":15.439292907714844,\"text\":\"Sábado\"}],[{\"top\":268.84985,\"left\":53.875286,\"width\":1.0948638916015625,\"height\":15.480224609375,\"text\":\"\"},{\"top\":268.84985,\"left\":54.97015,\"width\":69.60276794433594,\"height\":15.480224609375,\"text\":\"12.30 - 13.00\"},{\"top\":268.84985,\"left\":124.572914,\"width\":69.8450698852539,\"height\":15.480224609375,\"text\":\"\"},{\"top\":268.84985,\"left\":194.41798,\"width\":69.87287902832031,\"height\":15.480224609375,\"text\":\"\"},{\"top\":268.84985,\"left\":264.29086,\"width\":69.84829711914062,\"height\":15.480224609375,\"text\":\"\"},{\"top\":268.84985,\"left\":334.13916,\"width\":69.84783935546875,\"height\":15.480224609375,\"text\":\"\"},{\"top\":268.84985,\"left\":403.987,\"width\":69.88223266601562,\"height\":93.0,\"text\":\"ALGA[I] (T)E.1.08\"},{\"top\":268.84985,\"left\":473.86923,\"width\":69.78067016601562,\"height\":15.480224609375,\"text\":\"\"}],[{\"top\":361.84985,\"left\":53.875286,\"width\":1.0948638916015625,\"height\":15.480224609375,\"text\":\"\"},{\"top\":361.84985,\"left\":54.97015,\"width\":69.60276794433594,\"height\":15.480224609375,\"text\":\"15.30 - 16.00\"},{\"top\":361.84985,\"left\":124.572914,\"width\":69.8450698852539,\"height\":93.01986694335938,\"text\":\"\"},{\"top\":361.84985,\"left\":194.41798,\"width\":69.87287902832031,\"height\":93.01986694335938,\"text\":\"\"},{\"top\":361.84985,\"left\":264.29086,\"width\":69.84829711914062,\"height\":46.559417724609375,\"text\":\"E[I] (T)G.0.08\\rE[I] (P)L_H2\"},{\"top\":361.84985,\"left\":334.13916,\"width\":69.84783935546875,\"height\":93.01986694335938,\"text\":\"\"},{\"top\":361.84985,\"left\":403.987,\"width\":69.88223266601562,\"height\":93.01986694335938,\"text\":\"\"},{\"top\":361.84985,\"left\":473.86923,\"width\":69.78067016601562,\"height\":15.480224609375,\"text\":\"\"}]]},{\"extraction_method\":\"lattice\",\"top\":606.47534,\"left\":53.875286,\"width\":479.0958557128906,\"height\":109.9776611328125,\"right\":532.9711,\"bottom\":716.453,\"data\":[[{\"top\":606.47534,\"left\":53.875286,\"width\":52.825191497802734,\"height\":10.87567138671875,\"text\":\"ALGA[I] (T)\"},{\"top\":606.47534,\"left\":106.70048,\"width\":181.7071533203125,\"height\":10.87567138671875,\"text\":\"Teresa Maria de Araújo Melo Quinteiro\"},{\"top\":606.47534,\"left\":288.40762,\"width\":122.30331420898438,\"height\":10.87567138671875,\"text\":\"\"},{\"top\":606.47534,\"left\":410.71094,\"width\":122.26019287109375,\"height\":10.87567138671875,\"text\":\"\"}],[{\"top\":617.351,\"left\":53.875286,\"width\":52.825191497802734,\"height\":11.0687255859375,\"text\":\"E[I] (P)\"},{\"top\":617.351,\"left\":106.70048,\"width\":181.7071533203125,\"height\":11.0687255859375,\"text\":\"Fernando dos Santos Azevedo\"},{\"top\":617.351,\"left\":288.40762,\"width\":122.30331420898438,\"height\":11.0687255859375,\"text\":\"\"},{\"top\":617.351,\"left\":410.71094,\"width\":122.26019287109375,\"height\":11.0687255859375,\"text\":\"\"}],[{\"top\":628.41974,\"left\":53.875286,\"width\":52.825191497802734,\"height\":11.040283203125,\"text\":\"\"},{\"top\":628.41974,\"left\":106.70048,\"width\":181.7071533203125,\"height\":11.040283203125,\"text\":\"João Manuel Ferreira Martins\"},{\"top\":628.41974,\"left\":288.40762,\"width\":122.30331420898438,\"height\":11.040283203125,\"text\":\"\"},{\"top\":628.41974,\"left\":410.71094,\"width\":122.26019287109375,\"height\":11.040283203125,\"text\":\"\"}],[{\"top\":639.46,\"left\":53.875286,\"width\":52.825191497802734,\"height\":10.9202880859375,\"text\":\"E[I] (T)\"},{\"top\":639.46,\"left\":106.70048,\"width\":181.7071533203125,\"height\":10.9202880859375,\"text\":\"João Manuel Ferreira Martins\"},{\"top\":639.46,\"left\":288.40762,\"width\":122.30331420898438,\"height\":10.9202880859375,\"text\":\"\"},{\"top\":639.46,\"left\":410.71094,\"width\":122.26019287109375,\"height\":10.9202880859375,\"text\":\"\"}]]}]"
        private val textData = listOf("INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA \nGabinete de Planeamento de Salas e Horários \nLicenciatura em Engenharia Informática e de Computadores \nTurma: LI11D Ano Letivo: 2019/20-Verão \n\n...")
        private val rawData = RawData(jsonData, textData)

        private val builder = IselTimetableTeachersBuilder()
    }

    @Test
    fun whenSetTimetable_thenReturnTimetableTeacherFullyFilled() {
        // Arrange
        builder.reset()

        // Act
        builder.setTimetable(rawData)
        val timetableTeacher = builder.getTimetableTeachers()

        // Assert
        assertions(timetableTeacher)
    }

    @Test
    fun whenSetTeacher_thenReturnTimetableTeacherFullyFilled() {
        // Arrange
        builder.reset()

        // Act
        builder.setTeachers(rawData)
        val timetableTeacher = builder.getTimetableTeachers()

        // Assert
        assertions(timetableTeacher)
    }

    @Test
    fun whenSetTeacher_ThenGetTimetableTeachers_thenBuilderObjectIsEmpty() {
        // Arrange
        builder.reset()

        // Act
        builder.setTeachers(rawData)
        val timetableTeacher = builder.getTimetableTeachers()
        val emptyTimetableTeacher = builder.getTimetableTeachers()

        // Assert
        assertEquals(0, emptyTimetableTeacher.timetable.count())
        assertEquals(0, emptyTimetableTeacher.teachers.count())
    }

    private fun assertions(timetableTeacher: TimetableTeachers) {
        // Assert
        assertEquals(1, timetableTeacher.timetable.count())
        assertEquals(1, timetableTeacher.teachers.count())

        // Common data
        assertEquals("INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA", timetableTeacher.timetable[0].school)
        assertEquals("Licenciatura em Engenharia Informática e de Computadores", timetableTeacher.timetable[0].programme)
        assertEquals("2019/20-Verão", timetableTeacher.timetable[0].calendarTerm)
        assertEquals("LI11D", timetableTeacher.timetable[0].classSection)
        assertEquals("INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA", timetableTeacher.teachers[0].school)
        assertEquals("Licenciatura em Engenharia Informática e de Computadores", timetableTeacher.teachers[0].programme)
        assertEquals("2019/20-Verão", timetableTeacher.teachers[0].calendarTerm)
        assertEquals("LI11D", timetableTeacher.teachers[0].classSection)

        // Timetable data
        val beginTime = LocalDateTime.parse(timetableTeacher.timetable[0].courses[0].begin_time)
        val endTime = LocalDateTime.parse(timetableTeacher.timetable[0].courses[0].end_time)

        assertEquals(3, timetableTeacher.timetable[0].courses.count())
        assertEquals("ALGA[I]", timetableTeacher.timetable[0].courses[0].acronym)
        assertEquals("(T)", timetableTeacher.timetable[0].courses[0].type)
        assertEquals("E.1.08", timetableTeacher.timetable[0].courses[0].room)
        assertEquals(12, beginTime.hour)
        assertEquals(30, beginTime.minute)
        assertEquals(15, endTime.hour)
        assertEquals(30, endTime.minute)
        assertEquals("PT3H", timetableTeacher.timetable[0].courses[0].duration)
        assertEquals("Sexta", timetableTeacher.timetable[0].courses[0].weekday)

        assertEquals("PT1H30M", timetableTeacher.timetable[0].courses[1].duration)
        assertEquals("Quarta", timetableTeacher.timetable[0].courses[1].weekday)

        // Faculty data
        assertEquals(3, timetableTeacher.teachers[0].faculty.count())
        assertEquals("ALGA[I]", timetableTeacher.teachers[0].faculty[0].course)
        assertEquals("(T)", timetableTeacher.teachers[0].faculty[0].course_type)
        assertEquals(1, timetableTeacher.teachers[0].faculty[0].teachers.count())
        assertEquals("Teresa Maria de Araújo Melo Quinteiro", timetableTeacher.teachers[0].faculty[0].teachers[0].name)

        assertEquals("E[I]", timetableTeacher.teachers[0].faculty[1].course)
        assertEquals("(P)", timetableTeacher.teachers[0].faculty[1].course_type)
        assertEquals(2, timetableTeacher.teachers[0].faculty[1].teachers.count())
        assertEquals("Fernando dos Santos Azevedo", timetableTeacher.teachers[0].faculty[1].teachers[0].name)
        assertEquals("João Manuel Ferreira Martins", timetableTeacher.teachers[0].faculty[1].teachers[1].name)
    }
}
