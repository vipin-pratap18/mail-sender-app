package com.mailsender.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

@SpringBootApplication
public class MailSenderAppApplication implements CommandLineRunner{
	
	@Autowired
	private MailSender sender;

	public static void main(String[] args) {
		SpringApplication.run(MailSenderAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			  //sender.sendEmailClean();
			//sender.getFileFromS3("Journey Mapping.xlsx");
			testS3FileSize();
			}catch(Exception ex) {
				
			}
		
	}
	
	
	public void testS3FileSize() {
		
		//size File Size is : 126227229
		//126248550.4
		
		//File size are in bytes
		
		//10485760 Bytes //10 MB
		
		String fileName = "Speeding Events Attributes Comparison Report_1567344469211_2019-09-01T14_16_00_484Z.csv";
		
		AWSCredentials credentials = new BasicAWSCredentials("AccessKey", "SecretKey");

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.EU_WEST_1).build();
            S3Object object = s3Client.getObject("BucketName", fileName);
            long size = object.getObjectMetadata().getContentLength();
            System.out.println("File Size is : " + size);
        }catch (Exception ex){
            throw new RuntimeException("Error while fetching attachment from storage", ex);
        }
	}

}
