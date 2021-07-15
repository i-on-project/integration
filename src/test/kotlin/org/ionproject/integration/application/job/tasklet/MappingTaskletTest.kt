package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.domain.common.Weekday
import org.ionproject.integration.domain.timetable.TimetableTeachers
import org.ionproject.integration.domain.timetable.dto.RawTimetableData
import org.ionproject.integration.domain.timetable.model.EventCategory
import org.ionproject.integration.domain.timetable.model.Instructor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        MappingTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
internal class MappingTaskletTest {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    @Autowired
    private lateinit var state: ISELTimetableJob.State

    private val jsonData =
        "[{\"extraction_method\":\"lattice\",\"top\":74.71997,\"left\":37.200073,\"width\":520.66552734375,\"height\":580.1778564453125,\"right\":557.8656,\"bottom\":654.8978,\"data\":[[{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":74.71997,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.603233337402344,\"text\":\"Segunda\"},{\"top\":74.71997,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.603233337402344,\"text\":\"Terça\"},{\"top\":74.71997,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.603233337402344,\"text\":\"Quarta\"},{\"top\":74.71997,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.603233337402344,\"text\":\"Quinta\"},{\"top\":74.71997,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.603233337402344,\"text\":\"Sexta\"},{\"top\":74.71997,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.603233337402344,\"text\":\"Sábado\"}],[{\"top\":93.323204,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.73564910888672,\"text\":\"8.00 - 8.30\"},{\"top\":93.323204,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.73564910888672,\"text\":\"\"},{\"top\":93.323204,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.73564910888672,\"text\":\"\"},{\"top\":93.323204,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.73564910888672,\"text\":\"\"},{\"top\":93.323204,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.73564910888672,\"text\":\"\"},{\"top\":93.323204,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.73564910888672,\"text\":\"\"},{\"top\":93.323204,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.73564910888672,\"text\":\"\"}],[{\"top\":112.05885,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.721115112304688,\"text\":\"8.30 - 9.00\"},{\"top\":112.05885,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.721115112304688,\"text\":\"\"},{\"top\":112.05885,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.721115112304688,\"text\":\"\"},{\"top\":112.05885,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.721115112304688,\"text\":\"\"},{\"top\":112.05885,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.721115112304688,\"text\":\"\"},{\"top\":112.05885,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.721115112304688,\"text\":\"\"},{\"top\":112.05885,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.721115112304688,\"text\":\"\"}],[{\"top\":130.77997,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719879150390625,\"text\":\"9.00 - 9.30\"},{\"top\":130.77997,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":130.77997,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":130.77997,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":130.77997,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":130.77997,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":130.77997,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719879150390625,\"text\":\"\"}],[{\"top\":149.49985,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720291137695312,\"text\":\"9.30 - 10.00\"},{\"top\":149.49985,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720291137695312,\"text\":\"\"},{\"top\":149.49985,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720291137695312,\"text\":\"\"},{\"top\":149.49985,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720291137695312,\"text\":\"\"},{\"top\":149.49985,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720291137695312,\"text\":\"\"},{\"top\":149.49985,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720291137695312,\"text\":\"\"},{\"top\":149.49985,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720291137695312,\"text\":\"\"}],[{\"top\":168.22014,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719863891601562,\"text\":\"10.00 - 10.30\"},{\"top\":168.22014,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719863891601562,\"text\":\"\"},{\"top\":168.22014,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719863891601562,\"text\":\"\"},{\"top\":168.22014,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719863891601562,\"text\":\"\"},{\"top\":168.22014,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719863891601562,\"text\":\"\"},{\"top\":168.22014,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719863891601562,\"text\":\"\"},{\"top\":168.22014,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719863891601562,\"text\":\"\"}],[{\"top\":186.94,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719879150390625,\"text\":\"10.30 - 11.00\"},{\"top\":186.94,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":186.94,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":186.94,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":186.94,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":186.94,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":186.94,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719879150390625,\"text\":\"\"}],[{\"top\":205.65988,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720413208007812,\"text\":\"11.00 - 11.30\"},{\"top\":205.65988,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720413208007812,\"text\":\"\"},{\"top\":205.65988,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720413208007812,\"text\":\"\"},{\"top\":205.65988,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720413208007812,\"text\":\"\"},{\"top\":205.65988,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720413208007812,\"text\":\"\"},{\"top\":205.65988,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720413208007812,\"text\":\"\"},{\"top\":205.65988,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720413208007812,\"text\":\"\"}],[{\"top\":224.3803,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719619750976562,\"text\":\"11.30 - 12.00\"},{\"top\":224.3803,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719619750976562,\"text\":\"\"},{\"top\":224.3803,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719619750976562,\"text\":\"\"},{\"top\":224.3803,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719619750976562,\"text\":\"\"},{\"top\":224.3803,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719619750976562,\"text\":\"\"},{\"top\":224.3803,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719619750976562,\"text\":\"\"},{\"top\":224.3803,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719619750976562,\"text\":\"\"}],[{\"top\":243.09991,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720062255859375,\"text\":\"12.00 - 12.30\"},{\"top\":243.09991,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":243.09991,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":243.09991,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":243.09991,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":243.09991,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":243.09991,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720062255859375,\"text\":\"\"}],[{\"top\":261.81998,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719970703125,\"text\":\"12.30 - 13.00\"},{\"top\":261.81998,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719970703125,\"text\":\"\"},{\"top\":261.81998,\"left\":185.91994,\"width\":74.39613342285156,\"height\":56.1600341796875,\"text\":\"IC[T] - 2 (P)LH2-F\\rIC[T] - 1 (P)L_H2\"},{\"top\":261.81998,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719970703125,\"text\":\"\"},{\"top\":261.81998,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719970703125,\"text\":\"\"},{\"top\":261.81998,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719970703125,\"text\":\"\"},{\"top\":261.81998,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719970703125,\"text\":\"\"}],[{\"top\":280.53995,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720062255859375,\"text\":\"13.00 - 13.30\"},{\"top\":280.53995,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":280.53995,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":280.53995,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":280.53995,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":280.53995,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720062255859375,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":299.26,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720001220703125,\"text\":\"13.30 - 14.00\"},{\"top\":299.26,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":299.26,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":299.26,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":299.26,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":299.26,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":317.98,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719879150390625,\"text\":\"14.00 - 14.30\"},{\"top\":317.98,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":317.98,\"left\":185.91994,\"width\":74.39613342285156,\"height\":56.160125732421875,\"text\":\"LSD[T] - 1 (P)L_H1\"},{\"top\":317.98,\"left\":260.31607,\"width\":74.4061279296875,\"height\":56.160125732421875,\"text\":\"ALGA[T] (T)\"},{\"top\":317.98,\"left\":334.7222,\"width\":74.3946533203125,\"height\":56.160125732421875,\"text\":\"ALGA[T] (T)\"},{\"top\":317.98,\"left\":409.11685,\"width\":74.41314697265625,\"height\":56.160125732421875,\"text\":\"IC[T] (T)\"},{\"top\":317.98,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719879150390625,\"text\":\"\"}],[{\"top\":336.6999,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.71990966796875,\"text\":\"14.30 - 15.00\"},{\"top\":336.6999,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.71990966796875,\"text\":\"\"},{\"top\":336.6999,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.71990966796875,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":355.4198,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.7203369140625,\"text\":\"15.00 - 15.30\"},{\"top\":355.4198,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.7203369140625,\"text\":\"\"},{\"top\":355.4198,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.7203369140625,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":374.14014,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.7198486328125,\"text\":\"15.30 - 16.00\"},{\"top\":374.14014,\"left\":111.51635,\"width\":74.4035873413086,\"height\":56.160125732421875,\"text\":\"ALGA[T] - 1 (T/P)E.1.33\"},{\"top\":374.14014,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.7198486328125,\"text\":\"\"},{\"top\":374.14014,\"left\":260.31607,\"width\":74.4061279296875,\"height\":56.160125732421875,\"text\":\"LSD[T] (T)\"},{\"top\":374.14014,\"left\":334.7222,\"width\":74.3946533203125,\"height\":56.160125732421875,\"text\":\"LSD[T] (T)\"},{\"top\":374.14014,\"left\":409.11685,\"width\":74.41314697265625,\"height\":112.31988525390625,\"text\":\"PG I[T] (T)\"},{\"top\":374.14014,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.7198486328125,\"text\":\"\"}],[{\"top\":392.86,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719879150390625,\"text\":\"16.00 - 16.30\"},{\"top\":392.86,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":392.86,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":411.57986,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.72039794921875,\"text\":\"16.30 - 17.00\"},{\"top\":411.57986,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.72039794921875,\"text\":\"\"},{\"top\":411.57986,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.72039794921875,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":430.30026,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.71954345703125,\"text\":\"17.00 - 17.30\"},{\"top\":430.30026,\"left\":111.51635,\"width\":74.4035873413086,\"height\":56.159759521484375,\"text\":\"PG I[T] - 1 (P)E.1.33\"},{\"top\":430.30026,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.71954345703125,\"text\":\"\"},{\"top\":430.30026,\"left\":260.31607,\"width\":74.4061279296875,\"height\":56.159759521484375,\"text\":\"IC[T] (T)\"},{\"top\":430.30026,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.71954345703125,\"text\":\"\"},{\"top\":430.30026,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.71954345703125,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":449.0198,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720123291015625,\"text\":\"17.30 - 18.00\"},{\"top\":449.0198,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720123291015625,\"text\":\"\"},{\"top\":449.0198,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720123291015625,\"text\":\"\"},{\"top\":449.0198,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720123291015625,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":467.73993,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.7200927734375,\"text\":\"18.00 - 18.30\"},{\"top\":467.73993,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":467.73993,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":467.73993,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"},{\"top\":0,\"left\":0,\"width\":0,\"height\":0,\"text\":\"\"}],[{\"top\":486.46002,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720001220703125,\"text\":\"18.30 - 19.00\"},{\"top\":486.46002,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":486.46002,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":486.46002,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":486.46002,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":486.46002,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720001220703125,\"text\":\"\"},{\"top\":486.46002,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720001220703125,\"text\":\"\"}],[{\"top\":505.18002,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.719879150390625,\"text\":\"19.00 - 19.30\"},{\"top\":505.18002,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":505.18002,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":505.18002,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":505.18002,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":505.18002,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.719879150390625,\"text\":\"\"},{\"top\":505.18002,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.719879150390625,\"text\":\"\"}],[{\"top\":523.8999,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.7200927734375,\"text\":\"19.30 - 20.00\"},{\"top\":523.8999,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":523.8999,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":523.8999,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":523.8999,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":523.8999,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.7200927734375,\"text\":\"\"},{\"top\":523.8999,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.7200927734375,\"text\":\"\"}],[{\"top\":542.62,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.72027587890625,\"text\":\"20.00 - 20.30\"},{\"top\":542.62,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.72027587890625,\"text\":\"\"},{\"top\":542.62,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.72027587890625,\"text\":\"\"},{\"top\":542.62,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.72027587890625,\"text\":\"\"},{\"top\":542.62,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.72027587890625,\"text\":\"\"},{\"top\":542.62,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.72027587890625,\"text\":\"\"},{\"top\":542.62,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.72027587890625,\"text\":\"\"}],[{\"top\":561.3403,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.71978759765625,\"text\":\"20.30 - 21.00\"},{\"top\":561.3403,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.71978759765625,\"text\":\"\"},{\"top\":561.3403,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.71978759765625,\"text\":\"\"},{\"top\":561.3403,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.71978759765625,\"text\":\"\"},{\"top\":561.3403,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.71978759765625,\"text\":\"\"},{\"top\":561.3403,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.71978759765625,\"text\":\"\"},{\"top\":561.3403,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.71978759765625,\"text\":\"\"}],[{\"top\":580.06006,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.7197265625,\"text\":\"21.00 - 21.30\"},{\"top\":580.06006,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.7197265625,\"text\":\"\"},{\"top\":580.06006,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.7197265625,\"text\":\"\"},{\"top\":580.06006,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.7197265625,\"text\":\"\"},{\"top\":580.06006,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.7197265625,\"text\":\"\"},{\"top\":580.06006,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.7197265625,\"text\":\"\"},{\"top\":580.06006,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.7197265625,\"text\":\"\"}],[{\"top\":598.7798,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.720703125,\"text\":\"21.30 - 22.00\"},{\"top\":598.7798,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.720703125,\"text\":\"\"},{\"top\":598.7798,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.720703125,\"text\":\"\"},{\"top\":598.7798,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.720703125,\"text\":\"\"},{\"top\":598.7798,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.720703125,\"text\":\"\"},{\"top\":598.7798,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.720703125,\"text\":\"\"},{\"top\":598.7798,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.720703125,\"text\":\"\"}],[{\"top\":617.5005,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.73046875,\"text\":\"22.00 - 22.30\"},{\"top\":617.5005,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.73046875,\"text\":\"\"},{\"top\":617.5005,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.73046875,\"text\":\"\"},{\"top\":617.5005,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.73046875,\"text\":\"\"},{\"top\":617.5005,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.73046875,\"text\":\"\"},{\"top\":617.5005,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.73046875,\"text\":\"\"},{\"top\":617.5005,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.73046875,\"text\":\"\"}],[{\"top\":636.23096,\"left\":37.200073,\"width\":74.31627655029297,\"height\":18.6668701171875,\"text\":\"22.30 - 23.00\"},{\"top\":636.23096,\"left\":111.51635,\"width\":74.4035873413086,\"height\":18.6668701171875,\"text\":\"\"},{\"top\":636.23096,\"left\":185.91994,\"width\":74.39613342285156,\"height\":18.6668701171875,\"text\":\"\"},{\"top\":636.23096,\"left\":260.31607,\"width\":74.4061279296875,\"height\":18.6668701171875,\"text\":\"\"},{\"top\":636.23096,\"left\":334.7222,\"width\":74.3946533203125,\"height\":18.6668701171875,\"text\":\"\"},{\"top\":636.23096,\"left\":409.11685,\"width\":74.41314697265625,\"height\":18.6668701171875,\"text\":\"\"},{\"top\":636.23096,\"left\":483.53,\"width\":74.33560180664062,\"height\":18.6668701171875,\"text\":\"\"}]]}]"
    private val textData =
        listOf("INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA\nÁrea Dep. de Eng. Eletrónica e Telecomunicações e de Computadores\nLicenciatura em Engenharia Eletrónica e Telecomunicações e de Computadores\nTurma : LT11Da Ano Letivo : 2020/21-Verão\n")
    private val instructorData =
        "[{\"extraction_method\":\"stream\",\"top\":671.0,\"left\":36.0,\"width\":521.0,\"height\":152.0,\"right\":557.0,\"bottom\":823.0,\"data\":[[{\"top\":677.97,\"left\":40.8,\"width\":43.77017593383789,\"height\":3.7100000381469727,\"text\":\"ALGA[T] (T)\"},{\"top\":677.97,\"left\":120.48,\"width\":107.44999694824219,\"height\":3.7100000381469727,\"text\":\"Sónia Raquel Ferreira Carvalho\"}],[{\"top\":691.65,\"left\":40.8,\"width\":62.52817153930664,\"height\":3.7100000381469727,\"text\":\"ALGA[T] - 1 (T/P)\"},{\"top\":691.65,\"left\":120.48,\"width\":127.38999938964844,\"height\":3.7100000381469727,\"text\":\"Carlos Miguel Ferreira Melro Leandro\"}],[{\"top\":705.33,\"left\":40.8,\"width\":42.10811233520508,\"height\":3.7100000381469727,\"text\":\"IC[T] - 1 (P)\"},{\"top\":705.33,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":719.01,\"left\":40.8,\"width\":42.38066482543945,\"height\":3.7100000381469727,\"text\":\"IC[T] - 2 (P)\"},{\"top\":719.01,\"left\":120.48,\"width\":103.88999938964844,\"height\":3.7100000381469727,\"text\":\"Dora Helena Avelar Gonçalves\"}],[{\"top\":732.69,\"left\":40.8,\"width\":30.850666046142578,\"height\":3.7100000381469727,\"text\":\"IC[T] (T)\"},{\"top\":732.69,\"left\":120.48,\"width\":95.19999694824219,\"height\":3.7100000381469727,\"text\":\"Vítor Manuel da Silva Costa\"}],[{\"top\":746.37,\"left\":40.8,\"width\":49.82218551635742,\"height\":3.7100000381469727,\"text\":\"LSD[T] - 1 (P)\"},{\"top\":746.37,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":760.05,\"left\":40.8,\"width\":38.00986862182617,\"height\":3.7100000381469727,\"text\":\"LSD[T] (T)\"},{\"top\":760.05,\"left\":120.48,\"width\":146.5900115966797,\"height\":3.7100000381469727,\"text\":\"José David Pereira Coutinho Gomes Antão\"}],[{\"top\":773.73,\"left\":40.8,\"width\":50.310665130615234,\"height\":3.7100000381469727,\"text\":\"PG I[T] - 1 (P)\"},{\"top\":773.73,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}],[{\"top\":787.41,\"left\":40.8,\"width\":38.78065872192383,\"height\":3.7100000381469727,\"text\":\"PG I[T] (T)\"},{\"top\":787.41,\"left\":120.48,\"width\":96.63999938964844,\"height\":3.7100000381469727,\"text\":\"Manuel Fernandes Carvalho\"}]]}]"
    private val validUTCDate = "2021-05-16T21:48:38Z"
    private val rawData = RawTimetableData(jsonData, textData, instructorData, validUTCDate)

    @Test
    fun whenMappingTasklet_thenBusinessObjectFilled_andReturnsExitStatusCompleted() {
        state.rawTimetableData = rawData
        val je = jobLauncherTestUtils.launchStep("RawData to Business Object")
        assertions(state.timetableTeachers)
        assertEquals(ExitStatus.COMPLETED, je.exitStatus)
    }

    private fun assertions(timetableTeacher: TimetableTeachers) {
        // Assert
        assertEquals(1, timetableTeacher.timetable.count())
        assertEquals(1, timetableTeacher.teachers.count())

        val school = "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA"
        val programme = "Licenciatura em Engenharia Eletrónica e Telecomunicações e de Computadores"
        val term = "2020-2021-2"
        val section = "LT11Da"
        // Common data
        assertEquals(school, timetableTeacher.timetable[0].school.name)
        assertEquals(programme, timetableTeacher.timetable[0].programme.name)
        assertEquals(term, timetableTeacher.timetable[0].calendarTerm)
        assertEquals(section, timetableTeacher.timetable[0].calendarSection)
        assertEquals(school, timetableTeacher.teachers[0].school.name)
        assertEquals(programme, timetableTeacher.teachers[0].programme.name)
        assertEquals(term, timetableTeacher.teachers[0].calendarTerm)
        assertEquals(section, timetableTeacher.teachers[0].calendarSection)

        // Timetable data
        val beginTime = LocalTime.parse(timetableTeacher.timetable[0].courses[0].events[0].beginTime)

        assertEquals(12, timetableTeacher.timetable[0].courses.count())
        assertEquals("IC", timetableTeacher.timetable[0].courses[0].label.acr)
        assertEquals(EventCategory.PRACTICE, timetableTeacher.timetable[0].courses[0].events[0].category)
        assertTrue(timetableTeacher.timetable[0].courses[0].events[0].location.contains("LH2-F"))
        assertTrue(timetableTeacher.timetable[0].courses[0].events[0].location.count() == 1)
        assertEquals(12, beginTime.hour)
        assertEquals(30, beginTime.minute)
        assertEquals("01:30", timetableTeacher.timetable[0].courses[0].events[0].duration)
        assertTrue(timetableTeacher.timetable[0].courses[0].events[0].weekdays.contains(Weekday.TUESDAY))

        assertEquals("03:00", timetableTeacher.timetable[0].courses[9].events[0].duration)
        assertTrue(timetableTeacher.timetable[0].courses[9].events[0].weekdays.contains(Weekday.FRIDAY))

        assertEquals("01:30", timetableTeacher.timetable[0].courses[2].events[0].duration)
        assertTrue(timetableTeacher.timetable[0].courses[4].events[0].weekdays.contains(Weekday.THURSDAY))

        // Faculty data
        assertEquals(8, timetableTeacher.teachers[0].courses.count())
        assertEquals("ALGA", timetableTeacher.teachers[0].courses[0].classDetail.acronym)
        assertEquals(1, timetableTeacher.teachers[0].courses[0].instructors.count())
        assertEquals("Sónia Raquel Ferreira Carvalho", timetableTeacher.teachers[0].courses[0].instructors[0].name)

        assertEquals("IC", timetableTeacher.teachers[0].courses[2].classDetail.acronym)
        assertEquals(2, timetableTeacher.teachers[0].courses[2].instructors.count())

        assertEquals(
            listOf(
                Instructor("Vítor Manuel da Silva Costa"),
                Instructor("Dora Helena Avelar Gonçalves")
            ),
            timetableTeacher.teachers[0].courses[2].instructors
        )

        assertEquals("LSD", timetableTeacher.teachers[0].courses[4].classDetail.acronym)
        assertEquals(1, timetableTeacher.teachers[0].courses[4].instructors.count())
        assertEquals(
            "José David Pereira Coutinho Gomes Antão",
            timetableTeacher.teachers[0].courses[4].instructors[0].name
        )
    }
}
