package org.ionproject.integration.step.chunkbased.processor

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.format.exceptions.FormatCheckException
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        FormatVerifierProcessor::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.localFileDestination=src/test/resources/invalid.pdf",
        "isel-timetable.pdfKey=pdf-path"
    ]
)
@SpringBatchTest
internal class FormatVerifierProcessorTest {
    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Autowired
    private lateinit var processor: FormatVerifierProcessor

    private val validJson =
        "[ { \"extraction_method\": \"lattice\", \"top\": 113.945274, \"left\": 53.875286, \"width\": 489.77463, \"height\": 480.44003, \"right\": 543.6499, \"bottom\": 594.3853, \"data\": [ [ { \"top\": 113.945274, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.439293, \"text\": \"Segunda\" }, { \"top\": 113.945274, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.439293, \"text\": \"Terça\" }, { \"top\": 113.945274, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.439293, \"text\": \"Quarta\" }, { \"top\": 113.945274, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.439293, \"text\": \"Quinta\" }, { \"top\": 113.945274, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.439293, \"text\": \"Sexta\" }, { \"top\": 113.945274, \"left\": 473.86923, \"width\": 69.78067, \"height\": 15.439293, \"text\": \"Sábado\" } ], [ { \"top\": 129.38457, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.475281, \"text\": \"8.00 - 8.30\" }, { \"top\": 129.38457, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.475281, \"text\": \"\" }, { \"top\": 129.38457, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.475281, \"text\": \"\" } ], [ { \"top\": 144.85985, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480118, \"text\": \"8.30 - 9.00\" }, { \"top\": 144.85985, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480118, \"text\": \"\" }, { \"top\": 144.85985, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480118, \"text\": \"\" } ], [ { \"top\": 160.33997, \"left\": 54.97015, \"width\": 69.60277, \"height\": 15.480331, \"text\": \"9.00 - 9.30\" }, { \"top\": 160.33997, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 194.41798, \"width\": 69.87288, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 264.29086, \"width\": 69.8483, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 334.13916, \"width\": 69.84784, \"height\": 15.480331, \"text\": \"\" }, { \"top\": 160.33997, \"left\": 403.987, \"width\": 69.88223, \"height\": 15.480331, \"text\": \"\" } ] ] }, { \"extraction_method\": \"lattice\", \"top\": 606.47534, \"left\": 53.875286, \"width\": 479.09586, \"height\": 109.97766, \"right\": 532.9711, \"bottom\": 716.453, \"data\": [ [ { \"top\": 606.47534, \"left\": 53.875286, \"width\": 52.82519, \"height\": 10.875671, \"text\": \"ALGA[I] (T)\" }, { \"top\": 606.47534, \"left\": 106.70048, \"width\": 181.70715, \"height\": 10.875671, \"text\": \"Teresa Maria de Araújo Melo Quinteiro\" }, { \"top\": 606.47534, \"left\": 288.40762, \"width\": 122.303314, \"height\": 10.875671, \"text\": \"\" }, { \"top\": 606.47534, \"left\": 410.71094, \"width\": 122.26019, \"height\": 10.875671, \"text\": \"\" } ], [ { \"top\": 617.351, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.068726, \"text\": \"E[I] (P)\" }, { \"top\": 617.351, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.068726, \"text\": \"Fernando dos Santos Azevedo\" }, { \"top\": 617.351, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.068726, \"text\": \"\" }, { \"top\": 617.351, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.068726, \"text\": \"\" } ], [ { \"top\": 628.41974, \"left\": 53.875286, \"width\": 52.82519, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 106.70048, \"width\": 181.70715, \"height\": 11.040283, \"text\": \"João Manuel Ferreira Martins\" }, { \"top\": 628.41974, \"left\": 288.40762, \"width\": 122.303314, \"height\": 11.040283, \"text\": \"\" }, { \"top\": 628.41974, \"left\": 410.71094, \"width\": 122.26019, \"height\": 11.040283, \"text\": \"\" } ] ] } ]"
    private val invalidJson =
        "[ { \"extraction_method\": \"lattice\", \"top\": 113.945274, \"left\": 53.875286, \"width\": 489.77463, \"height\": 480.44003, \"right\": 543.6499, \"bottom\": 594.3853, \"data\": [ [ { \"top\": 113.945274, \"left\": 124.572914, \"width\": 69.84507, \"height\": 15.439293, \"text\": \"Segunda\" }"
    private val validHeader = listOf("Turma: LI11D Ano Letivo: 2019/20-Verão")
    private val incompleteHeader = listOf("Turma: LI11D ")

    @Test
    fun whenRawDataHasValidFormat_thenAssertResultIsTrue() {
        val rd = RawData(validJson, validHeader)
        val tryResult = processor.process(rd) as Try<Boolean>
        assertTrue(tryResult.orThrow())
    }

    @Test
    fun whenRawDataHasIncompleteHeader_thenFormatCheckExceptionIsThrown() {
        val rd = RawData(validJson, incompleteHeader)
        val ex = assertThrows<FormatCheckException> { processor.process(rd)?.orThrow() }
        assertEquals("FormatCheckException", ex.javaClass.simpleName)
        assertEquals("The timetable header changed its format", ex.message)
    }

    @Test
    fun whenRawDataHasMalformedJson_thenFormatCheckExceptionIsThrown() {
        val rd = RawData(invalidJson, validHeader)
        val ex = assertThrows<FormatCheckException> { processor.process(rd)?.orThrow() }
        assertEquals("FormatCheckException", ex.javaClass.simpleName)
        assertEquals("The timetable table changed its format", ex.message)
    }

    @Test
    fun whenEmptyHeader_thenFormatCheckExceptionIsThrown() {
        val rd = RawData(validJson, listOf(""))
        val ex = assertThrows<FormatCheckException> { processor.process(rd)?.orThrow() }
        assertEquals("FormatCheckException", ex.javaClass.simpleName)
        assertEquals("The timetable header changed its format", ex.message)
    }

    @Test
    fun whenEmptyJson_thenFormatCheckExceptionIsThrown() {
        val rd = RawData("", validHeader)
        val ex = assertThrows<FormatCheckException> { processor.process(rd)?.orThrow() }
        assertEquals("FormatCheckException", ex.javaClass.simpleName)
        assertEquals("The timetable table changed its format", ex.message)
    }
}