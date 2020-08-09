package com.seit_test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AutomateGitHub {
	
	public static String baseUrl = "https://api.github.com/";
	public String username;
	public String token;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void createRepo(String username , String token, String nameOfRepo) {

		String createUrl = baseUrl.concat("user/repos");
		
		try {
			StringEntity params =new StringEntity("{\"name\":\""+nameOfRepo+"\"}");
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(createUrl);
			request.addHeader("content-type", "application/json; utf-8");
			request.addHeader("Accept","application/vnd.github.nebula-preview+json");
			request.addHeader("Authorization", "token "+token);
			request.setEntity(params);
			
			HttpResponse response = httpClient.execute(request);
			
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject jobj = jelement.getAsJsonObject();
			String fullName = jobj.get("full_name").getAsString();
			if(fullName != null) {
				System.out.println("Successfully created!");
				System.out.println(fullName);
			}
			
			
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());			
		}
	}
	
	public ArrayList<String> checkRepo() {
		ArrayList<String> checkList = new ArrayList<String>();
		String checkUrl = baseUrl.concat("users/"+getUsername()+"/repos");
		
		try {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(checkUrl);
			request.addHeader("content-type", "application/json; utf-8");
			request.addHeader("Accept","application/vnd.github.nebula-preview+json");
			
			HttpResponse response = httpClient.execute(request);
			
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			JsonElement jelement = new JsonParser().parse(json);
			JsonArray jarr = jelement.getAsJsonArray();
			for (int i = 0; i < jarr.size(); i++) {
				JsonObject jo = (JsonObject) jarr.get(i);
				String nameRepo = jo.get("name").toString();
				nameRepo = nameRepo.substring(1, nameRepo.length()-1);				
				checkList.add(nameRepo);
			}
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());			
		}
		return checkList;
	}
	
	public boolean validateNewRepo(String nameOfRepo) {
		ArrayList<String> listNameRepo = checkRepo();
		if(listNameRepo.contains(nameOfRepo)) {
			return false;
		} else {
			return true;
		}
	}
	

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		AutomateGitHub automateGitHub = new AutomateGitHub();
		
		System.out.println("Input your GitHub username");
		String inputName = in.nextLine();
		System.out.println("Input your OAuth Token");
		String inputToken = in.nextLine();
		
		automateGitHub.setUsername(inputName);
		automateGitHub.setToken(inputToken);
		
		String username = automateGitHub.getUsername();
		String token = automateGitHub.getToken();
		
		while(true) {
			System.out.println("What would you like to do? (Ex: 1)");
			System.out.println("1.Create a Repository \n2.Check Repository \n3.Exit");
			String process = in.nextLine();
			
			if(process.equalsIgnoreCase("1")) {
				System.out.println("Input a new name for repository");
				String nameOfRepo = in.nextLine();
				if(automateGitHub.validateNewRepo(nameOfRepo) == false) {
					System.out.println("Repository is existed!");
					continue;
				} else {
					System.out.println("Sending request to Github");
					automateGitHub.createRepo(username,token,nameOfRepo);
				}
			} else if (process.equalsIgnoreCase("2")) {
				System.out.println("Sending request to Github");
				for(int i = 0; i < automateGitHub.checkRepo().size(); i++) {
					System.out.println((i+1)+". "+automateGitHub.checkRepo().get(i));
				}
			} else if (process.equalsIgnoreCase("3")) {
				in.close();
				break;
			}
		}
		
			
	}
	
	
}
