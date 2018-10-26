package consulting.bbgs.dataintgr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
 
public class HttpPostReq
{
    public static void main(String args[]) throws UnsupportedEncodingException, FileNotFoundException
    {
    	/** SIS DATA Integration Parameters
    	 * If you have question about those Parameters, contact your SIS Data Manager/Supporter
    	 * For more information, contact Blackboard Consultant
    	 * **/
    	//Define the Operation URL for importing data to LEARN
        String operationURL="https://bsk.blackboard.com/webapps/bb-data-integration-flatfile-BBLEARN/endpoint/person/store";
        //Define which SIS object will be used to send feed data
        //See Administrator Panel>Data Integration>Student Information System Integrations
        /** End of SIS DATA Integration Parameters**/
        
        String username="put your username of data integration object";
        String password="put your password of data integtation object"; 
        
        try {
        	/*
        	 * Data Extract from your Database 
        	*/
        	String feedString = "\uFEFF"+ //Adding UTF-8 bom (Byte Order Mark) 
        	"external_person_key,system_role,user_id,passwd,pwencryptiontype,Title,firstname,middlename,lastname,h_phone_1,h_fax,m_phone,email,birthdate,street_1,street_2,city,state,zip_code,country,company,department,job_title,b_phone_1,b_fax,student_id,institution_role,row_status,gender,h_phone_2,b_phone_2,webpage,educ_level,available_ind,Locale,othername,Suffix,data_source_key"
    		+"\r\n"+"std1,,std1,1234,,님,학생1,.,김,1234-5678,111-1111,010-1234-5678,std1@blackboard.edu,1977-03-01,주소1,주소2,Seoul,,12345,KR,공과대학,전자공학전공,재학,02-110-2323,111-2222,1101711,Student,Enabled,Female,1234-5678(h),02-110-2303(S),http://std1.blackboard.edu,freshman,Y,en_US,blue1,씨,Data_Source_Key-DEMO-USER";

	        HttpPostReq httpPostReq=new HttpPostReq();
	        HttpPost httpPost=httpPostReq.createConnectivity(operationURL , username, password, feedString);
	        httpPostReq.executeReq(feedString, httpPost);  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Add your catch block
			e.printStackTrace();
		}
    }
     
    HttpPost createConnectivity(String restUrl, String username, String password, String feedString)
    {
        HttpPost post = new HttpPost(restUrl);
        String auth=new StringBuffer(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader("AUTHORIZATION", authHeader);
        post.setHeader("Content-Type", "text/html; charset=UTF-8");
        post.setHeader("Accept-Encoding", "UTF-8");

        return post;
    }
     
    void executeReq(String feedData, HttpPost httpPost)
    {
        try{
            executeHttpRequest(feedData, httpPost);
        }
        catch (UnsupportedEncodingException e){
            System.out.println("error while encoding api url : "+e);
        }
        catch (IOException e){
            System.out.println("ioException occured while sending http request : "+e);
        }
        catch(Exception e){
            System.out.println("exception occured while sending http request : "+e);
        }
        finally{
            httpPost.releaseConnection();
        }
    }
     
    void executeHttpRequest(String feedString,  HttpPost httpPost)  throws UnsupportedEncodingException, IOException
    {
        HttpResponse response=null;
        String line = "";
        StringBuffer result = new StringBuffer();
        httpPost.setEntity(new StringEntity(feedString,StandardCharsets.UTF_8));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        System.out.println("Post parameters : " + feedString );
        System.out.println("Response Code : " +response.getStatusLine().getStatusCode());
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((line = reader.readLine()) != null){ result.append(line); }
        	System.out.println(result.toString());
    }
}