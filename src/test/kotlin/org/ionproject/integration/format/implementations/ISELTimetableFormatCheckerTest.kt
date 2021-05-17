package org.ionproject.integration.format.implementations

import org.ionproject.integration.format.exceptions.FormatCheckException
import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.utils.CompositeException
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ISELTimetableFormatCheckerTest {

    private val validTimeTableJson =
        "[ { \"extraction_method\": \"lattice\", \"top\": 113.945274, \"left\": 53.875286, \"width\": 489.77463, \"height\": 480.44003, \"right\": 543.6499, \"bottom\": 594.3853, \"data\": [ [ { \"top\": 113.945274, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.439293, \"text\": \"Segunda\" }, { \"top\": 113.945274, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.439293, \"text\": \"Terça\" }, { \"top\": 113.945274, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.439293, \"text\": \"Quarta\" }, { \"top\": 113.945274, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.439293, \"text\": \"Quinta\" }, { \"top\": 113.945274, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.439293, \"text\": \"Sexta\" }, { \"top\": 113.945274, \"left\": 473.86923, \"width\": 69.78067, \"height\": 15.439293, \"text\": \"Sábado\" } ], [ { \"top\": 129.38457, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.475281, \"text\": \"8.00 - 8.30\" }, { \"top\": 129.38457, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.475281, \"text\": \"\" } ], [ { \"top\": 144.85985, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480118, \"text\": \"8.30 - 9.00\" }, { \"top\": 144.85985, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480118, \"text\": \"\" } ], [ { \"top\": 160.33997, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480331, \"text\": \"9.00 - 9.30\" }, { \"top\": 160.33997, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480331, \"text\": \"\" } ] ] }, { \"extraction_method\": \"lattice\", \"top\": 606.47534, \"left\": 53.875286, \"width\": 479.09586, \"height\": 109.97766, \"right\": 532.9711, \"bottom\": 716.453, \"data\": [ [ { \"top\": 606.47534, \"left\": 53.875286, \"width\": 52.82519, \"height\": 10.875671, \"text\": \"ALGA[I] (T)\" }, { \"top\": 606.47534, \"left\": 106.70048, \"width\": 181.70715, \"height\": 10.875671, \"text\": \"Teresa Maria de Araújo Melo Quinteiro\" }, { \"top\": 606.47534, \"left\": 288.40762, \"width\": 122.303314, \"height\": 10.875671, \"text\": \"\" }, { \"top\": 606.47534, \"left\": 410.71094, \"width\": 122.26019, \"height\": 10.875671, \"text\": \"\" } ], [ { \"top\": 617.351, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.068726, \"text\": \"E[I] (P)\" }, { \"top\": 617.351, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.068726, \"text\": \"Fernando dos Santos Azevedo\" }, { \"top\": 617.351, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.068726, \"text\": \"\" }, { \"top\": 617.351, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.068726, \"text\": \"\" } ], [ { \"top\": 628.41974, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.040283, \"text\": \"João Manuel Ferreira Martins\" }, { \"top\": 628.41974, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.040283, \"text\": \"\" } ] ] } ]"
    private val validButNotMatchingJson =
        "[ { \"extraction_method\": \"lattice\", \"top\": 113.945274, \"left\": 53.875286, \"width\": 489.77463, \"height\": 480.44003, \"right\": 543.6499, \"bottom\": 594.3853, \"data\": [ [ { \"top\": 113.945274, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.439293, \"text\": \"Segunda\" }, { \"top\": 113.945274, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.439293, \"text\": \"Terça\" }, { \"top\": 113.945274, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.439293, \"text\": \"Quarta\" }, { \"top\": 113.945274, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.439293, \"text\": \"Quinta\" }, { \"top\": 113.945274, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.439293, \"text\": \"Sexta\" }, { \"top\": 113.945274, \"left\": 473.86923, \"width\": 69.78067, \"height\": 15.439293, \"text\": \"Sábado\" } ], [ { \"top\": 129.38457, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.475281, \"text\": \"8.00 - 8.30\" }, { \"top\": 129.38457, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.475281, \"text\": \"\" } ], [ { \"top\": 144.85985, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480118, \"text\": \"8.30 - 9.00\" }, { \"top\": 144.85985, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480118, \"text\": \"\" } ], [ { \"top\": 160.33997, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480331, \"text\": \"9.00 - 9.30\" }, { \"top\": 160.33997, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480331, \"text\": \"\" } ] ] }, { \"extraction_method\": \"lattice\", \"top\": 606.47534, \"left\": 53.875286, \"width\": 479.09586, \"height\": 109.97766, \"right\": 532.9711, \"bottom\": 716.453, \"data\": [ [ { \"top\": 606.47534, \"left\": 53.875286, \"width\": 52.82519, \"height\": 10.875671, \"text\": \"ALGA[I] (T)\" }, { \"top\": 606.47534, \"left\": 106.70048, \"width\": 181.70715, \"height\": 10.875671, \"text\": \"Teresa Maria de Araújo Melo Quinteiro\" }, { \"top\": 606.47534, \"left\": 288.40762, \"width\": 122.303314, \"height\": 10.875671, \"text\": \"\" }, { \"top\": 606.47534, \"left\": 410.71094, \"width\": 122.26019, \"height\": 10.875671, \"text\": \"\" } ], [ { \"top\": 617.351, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.068726, \"text\": \"E[I] (P)\" }, { \"top\": 617.351, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.068726, \"text\": \"Fernando dos Santos Azevedo\" }, { \"top\": 617.351, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.068726, \"text\": \"\" }, { \"top\": 617.351, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.068726, \"text\": \"\" } ], [ { \"top\": 628.41974, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.040283, \"text\": \"João Manuel Ferreira Martins\" }, { \"top\": 628.41974, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.040283, \"text\": \"\" } ] ] } ], { \"top\": 628.41974, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.040283, \"text\": \"\" }"
    private val invalidTimeTableJson =
        "[ { \"extraction_method\": \"lattice\", \"top\": 113.945274, \"left\": 53.875286, \"width\": 489.77463, \"height\": 480.44003, \"right\": 543.6499, \"bottom\": 594.3853, \"data\": [ [ { \"top\": 113.945274, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.439293, \"text\": \"Segunda\" }, { \"top\": 113.945274, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.439293, \"text\": \"Terça\" }, { \"top\": 113.945274, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.439293, \"text\": \"Quarta\" }, { \"top\": 113.945274, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.439293, \"text\": \"Quinta\" }, { \"top\": 113.945274, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.439293, \"text\": \"Sexta\" }, { \"top\": 113.945274, \"left\": 473.86923, \"width\": 69.78067, \"height\": 15.439293, \"text\": \"Sábado\" } ], [ { \"top\": 129.38457, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.475281, \"text\": \"8.00 - 8.30\" }, { \"top\": 129.38457, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.475281, \"text\": \"\" } ], [ { \"top\": 144.85985, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480118, \"text\": \"8.30 - 9.00\" }, { \"top\": 144.85985, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480118, \"text\": \"\" } ], [ { \"top\": 160.33997, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480331, \"text\": \"9.00 - 9.30\" }, { \"top\": 160.33997, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480331, \"text\": \"\" } ] ] },{ ]"

    private val exactMatch = "Turma: LI11D Ano Letivo: 2019/20-Verão\r"
    private val matchingText = "$exactMatch the quick fox jumps over the lazy dog"
    private val notMatchingText = "Alice 123"
    private val validUTCDate = "20210516T214838Z"
    private val validInstructorJson = "[{\"extraction_method\":\"stream\",\"top\":671.0,\"left\":36.0,\"width\":521.0,\"height\":152.0,\"right\":557.0,\"bottom\":823.0,\"data\":[[{\"top\":677.97,\"left\":40.8,\"width\":43.77017593383789,\"height\":3.7100000381469727,\"text\":\"ALGA[T] (T)\"},{\"top\":677.97,\"left\":120.48,\"width\":107.44999694824219,\"height\":3.7100000381469727,\"text\":\"Sónia Raquel Ferreira Carvalho\"}],[{\"top\":691.65,\"left\":40.8,\"width\":62.52817153930664,\"height\":3.7100000381469727,\"text\":\"ALGA[T] - 1 (T/P)\"},{\"top\":691.65,\"left\":120.48,\"width\":127.38999938964844,\"height\":3.7100000381469727,\"text\":\"Carlos Miguel Ferreira Melro Leandro\"}],[{\"top\":705.33,\"left\":40.8,\"width\":42.10811233520508,\"height\":3.7100000381469727,\"text\":\"IC[T] - 1 (P)\"},{\"top\":705.33,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":719.01,\"left\":40.8,\"width\":42.38066482543945,\"height\":3.7100000381469727,\"text\":\"IC[T] - 2 (P)\"},{\"top\":719.01,\"left\":120.48,\"width\":103.88999938964844,\"height\":3.7100000381469727,\"text\":\"Dora Helena Avelar Gonçalves\"}],[{\"top\":732.69,\"left\":40.8,\"width\":30.850666046142578,\"height\":3.7100000381469727,\"text\":\"IC[T] (T)\"},{\"top\":732.69,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":746.37,\"left\":40.8,\"width\":49.82218551635742,\"height\":3.7100000381469727,\"text\":\"LSD[T] - 1 (P)\"},{\"top\":746.37,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":760.05,\"left\":40.8,\"width\":38.00986862182617,\"height\":3.7100000381469727,\"text\":\"LSD[T] (T)\"},{\"top\":760.05,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":773.73,\"left\":40.8,\"width\":50.310665130615234,\"height\":3.7100000381469727,\"text\":\"PG I[T] - 1 (P)\"},{\"top\":773.73,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}],[{\"top\":787.41,\"left\":40.8,\"width\":38.78065872192383,\"height\":3.7100000381469727,\"text\":\"PG I[T] (T)\"},{\"top\":787.41,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}]]}]"
    private val invalidInstructorJson = "[{\"extraction_method\":\"stream\",\"top\":671.0,\"left\":36.0,\"width\":521.0,\"height\":152.0,\"right\":557.0,\"bottom\":823.0,\"data\":[[{\"top\":677.97,\"left\":40.8,\"width\":43.77017593383789,\"height\":3.7100000381469727,\"text\":\"ALGA[T] (T)\"},{\"top\":677.97,\"left\":120.48,\"width\":107.44999694824219,\"height\":3.7100000381469727,\"text\":\"Sónia Raquel Ferreira Carvalho\"}],[{\"top\":691.65,\"left\":40.8,\"width\":62.52817153930664,\"height\":3.7100000381469727,\"text\":\"ALGA[T] - 1 (T/P)\"},{\"top\":691.65,\"left\":120.48,\"width\":127.38999938964844,\"height\":3.7100000381469727,\"text\":\"Carlos Miguel Ferreira Melro Leandro\"}],[{\"top\":705.33,\"left\":40.8,\"width\":42.10811233520508,\"height\":3.7100000381469727,\"text\":\"IC[T] - 1 (P)\"},{\"top\":705.33,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":719.01,\"left\":40.8,\"width\":42.38066482543945,\"height\":3.7100000381469727,\"text\":\"IC[T] - 2 (P)\"},{\"top\":719.01,\"left\":120.48,\"width\":103.88999938964844,\"height\":3.7100000381469727,\"text\":\"Dora Helena Avelar Gonçalves\"}],[{\"top\":732.69,\"left\":40.8,\"width\":30.850666046142578,\"height\":3.7100000381469727,\"text\":\"IC[T] (T)\"},{\"top\":732.69,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":746.37,\"left\":40.8,\"width\":49.82218551635742,\"height\":3.7100000381469727,\"text\":\"LSD[T] - 1 (P)\"},{\"top\":746.37,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":760.05,\"left\":40.8,\"width\":38.00986862182617,\"height\":3.7100000381469727,\"text\":\"LSD[T] (T)\"},{\"top\":760.05,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":773.73,\"left\":40.8,\"width\":50.310665130615234,\"height\":3.7100000381469727,\"text\":\"PG I[T] - 1 (P)\"},{\"top\":773.73,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}],[{\"top\":787.41,\"left\":40.8,\"width\":38.78065872192383,\"height\":3.7100000381469727,\"text\":\"PG I[T] (T)\"},{\"top\":787.41,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}]]"

    @Test
    fun `when Both Fields Of Dynamic Object Are Valid then Assert Result True`() {
        val dynamicObject = RawTimetableData(
            validTimeTableJson,
            listOf(exactMatch),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val res = fc.checkFormat(dynamicObject).orThrow()
        assertTrue(res)
    }

    @Test
    fun `when Both Fields Do Not Match then Throw CompositeException`() {
        val dynamicObject = RawTimetableData(
            validButNotMatchingJson,
            listOf(notMatchingText),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val ex = assertThrows<CompositeException> { fc.checkFormat(dynamicObject).orThrow() }
        assertEquals("FormatCheckException", ex.exceptions[0].javaClass.simpleName)
        assertEquals("FormatCheckException", ex.exceptions[1].javaClass.simpleName)
    }

    @Test
    fun `when Invalid Json And Matching String then Throw FormatCheckException`() {
        val dynamicObject = RawTimetableData(
            invalidTimeTableJson,
            listOf(matchingText),
            invalidInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val ex = assertThrows<CompositeException> {
            fc.checkFormat(dynamicObject).orThrow()
        }
        assertEquals(2, ex.exceptions.count())
        assertTrue(ex.exceptions.all { it is FormatCheckException })
    }

    @Test
    fun `when Valid but Not Matching Json then Throw FormatCheckException`() {
        val dynamicObject = RawTimetableData(
            validButNotMatchingJson,
            listOf(matchingText),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val ex = assertThrows<FormatCheckException> {
            fc.checkFormat(dynamicObject).orThrow()
        }
        assertEquals("The timetable table changed its format", ex.message)
    }

    @Test
    fun `when Valid Json And Matching Text then Assert Result Is True`() {
        val dynamicObject = RawTimetableData(
            validTimeTableJson,
            listOf(matchingText),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val res = fc.checkFormat(dynamicObject).orThrow()
        assertTrue(res)
    }

    @Test
    fun `when Valid Json And Empty String then Throw FormatCheckException`() {
        val dynamicObject = RawTimetableData(
            validTimeTableJson,
            listOf(""),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val ex = assertThrows<FormatCheckException> { fc.checkFormat(dynamicObject).orThrow() }
        assertEquals("The timetable header changed its format", ex.message)
    }

    @Test
    fun `when Valid Json And Not Matching Text then Throw FormatCheckException`() {
        val dynamicObject = RawTimetableData(
            validTimeTableJson,
            listOf(notMatchingText),
            validInstructorJson,
            validUTCDate
        )
        val fc = ISELTimetableFormatChecker()
        val ex = assertThrows<FormatCheckException> { fc.checkFormat(dynamicObject).orThrow() }
        assertEquals("The timetable header changed its format", ex.message)
    }
}
