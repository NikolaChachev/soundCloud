package finalproject.soundcloud.util;

//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import com.amazonaws.services.s3.model.PutObjectRequest;

@Component
@NoArgsConstructor
public class AmazonClient {
    public AmazonS3 amazonS3;

    @Value("${endpointUrl}")
    public String endpointUrl;
    @Value("${bucketName}")
    public String bucketName;
    @Value("${accessKeyId}")
    public String accessKey;
    @Value("${accessKeyCode}")
    public String secretKey;
    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.amazonS3 = new AmazonS3Client(credentials);
    }
     public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
     public void uploadFileTos3bucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String uploadFile(File file,String fileName){

        String fileUrl = "";
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            try {
                uploadFileTos3bucket(fileName, file);
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            file.delete();
        return fileUrl;
    }
    public boolean deleteFileFromS3Bucket(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName + "/", fileName));
        return true;
    }
}
