package Evan.EngageMaven;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RequestManager extends JFrame{
	
	//GUI stuff
	public static final int WIDTH = 800;
	
	//request manager stuff
	private HashMap<String, JSONObject> approvedRequests;
	private HashMap<String, JSONObject> completeRequests;
	
	public RequestManager() {
				
		approvedRequests = new HashMap<String, JSONObject>();
		completeRequests = new HashMap<String, JSONObject>();
		
		loadSavedRequests();
		
		//initially populate maps
		checkRequests();
		
		setSize(800,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel noReceiptPanel = new JPanel();
		JPanel completePanel = new JPanel();
		
		completePanel.setBorder(new TitledBorder(new EtchedBorder(), "Completed Purchases"));
		completePanel.setLayout(new BoxLayout(completePanel,BoxLayout.Y_AXIS));
		completePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		//populate no-receipt panel with all of the requests for now.
		//noReceiptPanel.setLayout(new BoxLayout(noReceiptPanel,BoxLayout.X_AXIS));
		noReceiptPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		noReceiptPanel.setBorder(new TitledBorder(new EtchedBorder(), "Purchase Requests"));
		
		//create sub panels (one to hold buttons and one to hold text fields)
		JPanel buttonPanel = new JPanel();
		JPanel textPanel = new JPanel();
		textPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.Y_AXIS));
		buttonPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setMaximumSize(new Dimension(100,10000));
		
		//add items from map into no-receipt panel
		Set<String> keys = approvedRequests.keySet();
		for(String key: keys) {
			JSONObject request = approvedRequests.get(key);
			String text = ""+request.get("accountName")+" - "+request.get("requestedAmount")+" - "+request.get("description");
			JTextField textField = new JTextField(text);
			textField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
			textField.setMaximumSize(new Dimension(10000,50));
			textField.setBorder(BorderFactory.createCompoundBorder(
			        textField.getBorder(), 
			        BorderFactory.createEmptyBorder(3, 5, 3, 5)));
			
			textField.setBackground(Color.PINK);
			
			textPanel.add(textField);			
			textPanel.revalidate();
			JButton button = new JButton("add receipt");
			button.setMaximumSize(new Dimension(100,50));
			buttonPanel.add(button);
			buttonPanel.revalidate();
			
		}
		
		noReceiptPanel.add(buttonPanel);
		noReceiptPanel.add(textPanel);
		
		//add items from map into complete panel
		keys = completeRequests.keySet();
		for(String key: keys) {
			JSONObject json = completeRequests.get(key);
			String text = ""+json.get("accountName")+" $"+json.get("requestedAmount");
			JTextField textField = new JTextField(text);
			textField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
			textField.setMinimumSize(new Dimension(800,30));
			textField.setMaximumSize(new Dimension(10000,30));
			completePanel.add(textField);
			completePanel.revalidate();
		}
		
		//set up scrolling
		JScrollPane noReceiptScroll = new JScrollPane(noReceiptPanel);
		JScrollPane completeScroll = new JScrollPane(completePanel);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("no receipt", noReceiptScroll);
		tabbedPane.addTab("complete",completeScroll);
		
		setContentPane(tabbedPane);
		
		setVisible(true);
		
		saveRequests();
	}
	
	//add a request to the hashmap if it has been approved
	public void addRequest(String id, JSONObject request) {
		if(request.get("status").equals("Approved")) {
			approvedRequests.put(id, request);
		}
	}
	
	//link a request to its cooresponding receipt
	public void addReceipt(String requestId, String receiptFile) {
		
	}
	
	public void checkRequests() {
		
		//create request
    	OkHttpClient client = new OkHttpClient();
    			Request request = new Request.Builder()
    			  .url("https://mines.campuslabs.com/engage/api/financerequests?key=2ac4aed93ad248248ffa49aa80f3a023")
    			  .method("GET", null)
    			  .addHeader("Accept", "application/json")
    			  .addHeader("key", "2ac4aed93ad248248ffa49aa80f3a023")
    			  .build();
    	    	
    			try {    		
    				//send request
					Response response = client.newCall(request).execute();
					
					//output response to file
			    		
						InputStream inStream = response.body().byteStream();
						
						//create output file
			    		File file = new File("response.json");
			    		FileOutputStream oStream = new FileOutputStream(file);
			    		
			    		//write bytes
			    		int read = 0;
			    		byte[] buffer = new byte[32768];
			    		while((read = inStream.read(buffer)) > 0) {
			    			
			    			oStream.write(buffer,0,read);
			    			
			    		}
			    		
			    		oStream.close();
			    		inStream.close();	
			    		
			    	//done writing response to file
					
					System.out.println("response: "+response.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
    			
    			//parse response file
    			
    			JSONParser parser = new JSONParser();
    	    	
    	    	//create JSONObject from JSON file
    	    	try {
    				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("C:/Users/eklein/eclipse-workspace/EngageMaven/response.json"));
    				    				    				
    				JSONArray items = (JSONArray) jsonObject.get("items");
    				
    				Iterator<JSONObject> iterator = items.iterator();
    				while(iterator.hasNext()) {
    				   JSONObject item = iterator.next();
    				   String id = String.valueOf(item.get("requestId"));
    				   
    				   //add this request to hashmap (approved request) (will only be added if approved and not already there)
    				   addRequest(id,item);
    				}
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
		
	}
	
	public void saveRequests() {
		
		try {
			File file = new File("approved_requests.json");
			PrintWriter writer = new PrintWriter(file);
			
			Set<String> keys = approvedRequests.keySet();
			for(String key: keys) {
				writer.println(approvedRequests.get(key));
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void loadSavedRequests() {
		
		try {
			File file = new File("approved_requests.json");
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				JSONParser parser = new JSONParser();
				
				JSONObject json = (JSONObject) parser.parse(line);
				String id = String.valueOf(json.get("requestId"));
				System.out.println(id);
				approvedRequests.put(id, json);
			}
		} catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		}
		
		try {
			File file = new File("complete_requests.json");
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				JSONParser parser = new JSONParser();
				
				JSONObject json = (JSONObject) parser.parse(line);
				String id = String.valueOf(json.get("requestId"));
				System.out.println(id);
				completeRequests.put(id, json);
			}
		} catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		}
		
	}
	
}
