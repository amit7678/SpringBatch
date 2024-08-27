package com.batch.batchproject.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.batch.batchproject.model.Product;

@Configuration
public class BatchConfig {
	
	 /**
     * Defines a Spring Batch Job bean.
     *
     * @param jobRepository the JobRepository used to manage job executions
     * @param listener the JobCompletionNotificationImpl listener for job events
     * @param steps the Step to be executed by the job
     * @return a Job instance configured with the provided parameters
     */
	
	@Bean
	public Job jobBean(JobRepository jobRepository,JobCompletionNotificationImpl listener, Step steps) 
	{
		 return new JobBuilder("job", jobRepository) // Create a new JobBuilder with name "job"
	                .listener(listener) // Add a listener for job completion events
	                .start(steps) // Define the first step of the job
	                .build(); // Build and return the Job instance
	}
	
    /**
     * Defines a Spring Batch Step bean.
     *
     * @param jobRepository the JobRepository used to manage step executions
     * @param transactionManager the transaction manager for the step
     * @param reader the ItemReader to read items
     * @param processor the ItemProcessor to process items
     * @param writer the ItemWriter to write items
     * @return a Step instance configured with the provided parameters
     */
	
	@Bean
	public Step steps(JobRepository jobRepository, 
			 DataSourceTransactionManager transactionManager,
			 ItemReader<Product> reader, ItemProcessor<Product, Product> processor,
			 ItemWriter<Product> writer)
	{
		return new StepBuilder("jobStep", jobRepository)
				 .<Product,Product>chunk(5,transactionManager) // Configure chunk size and transaction manager
				.reader(reader)  // Set the ItemReader to read items
				.processor(processor) // Set the ItemProcessor to process items
				.writer(writer) // Set the ItemWriter to write items
			    .build();
	 	
	}
	 /**
     * Defines a FlatFileItemReader bean for reading CSV files.
     *
     * @return a FlatFileItemReader instance configured to read Product data from a CSV file
     */
	
	@Bean
	public FlatFileItemReader<Product>reader(){
		return new FlatFileItemReaderBuilder<Product>()
				 .name("itemReader") // Set the name of the reader
	                .resource(new ClassPathResource("data.csv"))
	                .delimited()  // Define the file format as delimited (CSV)
	                .names("productId", "title", "description", "price", "discount")  // Specify the column names in the CSV
	                .targetType(Product.class)  // Map the CSV data to the Product class
	                .build(); // Build and return the FlatFileItemReader instance
	 }
	/**
     * Defines an ItemProcessor bean.
     *
     * @return an ItemProcessor instance that processes Product items
     */
	@Bean
	public ItemProcessor<Product, Product> itemProcessor() {
		return new CustumItemProcessor();  // Return a custom item processor implementation
	}
	
	/**
     * Defines an ItemWriter bean for writing data to a database.
     *
     * @param dataSource the DataSource used for database operations
     * @return an ItemWriter instance configured to write Product data to the database
     */
	
	 @Bean
	    public ItemWriter<Product> itemWriter(DataSource dataSource) {

	        return new JdbcBatchItemWriterBuilder<Product>()
	                .sql("insert into products(product_id,title,description,price,discount,discounted_price)values(:productId, :title, :description, :price, :discount, :discountedPrice)")
	                .dataSource(dataSource)  // Set the DataSource to be used for writing
	                .beanMapped()   // Map the Product properties to SQL parameters
	                .build();     // Build and return the JdbcBatchItemWriter instance

	    }
	}
		
		
