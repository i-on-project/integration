package org.ionproject.integration.job

import javax.sql.DataSource
import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.YmlBytesFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.model.external.generic.ICoreModel
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.ionproject.integration.step.chunkbased.processor.GenericProcessor
import org.ionproject.integration.step.chunkbased.reader.GenericReader
import org.ionproject.integration.step.chunkbased.writer.GenericCoreWriter
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.PostUploadTasklet
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Generic(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Autowired
    val ds: DataSource
) {
    @Bean
    fun genericJob() = jobBuilderFactory.get("Generic Batch Job")
        .start(taskletStep("Download Yaml", downloadAndCompareTasklet()))
        .next(genericParseAndUploadToCoreStep())
        .next(taskletStep("PostUploadStep", postUploadStep()))
        .build()

    private fun genericParseAndUploadToCoreStep(): Step {
        return stepBuilderFactory.get("Parse And Upload to Core Step")
            .chunk<IInternalModel, ICoreModel>(1)
            .reader(genericReader())
            .processor(genericProcessor())
            .writer(genericCoreWriter())
            .build()
    }

    @StepScope
    @Bean("downloadGeneric")
    fun downloadAndCompareTasklet(): DownloadAndCompareTasklet {
        val ymlChecker = YmlBytesFormatChecker()
        val downloader = FileDownloaderImpl(ymlChecker)
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, fileComparator)
    }

    private fun taskletStep(name: String, tasklet: Tasklet): TaskletStep {
        return stepBuilderFactory
            .get(name)
            .tasklet(tasklet)
            .build()
    }
    @StepScope
    @Bean
    fun genericReader() = GenericReader()
    @StepScope
    @Bean
    fun genericProcessor() = GenericProcessor()
    @StepScope
    @Bean
    fun genericCoreWriter() = GenericCoreWriter()
    @StepScope
    @Bean("genericPostUpload")
    fun postUploadStep() = PostUploadTasklet()
}
