package com.knorex.KnorexAssignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;


public class VastParser {
	 private static final String DB_URL = "jdbc:mysql://localhost:3306/vastdb";
	    private static final String DB_USER = "root";
	    private static final String DB_PASSWORD = "root";

	    public static void main(String[] args) {
	        try {
	            // Example usage
	            String xmlContentFromFile = readXmlFromFile("C:/path/to/file.xml");
	            String xmlContentFromUrl = readXmlFromUrl("https://raw.githubusercontent.com/InteractiveAdvertisingBureau/VAST_Samples/master/VAST 3.0 Samples/Inline_Companion_Tagtest.\r\n"
	            		+ "xml");

	            VastData parsedData = parseVastXml(xmlContentFromFile);
	            System.out.println("Parsed Data: " + parsedData);

	            saveToDatabase(parsedData);
	            VastData queriedData = queryFromDatabase(parsedData.getId());
	            System.out.println("Queried Data: " + queriedData);

	            String jsonData = convertToJson(parsedData);
	            System.out.println("JSON Data: " + jsonData);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private static String readXmlFromFile(String filePath) throws Exception {
	        BufferedReader reader = new BufferedReader(new FileReader(filePath));
	        StringBuilder xmlContent = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            xmlContent.append(line);
	        }
	        reader.close();
	        return xmlContent.toString();
	    }

	    private static String readXmlFromUrl(String url) throws Exception {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setRequestMethod("GET");

	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuilder xmlContent = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            xmlContent.append(line);
	        }
	        reader.close();
	        return xmlContent.toString();
	    }

	    private static VastData parseVastXml(String xmlContent) throws Exception {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlContent)));

	        Element root = document.getDocumentElement();
	        String version = root.getAttribute("version");
	        String id = root.getElementsByTagName("Ad").item(0).getAttributes().getNamedItem("id").getNodeValue();
	        String title = root.getElementsByTagName("AdTitle").item(0).getTextContent();
	        String description = root.getElementsByTagName("Description").item(0).getTextContent();

	        NodeList impressionList = root.getElementsByTagName("Impression");
	        Element impression = (Element) impressionList.item(0);
	        String impressionId = impression.getAttribute("id");
	        String impressionUrl = impression.getTextContent();

	        // More parsing logic goes here...

	        return new VastData(version, id, title, description, impressionId, impressionUrl);
	    }

	    private static void saveToDatabase(VastData data) {
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            String sql = "INSERT INTO vast_data (version, id, title, description, impression_id, impression_url) VALUES (?, ?, ?, ?, ?, ?)";
	            PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, data.getVersion());
	            statement.setString(2, data.getId());
	            statement.setString(3, data.getTitle());
	            statement.setString(4, data.getDescription());
	            statement.setString(5, data.getImpressionId());
	            statement.setString(6, data.getImpressionUrl());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static VastData queryFromDatabase(String id) {
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            String sql = "SELECT * FROM vast_data WHERE id = ?";
	            PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, id);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                String version = resultSet.getString("version");
	                String title = resultSet.getString("title");
	                String description = resultSet.getString("description");
	                String impressionId = resultSet.getString("impression_id");
	                String impressionUrl = resultSet.getString("impression_url");
	                return new VastData(version, id, title, description, impressionId, impressionUrl);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    private static String convertToJson(VastData data) throws Exception {
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper.writeValueAsString(data);
	    }
}
