package com.zoom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.zoom.util.Message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_ViewMessages extends Activity{
	private static final String TAG = "QueueActivity";		
	private ProgressDialog getPaymentsProgressDialog;	
	TextView heading;	
	ListView paymentList;	
	Button done_button;
	
	static ArrayList<Message> payments;		
	static Activity activity;
		
	String _userName;	
	String _passWord;
	
	//private GetPaymentsAsync mTask;
	
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_queue);
				
		setUpUI();
		
		if(payments == null) {
			payments = new ArrayList<Message>();
		}
		else {
			setupList();
		}
				
		/*
		Object retained = getLastNonConfigurationInstance();
		if ( retained instanceof GetPaymentsAsync) {		
			Log.i(TAG, "Reclaiming previous background task.");
			mTask = (GetPaymentsAsync) retained; 
            mTask.setActivity(this);   
            if(mTask.getStatus() == AsyncTask.Status.RUNNING)
            {
            	onTaskStarted();
            }            
		}
		else { 
            Log.i(TAG, "Creating new background task."); 
            //mTask = new GetPaymentsAsync(this);
            //mTask.execute("");	
		}
		*/
		
		
		activity = this;
	}
	
	
	/**
	 * After a screen orientation change, this method is invoked.
	 * As we're going to state save the task, we can no longer associate 
	 * it with the Activity that is going to be destroyed here.
	 */
	/*
    @Override 
    public Object onRetainNonConfigurationInstance() { 
            mTask.setActivity(null); 
            return mTask; 
    }
    */
		
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private void setUpUI() {
		heading = (TextView)findViewById(R.id.heading);
		paymentList = (ListView)findViewById(R.id.listView1);
		done_button = (Button)findViewById(R.id.done);
					
		done_button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				finish();					  	
			}			
		});	
	}
				
	
	//
	// Custom ArrayAdapter for list view
	//
	class myCustomAdapter extends BaseAdapter {
		Button colorButton;
		TextView userName;

		/**
		 * returns the count of elements in the Array that is used to draw the
		 * text in rows
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// return the length of the data array, so that the List View knows how many rows it has to draw		
			return payments.size();			
		}

		/**
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String getItem(int position) {
			return null;
		}

		/**
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Returns the complete row that the System draws. It is called every
		 * time the System needs to draw a new row; You can control the
		 * appearance of each row inside this function.
		 * 
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @param convertView
		 *            The View object of the row that was last created. null if
		 *            its the first row
		 * @param parent
		 *            The ViewGroup object of the parent view
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			/*
			View row = convertView;
			final int index = position;			
			Payment currentPayment = payments.get(index);
						  
			if (row == null) {
				// Getting custom layout to the row
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.queue_row, parent, false);
			}
						
			// Get a reference to the row's text view; find with row.findViewById()
			userName = (TextView) row.findViewById(R.id.textview);
						
			// Use a monospace font so the data will be aligned properly
			userName.setTypeface(Typeface.MONOSPACE);
			
			// Format the payment details and display in the row's textview
			String paymentDetails = String.format(Locale.US, "%-5d %-20s %s", index+1, currentPayment.getPayToName(), currentPayment.getCreateOnStr());
			userName.setText(paymentDetails);
			
			row.setOnClickListener(new OnClickListener() {    
	        	public void onClick(View v) {   	        		
	        		// Launch activity to view payment details (send picture id)
	        		Intent intent = new Intent(getApplicationContext(), EditPayment_Activity.class);
	        		
	        		Payment payment = payments.get(index);
	        		
	        		intent.putExtra("IMAGE_ID", payment.getPictureId());	        		
	        		intent.putExtra("ACCT_NUM", payment.getPayToAccount());
	        		intent.putExtra("ZIPCODE", payment.getPayToZip());
	        		intent.putExtra("NAME", payment.getPayToName());
	        		intent.putExtra("ADDRESS", payment.getPayToAddress());
	        		intent.putExtra("CITY", payment.getPayToCity());
	        		intent.putExtra("STATE", payment.getPayToState());
	        			        		
	        		startActivity(intent);	        		        		
	        	}
	        });
			
			return row; // the row that ListView draws
			*/
			return null;
		}		
	}
	
	public void onTaskStarted()
	{
		getPaymentsProgressDialog = new ProgressDialog(this);
		getPaymentsProgressDialog.setCancelable(false);
		getPaymentsProgressDialog.setTitle("Getting Payments");
		getPaymentsProgressDialog.setMessage("Please wait...");
		getPaymentsProgressDialog.show();
	}
	
	@Override
    public void onPause()
    {
    	super.onPause();    	
		if(getPaymentsProgressDialog!=null)
		{
			if(getPaymentsProgressDialog.isShowing())
			{
				getPaymentsProgressDialog.dismiss();
			}
			getPaymentsProgressDialog = null;
		}
    }
	
	@Override 
	public void onStop()
	{
		super.onStop();
		if(getPaymentsProgressDialog!=null)
		{
			if(getPaymentsProgressDialog.isShowing())
			{
				getPaymentsProgressDialog.dismiss();
			}
			getPaymentsProgressDialog = null;
		}
	}
	
	public void onTaskCompleted()
	{
		/*
		mTask.completed = true;
		if(getPaymentsProgressDialog != null)
		{
			if(getPaymentsProgressDialog.isShowing())
			{
				getPaymentsProgressDialog.dismiss();
			}
			getPaymentsProgressDialog = null;
		}		
        payments = mTask.getPayments();
        
        setupList();
        */
	}
	
	
	//
	// Display heading for normal screen sizes
	//
	private void setupList() {	
		/*
		// Set the heading background color
		heading.setBackgroundColor(Color.LTGRAY);
		
		// Format the payment details and display in the row's textview
		//String headingText = String.format(Locale.US, "%-5d %-20s %s", "#", "Pay To", "Created On");
		String headingText = String.format(Locale.US, "%-6s %-21s %s", "#", "Pay To", "Created On");
		heading.setText(headingText);
		
		// Display the payment count
		TextView countTextView = (TextView) findViewById(R.id.countTextView);
		countTextView.setText("" + payments.size());
		
		// Initialize array adapter for list view
		paymentList.setAdapter(new myCustomAdapter());
		*/
	}
	
	
	
	
	/*
	private class GetPaymentsAsync extends AsyncTask<String, Void, ArrayList<Payment>>
	{		
		
		private static final String TAG = "GetPaymentsAsync";
		
		public Activity_ViewMessages activity;
				
		private Client _clientInfo;
		
		private ArrayList<Payment> _payments;
		
		public boolean completed;
		
		public GetPaymentsAsync(Activity_ViewMessages activity)	{
			this.activity = activity;
		}
		
		@Override
	    protected void onPreExecute(){
	       activity.onTaskStarted();
	    }
		
		private void setActivity(Activity_ViewMessages activity) {			
            this.activity = activity;           
            if(this.getStatus() == AsyncTask.Status.FINISHED && !completed)
            {
            	notifyActivityTaskCompleted();
            }            
		}
		
		@Override
		protected ArrayList<Payment> doInBackground(String... credentials) 
		{		
			ArrayList<Payment> payments = new ArrayList<Payment>();
			
			try
			{
				SharedPreferences mPrefs = getSharedPreferences(Main_Activity.PREFS_NAME,MODE_PRIVATE);
				
				String username = mPrefs.getString(Main_Activity.PREF_USERNAME, null);
				String domain = mPrefs.getString(Main_Activity.PREF_DOMAIN, null);
					        
				
				_clientInfo = new Client(username, domain);								
								
				HttpClient client = new DefaultHttpClient();
									
				List<NameValuePair> params = new LinkedList<NameValuePair>();
				
				String searchType = "Unreviewed";
				params.add(new BasicNameValuePair("searchType", searchType));				
				
				String paramString = URLEncodedUtils.format(params, "utf-8");
				
				String url = _clientInfo.getBaseURI() + "/payments/pictures?" + paramString;

				HttpGet request = new HttpGet(url);
							
				request.addHeader("Authorization", _clientInfo.getAuth(url));
						
				request.addHeader("Accept", "application/json");
				
				HttpResponse response = client.execute(request);				
				String jsonString = EntityUtils.toString(response.getEntity());				
				PaymentParser parser= new PaymentParser(jsonString);				
				payments = parser.getPayments();				
			}
			catch(Exception ex)
			{
				Log.e(TAG, ex.toString());	
			}		
				return payments;
		}
		
		public ArrayList<Payment> getPayments()
		{
			return _payments;
		}

		@Override
		protected void onPostExecute(ArrayList<Payment> result) {
	        _payments = result;
	        notifyActivityTaskCompleted();
	    }		
	    		
			
		
		// Helper method to notify the activity that this task was completed.         
        private void notifyActivityTaskCompleted() { 
                if ( null != activity ) { 
                	//activity.onTaskCompleted(); 
                } 
        }
	}
	*/
}

