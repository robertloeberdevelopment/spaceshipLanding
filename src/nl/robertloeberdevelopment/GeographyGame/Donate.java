package nl.robertloeberdevelopment.GeographyGame;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


public class Donate extends Activity {
	
	 WebView myWebView; 
	 String key;
	 String idWebpage;// naar test artikel. Dit is nummer van pagina in speciale wordpresssite die info over het project bevat. id moet opgeslagen worden in de strings, achter de lat en lng van target
	 ProgressBar progressBar;
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.donate);
	        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// naar landscape draaien

	        //id van webpagina uit extra halen
	         Intent intent = getIntent();
	         idWebpage=intent.getStringExtra("idWebpage");
	        
	        
	        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	        
	        
	        if (!checkInternetConnection(this)) {
                //Toast.makeText(getApplicationContext(), "You have no internet connection", Toast.LENGTH_LONG).show();
                setContentView(R.layout.no_internet);// layout vervangen door geen connectie layout
	        
	        	} 
	        
	        else {
	        	maakWebview();
	        	}
	        
	        
	        
	        
	        
	        
	        
	        
	       
	        //Toast.makeText(getBaseContext(), "testing voor javascript trigger", Toast.LENGTH_LONG).show();  
	        
	        
	        
	     /* idee: In WebView een html pagina draaien waar de de donatie kan doen, bv via Mollie en Ideal, enz. Daarna de antwoord data teruggeven aan de app.
	      * Dat kan door een javascript functie te maken die de data naar Android stuurt
	      * http://stackoverflow.com/questions/22027161/android-passing-json-object-from-webview-javascript-to-java
	
	
			
			@njzk2 is right, do it like this:
			
			In JAVA:
			
			@JavascriptInterface
			public String test(String data) {
			   Log.d("TEST", "data = " + data);
			   return "this is just a test";
			}
			
			In JS:
			
			// some code 
			var result = test("{ a: 1, b: 2 }");
			alert(result);
			//some code
			
			function test(args) {
			   if (typeof Android != "undefined"){ // check the bridge 
			      if (Android.test!= "undefined") { // check the method
			         Android.test(args);  //hier dus de Android method uitvoeren en data doorsturen
			      }
			   }
			}

 
	      *    
	//Zo kan het ook: http://stackoverflow.com/questions/10114993/how-to-pass-json-formatted-data-from-a-webview-to-a-html-page


	      */
	        
	        
   
	        
	 }
	 
	 
	 
	 void maakWebview(){

		 //long timestampMili= System.currentTimeMillis();
  		 key="07011963002";// eigen api key
		 
          WebView myWebView = (WebView) findViewById(R.id.webview);
          
          //myWebView.setWebChromeClient(new WebChromeClient());// toegevoegd nodig voor beter javascript?
          
          myWebView.setWebViewClient(new myWebClient());// object van onderstaande class, die weer extensie is. Dit om de spinner te laten verdwijnen als pag is geladen
         
         
          final JavascriptUitvoeren myJavaScriptInterface= new JavascriptUitvoeren(this);
           
          WebSettings webSettings = myWebView.getSettings();
          webSettings.setJavaScriptEnabled(true); //dit werkt
          
          myWebView.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");//object van een class meegeven en de naam voor uitvoering in javascript
          
          myWebView.loadUrl("http://www.robertloeberdevelopment.nl/charigame/donate?infoId="+idWebpage+"&key="+key);//  
		
          
	 	}//maakWebview
	  
	 
	 
	 void toMainPage(){
		 Intent intent = new Intent(getApplicationContext(),MainActivity.class);
    	 startActivity(intent);
    	 
    	 Toast.makeText(getBaseContext(), "testing", Toast.LENGTH_SHORT).show(); 
    	 
     }
	 
	 
	 
	 
	 
	//de webviewclient class die gebruikt wordt met uitbreiding van methods om progress bar te kunnen laten zien, het draaiende spinnertje zolang de pagina laadt.
	 public class myWebClient extends WebViewClient
	    {
	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	            // TODO Auto-generated method stub
	            super.onPageStarted(view, url, favicon);
	        }
	 
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	 
	            view.loadUrl(url);
	            return true;
	 
	        }
	 
	        @Override
	        public void onPageFinished(WebView view, String url) {
	            // TODO Auto-generated method stub
	            super.onPageFinished(view, url);
	 
	            progressBar.setVisibility(View.GONE);
	        }
	    }
	 
	 
	 
	 
	 public boolean checkInternetConnection(Context context) {

         ConnectivityManager con_manager = (ConnectivityManager) context
                 .getSystemService(Context.CONNECTIVITY_SERVICE);

         if (con_manager.getActiveNetworkInfo() != null
                 && con_manager.getActiveNetworkInfo().isAvailable()
                 && con_manager.getActiveNetworkInfo().isConnected()) {
             return true;
         } else {
             return false;
         }
     }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	public  class JavascriptUitvoeren {
		                 Context mContext;
						 
		                 JavascriptUitvoeren(Context c){
							   mContext = c; 
						 }
						 
		                 
						 // vanaf api 17 moet dit erboven staan, voor veiligheidsredenen. Anders is de method niet visible!
						 @JavascriptInterface 
						 public void toMainPage(){
							 Intent intent = new Intent(mContext,MainActivity.class);// hij moet de doorgegeven context krijgen!!! En de method moet ook public zijn!!
							 //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//nu zou hij de vorige activity weer boven moeten krijgen, inderdaad, maar nu dus geen certificaat!!
							 //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//nieuwe, en oude weg? Werkt niet, nieuwe act, maar oude zit er nog onder. Misschien dat hij de donatie act eruitgooit, maar dat staat al in manifest, noHistory voor deze act
							 intent.putExtra("viaDonatiescherm", "donatie");// extra meesturen, om mainactivity te laten weten dat ie vanuit donatiescherm wordt opgevraagd, om overlays wel of niet te laten zien
							 startActivity(intent);
 
			             }
						 
						//vanuit het laatste scherm als donatie klaar is het bedrag opslaan om rang te promoten 
						 @JavascriptInterface 
						public void getAmount (String amountDonated ){
							int amountInt = Integer.parseInt(amountDonated);// wordt als string doorgegeven, hier er een int van maken
							SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();      
							editor.putInt("amountDonated", amountInt );
							editor.commit();
							 //Toast.makeText(mContext, amountDonated , Toast.LENGTH_LONG).show(); 

						} 
						 
						 // bevestigen dat iemand het terugkeerscherm heeft eghaald, dus werkelijk heeft overgemaakt. Klopt dat? of kan je ook anders naar returnscherm? Uitzoeken
						 @JavascriptInterface 
						 public void confirmDonation(){
							SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();      
							editor.putInt("confirmDonation", 1 );
							editor.commit();
							//Toast.makeText(mContext, "donatie bevestigd" , Toast.LENGTH_LONG).show(); 
						}
						 

	 			}
	 
	
	
	// als terugknop wordt gedrukt, mainactivity maken. Want die staat op noHistory, om te voorkomen dat er twee overelkaar heen komen, en de eerste nog veel geheugen bezet houdt.
	
	@Override
	 public void onBackPressed() {
		 Intent intent = new Intent(this,MainActivity.class);
	        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 intent.putExtra("overlayVisible", "noOverlay");// als vanuit donatiescherm weer terug wil niet opnieuw de uitleg overlay
		 startActivity(intent);

	  }
	
	
	
	
	 @Override
	    protected void onPause() {
	        super.onPause();
	        //myWebView.removeAllViews(); geeft null pointer exception
	        //myWebView.destroy(); geeft ook null pointer exception, dus lijkt wel of de webview al verdwenen is als je uit de activity gaat.  
	        
	 }
	
	
	
	// lijkt erop dat de webview als object in geheugen blijft, en problemenen verorozaakt. Pogen bij verlaten pagina het object te releasen
	@Override
    protected void onDestroy() {
        super.onDestroy();
        //myWebView.removeAllViews();
        //myWebView.destroy();
       
    }
	 
	 
	 
	 
}
