package Evan.EngageMaven;

import com.squareup.okhttp.MediaType;

public class App 
{
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private static RequestManager requestManager;

	public static void main( String[] args )
    {
		requestManager = new RequestManager();
		
    	
    			
    	
    }
    
}
