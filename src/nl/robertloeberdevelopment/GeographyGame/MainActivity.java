package nl.robertloeberdevelopment.GeographyGame;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;//dit werkt eindelijk. Je moet de library importeren: http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pollfish.constants.Position;
import com.pollfish.main.PollFish;

public class MainActivity extends Activity {

	  private GoogleMap mMap;
	  UiSettings mUiSettings;
	  double lat = 0; //pos is camera gaat omhoog noordwaarts (-90 t/m +90)
	  double lng =0;// pos is camera naar rechts oostwaarts (-180 t/m +180)
	  float zoom;
	  float zoomPrerun=9.0f;//vanaf dit zoomlevel prerun zooms tiles laden
	  int width;
	  int height;
	  float steeringSpeedFactor=1.0f;
	  float descendingSpeedFactor=1.0f;
	  int amountDonated;
	  int confirmDonation;
	  
	  
      boolean started=false;
      boolean newGame=false;
	  boolean linksIngedrukt=false;
	  boolean rechtsIngedrukt=false;
	  boolean omhoogIngedrukt=false;
	  boolean omlaagIngedrukt=false;
	  boolean preRunFinished=false;
	  boolean newLocation=true;
	  boolean boosterExhausted=false;
	  boolean steeringRocketEngaged=false;
	  boolean toggleLandingToevoegen=true;
	  boolean toggleMarker1=false;
	  boolean toggleMarker2=false;
	  boolean toggleMarker3=false;
	  boolean toggleScorescreen=false;
	  
	  boolean emergencyBoosterIngedrukt=false;
	  double emergencyBoosterTime=10.0;
	  int altitude=680000;
	  int fadeoutCounter=0;
	  float volume;
	  
	  double targetLat=0;
	  double targetLng=0;
	  String targetName1;
	  String targetName2;
	  String targetName3;
	  String targetName4;
	  String rank;
	  String rankOud;
	  String idWebpage;
	  
	  
	final Handler handler = new Handler();
	Runnable r;

    Button startButton;
    TextView gegevensLinksboven;
    TextView targetLocation;
    TextView alarmMessages;
    TextView score;
    TextView totalScore;
    TextView preCacheScherm;
    TextView naamOnder;
    
    ImageView countDownView;
    ImageView oorkonde;
    ImageView sterren;
    RelativeLayout scoreOverlay;
    LinearLayout uitlegOverlay;
    Random generator = new Random();
    double fuelSteeringRocket=100;
    int counterMessageSR=0;
    DecimalFormat afgerondEenDecimaal = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));
    // locale.US is essentieel. In Android 4.4 maakt hij er anders een komma van, en gaat ie down! want: java.lang.numberformatexception invalid double als hij die 
   
    Marker marker1;
    Marker marker2;
    Marker marker3;
    
    int preRunCounter=10;
    int realRunCounter=0;
    int countDown;
    int gameNumberDone;
    int aantalLandingen=0;
    int totaalscoreInt;

    MediaPlayer mediaPlayer; 
    MediaPlayer stuurraket;
    MediaPlayer booster;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
      
	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// naar landscape draaien
	   setContentView(R.layout.activity_main);
	        
	   if (!checkInternetConnection(this)) {
            //Toast.makeText(getApplicationContext(), "You have no internet connection", Toast.LENGTH_LONG).show();
        	setContentView(R.layout.no_internet);// layout vervangen door geen connectie layout
        	} 
        
	  else {
        	
		  
		  
		  
        /* 
        
        
If you are designing a map you plan on overlaying over google maps or virtual earth and creating a tiling scheme then i think what you are looking for are the scales for each zoom level, use these:

20 : 1128.497220
19 : 2256.994440
18 : 4513.988880
17 : 9027.977761
16 : 18055.955520
15 : 36111.911040
14 : 72223.822090
13 : 144447.644200
12 : 288895.288400
11 : 577790.576700
10 : 1155581.153000
9  : 2311162.307000
8  : 4622324.614000
7  : 9244649.227000
6  : 18489298.450000
5  : 36978596.910000
4  : 73957193.820000
3  : 147914387.600000
2  : 295828775.300000
1  : 591657550.500000


http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Resolution_and_Scale
http://www.slideshare.net/lodeblomme/google-maps-projection-and-how-to-use-it-for-clustering-presentation
        
       */
    	
		//width = getResources().getDisplayMetrics().widthPixels;
		//height = getResources().getDisplayMetrics().heightPixels;    
		
		//if (width<1024){zoom=2.0f;}
		//else{zoom=3.0f;}
		//int density = getResources().getDisplayMetrics().densityDpi;
		
		//int screenWidth=width*160/density;//
		//int screenHeight=height*160/density;
        
        //schermgrootte bepaalt zoom level. Kleine schermen gaan vanaf 2, grote vanaf 3
		//als het scherm groter is dan 1024 px, dan standard op zoom 3.0f zetten, kleiner, dan op 2.0 zetten. testen op S4 en tablet
		
		
        //Map maken en opzetten
        initializeMap();
        setUpMap(); //alles op beginstand zetten
        
        PollFish.init(this, "b8bb97cf-fd89-4293-a4a1-dbe406aeec78", Position.MIDDLE_LEFT,0);
 	    PollFish.hide();
        
        
        
        //BUTTONS

		final ImageButton buttonLinks= (ImageButton) findViewById(R.id.links);		 
		buttonLinks.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				 {
			      linksIngedrukt=true;
			      steeringRocketSound(true);
				 }
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				 {
			      linksIngedrukt=false;
			      steeringRocketSound(false);
				 }
				
		    return false;
			}
		});
		 
			
              
		final ImageButton buttonRechts= (ImageButton) findViewById(R.id.rechts);		 
		buttonRechts.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				 {
			      rechtsIngedrukt=true;
			      steeringRocketSound(true);  
				 }
				if (event.getAction() == MotionEvent.ACTION_UP)
				 {
			      rechtsIngedrukt=false;
			      
			      steeringRocketSound(false);
				 }
			
			return false;
			}
		});
		 
			      
              
		final ImageButton buttonOmhoog= (ImageButton) findViewById(R.id.omhoog);		 
		buttonOmhoog.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				 {
			     omhoogIngedrukt=true;
			     steeringRocketSound(true);
				 }
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				 {
			     omhoogIngedrukt=false;
			     steeringRocketSound(false);
				 }
				
		    return false;
			}
		}); 
		
		
		final ImageButton buttonOmlaag= (ImageButton) findViewById(R.id.omlaag);		 
		buttonOmlaag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				 {
			     omlaagIngedrukt=true;
			     steeringRocketSound(true);
				 }
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				 {
			     omlaagIngedrukt=false;
			     steeringRocketSound(false);
				 }
				
		    return false;
			}
		}); 
		
		
		
		//emergency booster, om snel weer op te stijgen. Als tijd is opgebruikt, exhausted==true, werkt niet meer
		final Button emergencyBooster= (Button) findViewById(R.id.emergencybooster);		 
		emergencyBooster.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				 {
			     emergencyBoosterIngedrukt=true;
			     
						     if (emergencyBoosterTime>0 && started==true){
							     booster = MediaPlayer.create(getApplicationContext(), R.raw.booster);
							     booster.start();
						     }
						     
				 }
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				 {
						emergencyBoosterIngedrukt=false;
						
						if (boosterExhausted==false && started==true){// als boosterAxhausted op true staat, is hij sowiezo gestopt en released, zie in booster gedeelte
						booster.stop();
						booster.release();
						}
				 }
				
		    return false;
			}
		}); 
		

		//new game button wordt zichtbaar als spelletje is afgelopen
		final Button newGame= (Button) findViewById(R.id.newgame);		 
		newGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
		        //in recet zit ook weer nieuw map opzetten
		        recet();
				started=false;
				newLocation=true;
				startButton.setText("start");
				
			}   
		}); 
		
		
		

	//als op repeatknop is gedrukt de vorige locatie herhalen
			final Button tryAgain= (Button) findViewById(R.id.repeat);		 
			tryAgain.setOnClickListener(new OnClickListener() {

					@Override
				public void onClick(View arg0) {
				        //in recet zit ook weer nieuw map opzetten
				    recet();
					started=false;
					newLocation=false;
					startButton.setText("start");
						
			}   
		}); 
				
		
		
				
		 //doel laten zien, overlay blijft liggen
		    final Button showTarget= (Button) findViewById(R.id.showtarget);		 
			showTarget.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				showTarget();  
			}   
		}); 
						
				
				
				
				
		
		
		 //gegevens in linkerbovenhoek  
			
		 rankOud=pilotRank();//rang ophalen
		 
		 gegevensLinksboven= (TextView) findViewById(R.id.gegevenslinksboven); 
		 gegevensLinksboven.setText("Altitude: "+String.valueOf(altitude)  +"\nFuel S.R.: " + fuelSteeringRocket +"%" );
		 
		 targetLocation= (TextView) findViewById(R.id.targetlocation); 
		 targetLocation.setTypeface(null,Typeface.BOLD_ITALIC);
		 //targetLocation.setText("Landing Target:\nt.b.a. when misson has started");
		
		 alarmMessages= (TextView) findViewById(R.id.alarmmessages); 
		 alarmMessages.setText("");
		 
		 naamOnder=(TextView) findViewById(R.id.naamOnder); 
		 naamOnder.setText("pilot: " + rankOud);// methode pilotRank bepaalt naam, op basis van ja of nee donatie en welke hoeveelheid
		 
	
        //screen overlay: kiest tussen uitleg of oorkonde na donatie, als je voor het eerst scherm opent
		 showScreenOverlay(true);//visble is true
		// PollFish.init(this, "b8bb97cf-fd89-4293-a4a1-dbe406aeec78", Position.MIDDLE_LEFT,0); Geen pollfish direct in begin. Misschien later...
		 //PollFish.show();

		 
		//GAME LOOP--------------------------------------------------------------------------------------------------------- 
		 
		r = new Runnable() {
		            
			//in deze method staat de code die de runnable uitvoert en loopt
		        public void run() {
		  
				        //mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));    
				        
		           
				        
				   if(started){
		                
					   //eerst de prerun om kaarte te cachen
				         if(preRunFinished==false){
				        	hiddenPreRun();
				         }
				        	
				        
				         
				         
				        //na de prerun het werkelijke spel doen 
				        else{
				        	 
				        	final LatLng coordinate = new LatLng(lat, lng); 
				        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, zoom)); 
				        	
				        	
				        	
				        	
				        	
				        	
				        	//inzoomen zolang hij nog geen 22 is
				        	if( zoom<21.20 ){
				        		
				        		//dalen
				        		zoom=(float) (zoom+ (descendingSpeedFactor* 0.002* (10/ ((zoom +10)/zoom )    ) ) );// degressief toenemen, logritmisch dus, want het zoomen gaat anders steeds sneller, ook afhankelijk van factor afh van grootte scherm
				        	   //was +0.001
				        		
					        		if (realRunCounter>1){ 
						        		// de schermoverlay van het precache scherm onzichtbaar maken, maar pas als inzoomen al gestart is
						        		preCacheScherm.setVisibility(View.GONE);
						      		    countDownView.setVisibility(View.GONE);
						        		
						        		targetLocation.setText(targetName1 +", " +targetName2+", " + targetName3 +", " + targetName4 );
					        		}
				        		
					        		
					        	//als rode emergencybooster knop wordt gebruikt weer opstijgen, en mededeling op scherm
					        	if(emergencyBoosterIngedrukt==true && zoom>2.5 ){ //was 2, maar max zoom level van tabelt =3, dus compromis
					        		
					        		if(emergencyBoosterTime>0){
					        			
					        			zoom=(float) (zoom-  ( 0.025* (10/ ((zoom +10)/zoom ))));//opstijgen door dalen te overrulen, was + 0.005
						        		emergencyBoosterTime=emergencyBoosterTime-0.10;//was-0.05
						        		double emergencyBoosterTimeAfgerond =  Double.valueOf(afgerondEenDecimaal.format(emergencyBoosterTime));
						        		alarmMessages.setText("Emergency Booster deployed! \nSeconds left: "+emergencyBoosterTimeAfgerond);
					        			}
					        		else{
					        			
					        			alarmMessages.setText("Emergency Booster failure!\nFuel exhaustion");
					        			
					        		    if ( boosterExhausted==false ){ // && booster.isPlaying()==true zit in loop, en mag maar een keer opgeroepen worden, want daarna bestaat object booster niet meer
												 booster.stop();
												 booster.release();
												 boosterExhausted=true;
									    	 }
									     
					        		
					        		}	
					        	}// ingedrukt
					        	
					        	// als losgelaten
					        	if(emergencyBoosterIngedrukt==false ){
					        		   alarmMessages.setText(" ");
					        	}
					        	
					        
					        	
				        	//'hoogte' voor linksboven berekenen
				        	 
				        	float factor=(zoom-1f);	
				        		
				        	altitude=(int) (680000/ (factor*factor) ) -1660 ;// logaritmisch afnemend. Als zoom=1, delen door1, als zoom=2, delen door 2x2, als 3delen door 3x2
				        	
				        	if (altitude<50){altitude=50;}// de flying saucer is 50 meter hoog!!

				        	
				        	
				        	//essentieel dat afgreondEeenDecimaal een waarde met punt geeft en niet met komma. De s4 met android 4.4 ging down. Afronding moet dus op Amerikaans formaat staan
					       fuelSteeringRocket =  Double.valueOf(afgerondEenDecimaal.format(fuelSteeringRocket));//maat dus weer een double van een afgeronde string, met een plaats achter punt.
					   
					        
				        	
				        	
				        	gegevensLinksboven.setText("Altitude: "+String.valueOf(altitude)  +"\nFuel S.R.: " + fuelSteeringRocket +"%" );
				        	
				        	
				        	if(fuelSteeringRocket<40){
				        		   
				        		if(counterMessageSR % 70 <20){// om de 7 seconden alarm boodschap tonen
					        		   
				        			   alarmMessages.setText("Fuel steering rockets reduced to "+ fuelSteeringRocket +"%" ); 

				        		   }
				        		   counterMessageSR++;
				        	  
				        		  if (fuelSteeringRocket==0){alarmMessages.setText("Steering rockets out of fuel. Steering incapacitated!" );  }
				        	
				        	}	
				        	
				        	
				        	//Log.d("geography game",  " width opnieuw= " +width + " height= " +height + " zoom=" +zoom);//test
				        	//voor tablet (>1024) wat later de eerste marker zetten, maar voor klein scherm eerder, want het doel raakt sneller buiten beeld.
				        	float zoomFirstMarker;
				        	if(width>1024){zoomFirstMarker=14.5f;}
				        	else{zoomFirstMarker=13.6f;}
				        	
				        	// doel marker op kaart zetten
				        	if(zoom>zoomFirstMarker && toggleMarker1==false){
				        		
				        		
				        		if(width>1024 ){
				        		marker1 = mMap.addMarker(new MarkerOptions()
				                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_60px))
				                .anchor(0.5f, 0.5f)// Anchors 
				                .visible(true)
				                .position(new LatLng(targetLat, targetLng)));
				        		
				        		//Log.d("geography game",  "doet groter dan 1024");//test
				        		}
				        		
				        		if(width<=1024){ //voor kleine schermen
				        			marker1 = mMap.addMarker(new MarkerOptions()
					                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_45px))
					                .anchor(0.5f, 0.5f)// Anchors 
					                .visible(true)
					                .position(new LatLng(targetLat, targetLng)));
				        			//Log.d("geography game",  " doet <= 1024");//test
				        		}
				        		
				        	toggleMarker1=true;//om te voorkomen dat hij telkens opnieuw gemaakt wordt in de loop
				        	}	
				        	
				        	
				        	//dalen iets versnellen als het target in zicht is, anders wordt het saai
				        	if (zoom>14.8){
				        		zoom=(float) (zoom+ (descendingSpeedFactor* 0.0021* (10/ ((zoom +10)/zoom )    ) ) );// degressief toenemen, logritmisch dus, want het zoomen gaat anders steeds sneller, ook afhankelijk van factor afh van grootte scherm		
				        	}
				        	
				        	
				        	
				        	if(zoom>16.5 && toggleMarker2==false){
				        		
				        		marker1.setVisible(false);
				        		//so Marker marker=your_map.addMarker(new MarkerOptions()) add this marker object to list or map markerArraylist.add(marker); then easily you can extract marker from list by Marker marker=markerArraylist.get(index); and then call marker.remove();
				        		marker1.remove(); //vorige marker verwijderen en nieuwe maken, enige manier, anders komen ze bovenop elkaar
				        		//probleem: remove lijkt alleen te gebeuren als de kaart geupdated wordt, dus dat duurt even.
				        		
				        		//LIJKT OF JE DE ID VAN DE MARKER MOET VINDEN
				        		
				        		//To select a marker programmatically, you'll have to keep a list of all your markers, then get a handle on one and call showInfoWindow(), similar to this:

				        		//markerList is just a list keeping track of all the markers you've added
				        		//to the map so far, which means you'll have to add each marker to this
				        		//list as you put it on the map
				        		//Marker marker = this.markerList.get(someObjectYoureShowingAMarkerFor.getId());

				        		//if(marker != null)
				        		//{
				        		//    marker.showInfoWindow();
				        		//}
				        		
				        		if(width>1024){
				        		marker2 = mMap.addMarker(new MarkerOptions()
				                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_100px))
				                .anchor(0.5f, 0.5f)// Anchors 
				                .position(new LatLng(targetLat, targetLng)));
				        		}
				        		if(width<=1024){
				        		marker2 = mMap.addMarker(new MarkerOptions()
					             .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_100px))
					             .anchor(0.5f, 0.5f)// Anchors 
					             .position(new LatLng(targetLat, targetLng)));				        			
				        		}
				        		toggleMarker2=true;//om te voorkomen dat hij telkens opnieuw gemaakt wordt in de loop
				        	}	
				        	
				        	
				        	//if (zoom>17.0){
				        		//marker2.setVisible(false);
				        		//marker2.remove();
				        		//}
				        	
				        	if(zoom>20 && toggleMarker3==false){
				        			
				        		marker2.setVisible(false);
				        		marker2.remove(); 
				        		
				        		if(width>1024){
				        		marker3 = mMap.addMarker(new MarkerOptions()
				                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_200px))
				                .anchor(0.5f, 0.5f)// Anchors 
				                .position(new LatLng(targetLat, targetLng)));
				        		}
				        		
				        		if(width<=1024){
				        		marker3 = mMap.addMarker(new MarkerOptions()
					             .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_120px))
					             .anchor(0.5f, 0.5f)// Anchors 
					             .position(new LatLng(targetLat, targetLng)));	
				        		}
				        		toggleMarker3=true;//om te voorkomen dat hij telkens opnieuw gemaakt wordt in de loop	
				        	}	
				        		
				        	
				    
				            realRunCounter++;
				        	
				        }//if zoom<21.25
				       
				        	
				        	
				        	
				        	//als einde van zoom is bereik ben je geland, en is spel afgelopen
				     else{
				    	    if(toggleScorescreen==false){
				    	    	showScoreScreen();
				    	    	toggleScorescreen=true;
				    	    	//Log.d("SCORE array",  " percent nauwk= " +scoreArray[0] +"EB= " +scoreArray[1] + "Fuel ST= " +scoreArray[2] + "punten= " + scoreArray[3]);//test  
				    	    	}
				    	    
				    	    //geluid uitfaden met laatste loops
				    	    fadeSound();
				    	    
				        	}
				        	
				        	
				        }// if started   
					
				        
				   
				        
				        // de handler aanzetten met de vertraging, was 50 ms, maar dan kon de map zich niet snel genoeg updaten, en zonder cache laadden sommige tiles niet.
			        	  handler.postDelayed(this, 100);
				      
		            
			        
				        // nog een probleem: op tablet beweegt hij te langzaam, want meer pixels, dus moet afhankelijk worden van een factor.
				        
			            if (rechtsIngedrukt==true && fuelSteeringRocket>0){
			      
			            	
			            		
			            	if (zoom<13.8){
			            	lng=lng+ (40 *steeringSpeedFactor) /(zoom*zoom*zoom*zoom);//was +20
			            	}
			            	
			            	
			            	else{lng=lng+(100 *steeringSpeedFactor)/(zoom*zoom*zoom*zoom*zoom); }//was +50
			            	//stuurraket brandstof verminderen
			            	fuelSteeringRocket=fuelSteeringRocket-0.2;
			            }
			        

			            if (linksIngedrukt==true && fuelSteeringRocket>0){
			            	if (zoom<13.8){
				            lng=lng-(40*steeringSpeedFactor)/(zoom*zoom*zoom*zoom);
			            	}
			            	
			            	else{lng=lng-(100*steeringSpeedFactor)/(zoom*zoom*zoom*zoom*zoom); }
			            	
			            	fuelSteeringRocket=fuelSteeringRocket-0.2;
			            }
			            
			            

			            if (omhoogIngedrukt==true && fuelSteeringRocket>0){
			            	if (zoom<13.8){
			            	lat=lat+(30*steeringSpeedFactor)/(zoom*zoom*zoom*zoom);//was +15
			            	}
			            	
			            	else{ lat=lat+(60*steeringSpeedFactor)/(zoom*zoom*zoom*zoom*zoom);}//was +30
			            	
			            	fuelSteeringRocket=fuelSteeringRocket-0.2;
			            }
			            
			            
			            if (omlaagIngedrukt==true && fuelSteeringRocket>0){
			            	
			               	if (zoom<13.8){
				            lat=lat-(30*steeringSpeedFactor)/(zoom*zoom*zoom*zoom);
			               	}
			               	
			               	else{ lat=lat-(60*steeringSpeedFactor)/(zoom*zoom*zoom*zoom*zoom);}
			               	
			               	fuelSteeringRocket=fuelSteeringRocket-0.2;
			            }
			               	
				            
					            
			            //Log.d("geography game",  " zoom= " +zoom + "lat= " +lat + "lng= " +lng );//test   	
		            
			            
			            
			            
				       }//else on prerun finished is rue 
				       
				   }//run method
		
		};//runnable
		       
	// Einde GAME LOOP---------------------------------------------------------------------------------------------------------	        
		       
		        
		   
		
		
		
		   startButton= (Button) findViewById(R.id.start);		 
		   startButton.setOnClickListener(new OnClickListener() {

				  public void onClick(View v ) {
					
					//omte testen, straks weer weg! 
					//Intent intent = new Intent(getApplicationContext(),Donate.class);
      	    	    //startActivity(intent);	  
							  
      	    	    
      	    // weer inschakelen als donatie gedoe gelukt is om normale functie van knop weer te maken
				if(started==false ){				
					            showScreenOverlay(false);// visible van uitleg/oorkonde false
								 started=true;
								 recet();//alles recetten, zie functie en target locatie ophalen
								 String[] targetArray=targetLocation();//array vullen door een keer de method uit te voeren, als newLocation true, dan nieuwe locatie, als false herhalen vorige locatie
								 //targetLocation.setTypeface(null,Typeface.BOLD_ITALIC);
								 targetName1=targetArray[0];
								 targetName2=targetArray[1];
								 targetName3=targetArray[2];
								 targetName4=targetArray[3];
								 targetLat= Double.parseDouble( targetArray[4]);//target lng uit de array halen en als double in var zetten
								 targetLng= Double.parseDouble( targetArray[5]);
								 startButton.setText("stop");
							     r.run();// de loop starten 
							     //geopende pollfish verbergen
							   	 PollFish.hide();
							}
				
				
				else {	
						startButton.setText("start");
						lat=0;
						lng=0;
						altitude=680000;
						fuelSteeringRocket=100.0;
						gegevensLinksboven.setText("Altitude: "+String.valueOf(altitude)  +"\nFuel S.R.: " + fuelSteeringRocket +"%");
						//targetLocation.setTypeface(null,Typeface.BOLD_ITALIC);
						//targetLocation.setText("Landing Target:\n t.b.a. when misson has started");
						emergencyBoosterTime=10.0;
						alarmMessages.setText(""); 

						handler.removeCallbacks(r);//de runnable loop stoppen
						showScoreScreen();// scoreoverlay met keuzeknoppen zichtbaar maken: nieuw spel, herhalen, of toon locatie, geluid wordt in showScoreScreen gestopt
						started=false;
						mediaPlayer.stop();//na showscore screen, want daar zit volume regelaar, en dan moet mediaplayer nog bestaan
						mediaPlayer.release();
					}
				  
				  }
			    }); 
		        
    
		   
	  }//else internetConnection
      
    }//onCreate

    
    
    
   //METHODS --------------------------------------------------------------------------------------------------------------------------------------- 
   
    
   void pollfishHide(){ 
	
	 //geopende pollfish verbergen
	   	  //PollFish.hide();
   	} 
    
    
    
    
    //recet method, wordt vanuit 'start' getriggerd
   void recet(){
	   	
		  PollFish.hide();
	   	  mMap.clear();//alle markers ed verwijderen
	      //markerList.clear();
		  initializeMap();
	      setUpMap();
	      lat=0;
		  lng=0;
		
		  fuelSteeringRocket=100.0;
		  altitude=680000;
		  emergencyBoosterTime=10.0;
		  score = (TextView) findViewById(R.id.score); 
		  score.setText("");
		  totalScore = (TextView) findViewById(R.id.totalscore);
		  totalScore.setText("");
		  scoreOverlay=(RelativeLayout) findViewById(R.id.scoreoverlay);
		  scoreOverlay.setVisibility(View.GONE);//score text onzichtbaar maken
		  oorkonde.setVisibility(View.GONE); //oorkonde onzichtbaar maken
		  
		  gegevensLinksboven= (TextView) findViewById(R.id.gegevenslinksboven); 
		  gegevensLinksboven.setText("" );
		
	      alarmMessages= (TextView) findViewById(R.id.alarmmessages); 
		  alarmMessages.setText("");
  
		  preRunFinished=false;
		  toggleLandingToevoegen=true;
		  toggleMarker1=false;
		  toggleMarker2=false;
		  toggleMarker3=false;
		  toggleScorescreen=false;
		  
		 targetLocation.setText("");
		 countDown=9;
		 realRunCounter=0;
	
		 volume=1.0f;
		 boosterExhausted=false;
		
		 mMap.setOnMarkerClickListener(new OnMarkerClickListener() {// lege listener, omdat hij anders klikbaar blijft, tewrijl dat pas moet kunnen als de grote knop is verschenen
		        @Override
		        public boolean onMarkerClick(Marker marker) {
		         return true;
		        }
		    });

   	}//recet 
    
    
    
    
   //Map laden
    private void initializeMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
             if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.
                // Sets the map type to be "hybrid, kan ook terrain enz"
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);  
            }
            
 
          else{ 
            	Toast.makeText(getApplicationContext(),
                 "Sorry! unable to create Google Map. To play this game you need a live internet connection", Toast.LENGTH_LONG)
                 .show();
           }
      
        }//if
    }

    
    
    
    
   //Google map opzetten
    private void setUpMap() {
    	
        mMap.setMyLocationEnabled(false);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setAllGesturesEnabled (false);
        
        width = getResources().getDisplayMetrics().widthPixels;
	    height = getResources().getDisplayMetrics().heightPixels;
		//int density = getResources().getDisplayMetrics().densityDpi;
		
	    
			//int screenWidth=width*160/density;//
			//int screenHeight=height*160/density;
		

	     //schermgrootte bepaalt zoom level. Kleine schermen gaan vanaf 2, grote vanaf 3
		//als het scherm groter is dan 1024 px, dan standard op zoom 3.0f zetten, kleiner, dan op 2.0 zetten. testen op S4 en tablet
	   // ook de stuursnelheid moet vergroot worden voor tablets, want hij deelt door de zoom*zoom*zoom*zoom en die is meteen een niveau groter op tablet
	      if (width<1024){
	    	  zoom=2.0f;
	    	  steeringSpeedFactor=1.6f;// stuurde wat te langzaam op telefoon, opgehoogd
	    	  descendingSpeedFactor=1.1f;//moet iets sneller afdalen dan tablet, want zoom is factor groter
	    	  
	      }
	      else{
	    	  zoom=3.0f;
	    	  steeringSpeedFactor=3.0f;// compensatie voor hogere zoom
	    	  descendingSpeedFactor=1.0f;
	      }
	      
	     //Log.d("geography game",  " width= " +width + " height= " +height + " zoom=" +zoom);//test
 
        //op beginstand zetten, waarden positie en zoom ook invullen, want als je voor donatie naar andere activity bent gegaan zijn die kwijt
        lat=0;
        lng=0;
        //zoom=2.0f;		
    	final LatLng coordinate = new LatLng(lat, lng); 
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, zoom);
        mMap.moveCamera(yourLocation);

    }
             
     
    
  
 
    
  //het doel al een keer verborgen benaderen om te zorgen dat de tiles gecached worden. Wordt vanuit de runnable getriggerd 
  void hiddenPreRun(){
	   	
  		if(preRunCounter% 5==0){//was 10
  			
		  		zoomPrerun=zoomPrerun+0.5f;// was +2, nu elk niveau vanaf zoom 10
		  		//Log.d("geography game",  " targetLat= " +targetLat + " Targetlng= " +targetLng +"preruncounter= "+preRunCounter);//test 
		  		
		  		final LatLng coordinate2 = new LatLng(targetLat, targetLng); 
		  		
		  		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate2, zoomPrerun);
		  		mMap.moveCamera(yourLocation);
		  	
		  		 preCacheScherm= (TextView) findViewById(R.id.precachescherm); 
		  		 preCacheScherm.setVisibility(View.VISIBLE);
		  		 preCacheScherm.setText("Landing Target:\n"+ targetName1 +", " +targetName2 +", " + targetName3 +", " + targetName4 +"\n\nStarting landing sequence in: " );
		  		 
		  		 countDownView=(ImageView) findViewById(R.id.countdown); 
		  		 countDownView.setVisibility(View.VISIBLE);
		  		 
		  		 BitmapDrawable bmpDraw = (BitmapDrawable) countDownView.getDrawable();
		         Bitmap bmp = Bitmap.createBitmap(100, 100, Config.ARGB_8888);// 
		         Canvas c = new Canvas(bmp);
		         Paint p = new Paint();
		         p.setAntiAlias(true);
		         p.setColor( 0xff49FE02);
		         
				p.setStyle(Paint.Style.STROKE);
		  		c.drawCircle(50, 50, 40, p); //x,y, radius
		  	    //p.setColor( 0xff000000);
			    p.setTextSize(48);
			    
			    p.setStyle(Paint.Style.FILL);
		        c.drawText(String.valueOf(countDown), 36, 66, p); 
		        countDownView.setImageBitmap(bmp);//essentieel, anders zie je niks  
		        
		        if (preRunCounter% 10==0){
		  		   countDown--;
		  		   }
		        

  		}  

  	    preRunCounter++;
      	//elke zoveel ms een niveau inzoomen 
  	    
  		if(zoomPrerun>18){//was 19 als prerun klaar is dit doen:
	  		   final LatLng coordinate2 = new LatLng(lat, lng); 
	  		   CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate2, zoom);// naar de officiele zoom beginstand
	  		   mMap.moveCamera(yourLocation);
	  		   preRunFinished=true;
	  		   preRunCounter=10;
	  		   zoomPrerun=9.0f;
	  		   playSound();
	  		   //geluidAfspelen.execute(); // object met achtergrondgeluid starten
  		}
  }  
    
    
  
  
  
  // stuurraketgeluid aan en uit
  void steeringRocketSound(boolean onof){
	  if(steeringRocketEngaged==false && onof==true &&started==true){
		  stuurraket = MediaPlayer.create(getApplicationContext(), R.raw.stuurraket);
	      stuurraket.start(); 
		  steeringRocketEngaged=true;  
	  }
	
	 if(steeringRocketEngaged==true && onof==false &&started==true) {
		  stuurraket.stop();
	      stuurraket.release(); 
	      steeringRocketEngaged=false;  
	 }
	   
  };
  
  
   
   
   //Achtergrondgeluid van dalend ruimteschip
   void playSound() {
			  	
	            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.spaceship_scifi);
				mediaPlayer.start();
             } 
   
  

  
   //Doel locatie ophalen 
   String[] targetLocation(){
	   
	   SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE);
   	   gameNumberDone = sp.getInt("gameNumberDone", 0);
	   
	   //string array ophalen met bestemmingen en coordinaten
		Resources res = getResources();
		CharSequence[] array_plaatsen_coordinaten = res.getTextArray(R.array.country_place_place_place_lat_lng);
		
		// random getal voor keuze uit aantal beschikbare locaties met coordinaten		
	   //int i=generator.nextInt(array_plaatsen_coordinaten.length);
		
		//als alle landingsplaatsen geweest zijn weer opnieuw beginnen
		if (gameNumberDone+1 >= array_plaatsen_coordinaten.length) { 
			gameNumberDone=0;
			} //let op: hoogste plaats is een lager dan de lengte van de array!!!  als dat groter is dan lengte array, opnieuw beginnen, dus als hij gelijk is aan de lengte
		
		 //Log.d("GAME",  "newlocation= " + newLocation);//test  
		
		int newGameNumber=0;
		if(newLocation==true){newGameNumber=gameNumberDone+1;}//als er op start gedrukt is, volgende game. Als er op try again gedrukt is zelfde gamenumber
		else{newGameNumber=gameNumberDone; }
		
		String targetLocationTotaal=(String) array_plaatsen_coordinaten[newGameNumber];
		String[] targetLocation= new String[6];
		targetLocation=targetLocationTotaal.split(",");//string splitsen op komma en in de array zetten en returnen
		
		idWebpage=targetLocation[6]; //id van de webpagine die bij de locatie hoort uit de string halen en in de var zetten. Die wordt via extra weer naar donatie.java doorgegeven, zodat je meer info kan aanklikken
		
	    return targetLocation;  
   
   }
    
    
   

   
   //score scherm tonen als overlay
   void showScoreScreen(){
	 
	  
	   
	   
	  /* 
	  //nog even laten loop voor fade out, dan geluid stop
	   mediaPlayer.setVolume(volume, volume); 
	   
		   if(started==true && fadeoutCounter>20){
			   //geluidAfspelen.cancel(true);
			   //audioTrack.stop();// gestarte audio object stoppen en started op false zetten
			   mediaPlayer.stop();
			   mediaPlayer.release();
		   	   started=false;
		   	   fadeoutCounter=0;
		   	   volume=1.0f;
		   	   startButton.setText("start");
		   	   ///handler.removeCallbacks(r);//de runnable loop stoppen, lijkt hier niet te werken
		   }

	   volume=volume*0.85f;
	   fadeoutCounter++;
		*/
	   //Log.d("fade out",  "foedoutcounter= " + fadeoutCounter);//test  
	   
	   
	   // target is klikbaar als je geland bent. DIT UITSCHAKElEN VOOR NU, WAS BEDOELD OM NAAR DONATIESCHERM TE GAAN. KAN LATER evt GEBRUIKT WORDEN OM INFO OVER DOEL OP TE VRAGEN
		
	   /* 
	   mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

		        @Override
		        public boolean onMarkerClick(Marker marker) {
		        Intent intent = new Intent(getApplicationContext(),Donate.class);
		        intent.putExtra("idWebpage", idWebpage);// id van de webpagina van betreffende project meesturen met
	    	    startActivity(intent);	
		        return true;
		        }
		    });
	   */
	   
	   
	    float scoreArray[]=calculateScore();// score method uitvoeren   
		score = (TextView) findViewById(R.id.score);
		totalScore = (TextView) findViewById(R.id.totalscore);
		scoreOverlay=(RelativeLayout) findViewById(R.id.scoreoverlay);
		
		totaalscoreInt=(int)scoreArray[3];
		
		score.setText("Target accuracy: " + (int)scoreArray[0] + "%\nHorizontal distance from target: " +scoreArray[4] + " km\nVertical distance from target: "+scoreArray[5] +   " km\nEmergency Booster time left: "+scoreArray[1] +" sec.\nFuel steering rockets left: " + scoreArray[2] +"%");
		totalScore.setText("Score: " + totaalscoreInt);
		scoreOverlay.setVisibility(View.VISIBLE);
		
		 //pollfish komt omhoog 
		   //PollFish.init(this, "b8bb97cf-fd89-4293-a4a1-dbe406aeec78", Position.MIDDLE_LEFT,0);
		   final Handler handler2 = new Handler();
		    handler2.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		          //na 4 sec pas tonen, alleen als er een score is
		          if(totaalscoreInt>0){
		        	PollFish.show();
		          }
		        	
		        }
		    }, 4000);

		//als succesvolle landing was (groter dan ..procent , deze opslaan ivm bevordering hogere rang	
		if( (int)scoreArray[0]>95 && toggleLandingToevoegen==true){
			aantalLandingenOpslaan();
			toggleLandingToevoegen=false;//dit zodat hij gegarandeerd maar 1 keer getal kan ophogen. Blijkt nl dat deze funtie in de loop vaker getriggerd werd
		}
		
	
		
		
		
		//alleen als erop new game was gedrukt opslaan dat er een volgende game was gespeeld. Als repeat was gekozen het nummer niet ophogen
		if(newLocation==true){
			//in shared preferences dit spel als gespeeld opslaan, zodat hij volgende keer de volgende pakt
			SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();      
			editor.putInt("gameNumberDone", gameNumberDone+1 );
			editor.commit();
		}
	   
   }
   
   
   
   
  void fadeSound(){
	  
	//nog even laten loop voor fade out, dan geluid stop
	   mediaPlayer.setVolume(volume, volume); 
	   
		   if(started==true && fadeoutCounter>20){
			   //geluidAfspelen.cancel(true);
			   //audioTrack.stop();// gestarte audio object stoppen en started op false zetten
			   mediaPlayer.stop();
			   mediaPlayer.release();
		   	   started=false;
		   	   fadeoutCounter=0;
		   	   volume=1.0f;
		   	   startButton.setText("start");
		   	   ///handler.removeCallbacks(r);//de runnable loop stoppen, lijkt hier niet te werken
		   	   
		   }

	   volume=volume*0.85f;
	   fadeoutCounter++;
	  
  }
   
   
   
   
   
   
   float[] calculateScore(){
	   
	   //correlatie uitrekenen van doel met gehaalde prositie, 
	   double differenceLat=(targetLat-lat);
	   
	   //verschil in afstand lattitude, 1 graad is 111 km, 0.001 =111 meter, moet nog neg of positief zijn zodat je weet welke kan verschil op is
	   double kmDifferenceLat=Double.valueOf(afgerondEenDecimaal.format(differenceLat*111));   
	  
	   if (differenceLat < 0) {differenceLat = -differenceLat;}// verschil positief maken
	   
	   double differenceLng=targetLng-lng;
	   
	   double kmDifferenceLng= Double.valueOf(afgerondEenDecimaal.format(differenceLng*111));     
	    
	   if (differenceLng < 0) {differenceLng = -differenceLng;}// verschil positief maken
	   
	   double targetPrecision= differenceLat + differenceLng ;
	   
	   targetPrecision=targetPrecision*1000;// verschil vergroten zodat je bij plm 100 meter verschil ongeveer 2 a 3 procent van de 100 procent af zit
	   //het blijkt dat als je in Amsterdam west land, ivp Adam oost, het verschil ongeveer0.08932 is(lat+lng). maal 1000 maakt 89 Dit zou ongever 10 procent score moeten geven, dus tot 100
	   float[] scoreArray= new float[6];
	 
	   if(targetPrecision>100){targetPrecision=100;}
	   float targetPrecisionPercent=(float) ( 100- targetPrecision  );
	   
	   if (targetPrecisionPercent>99.8){targetPrecisionPercent=100;}//afronden naar 100, want maak er later een int van, waardoor hij anders nooit op 100 zou komen, ook al zit je er haast bovenop
	   
	   scoreArray[0]=targetPrecisionPercent;//target precision op plek 0 array zetten	   
	   scoreArray[1]=(float) emergencyBoosterTime;
	   scoreArray[2]=(float) fuelSteeringRocket;
	   scoreArray[4]=(float) kmDifferenceLng; // totaal score op 3
	   scoreArray[5]=(float) kmDifferenceLat;
	   
	   //Log.d("SCORE array",  " kmdiflng= " +scoreArray[3] +"kmdiflat= " +scoreArray[4] );//test  
	   
	   float totaalscore=0;
	   
	   //alleen totaalscore als er op z'n minst een klein beetje het doel is bereikt, anders score =0
	   if(targetPrecisionPercent>0){
		   totaalscore= (float) ( (1000+ targetPrecisionPercent*10 - ((10- emergencyBoosterTime)*80)) - ((100- fuelSteeringRocket)*4)) ;
	   }
	   
	   else{totaalscore=0; }
	   
	   scoreArray[3]=totaalscore;
	   
		//Log.d("geography game TARGET",  " targetPrecision= " +targetPrecision );//test  
		
	   return scoreArray;
   }
   
   
   
   //ophalen aantal geslaagde landingen, en eentje erbij optellen
   public void aantalLandingenOpslaan(){
		SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE); 
	    aantalLandingen = sp.getInt("aantalLandingen", 0);
	    SharedPreferences.Editor editor = sp.edit(); 
		editor.putInt("aantalLandingen", aantalLandingen+1 );
		editor.commit();
		//Log.d("geography game",  " aantalLandingen opgeslagen= " +aantalLandingen+1 );//test 
		//Toast.makeText(mContext, amountDonated , Toast.LENGTH_LONG).show(); 
		 naamOnder=(TextView) findViewById(R.id.naamOnder); 
		 naamOnder.setText("pilot: " +pilotRank());// methode pilotRank bepaalt naam
		
		 
		 
	} 
   
   
   
   String pilotRank() {
	
	   SharedPreferences sp = getSharedPreferences("landingSpaceship", Activity.MODE_PRIVATE);
   	   aantalLandingen = sp.getInt("aantalLandingen", 0);
 	   sterren = (ImageView) findViewById(R.id.sterren); 
 	   oorkonde = (ImageView) findViewById(R.id.oorkonde); 
 	   
 	   if (aantalLandingen==0 || Integer.toString(aantalLandingen) == null ){
 		   rank="cadet";//sterrenplaatje staat al standaard in layout
 	   }
 	   if (aantalLandingen>0 ){
 		   rank="lieutenant";
 		   sterren.setImageResource(R.drawable.sterren_tweede_officier_120px);
 		   //als er een bevordering plaasts vind, dus direct na landing een nieuwe rang gekregen, dan plakkaat tonen
			 if(aantalLandingen==1){
				 oorkonde.setVisibility(View.VISIBLE); 
				 oorkonde.setImageResource(R.drawable.promotion_lieutenant);
				 } 		   
 	   }
 	   
   	   if (aantalLandingen>3){
   		   rank="major";
   	       sterren.setImageResource(R.drawable.sterren_eerste_officier_120px);
	   	    
   	       if(aantalLandingen==4){ 
				 oorkonde.setVisibility(View.VISIBLE); 
				 oorkonde.setImageResource(R.drawable.promotion_major);
				 } 
   	            
   	   }
   	   
   	   if (aantalLandingen>7){
   		   rank="general";
   		   sterren.setImageResource(R.drawable.sterren_gezagvoerder_120px);
   		   
	   		 if(aantalLandingen==8){ 
				 oorkonde.setVisibility(View.VISIBLE); 
				 oorkonde.setImageResource(R.drawable.promotion_general);
				 } 
   		   	   
   	   }
   	      
   	   if (aantalLandingen>15){
   		   rank="Admiral of the Fleet";
   		   sterren.setImageResource(R.drawable.sterren_admiraal_120px);
   		   
		   	  if(aantalLandingen==16){ 
				  oorkonde.setVisibility(View.VISIBLE); 
				  oorkonde.setImageResource(R.drawable.promotion_admiral);
				  } 
   	   }
   	    	     	   
   	//Log.d("geography game",  " aantalLandingen= " +aantalLandingen );//test  
	   return rank;
   }
   
   
   
   
  
   //wordt door onCreate opgeroepen, dus telkens bij begin en terugkomen vanuit donatiescherm
   void showScreenOverlay(boolean visible){
	   
	    // Intent intent = getIntent();
	  	// String viaDonatiescherm=intent.getStringExtra("viaDonatiescherm");// haalt intent op die vanuit donatiescherm wordt gestuurd, in dit geval is waarde 1.
         
	  	 //als vanuit donatiescherm op backbutton wordt geklikt, wordt mainactivity weer opgeroepen, maar je wil dan niet opnieuw de spelregels zien
	  	 //String visibleFromDonate=intent.getStringExtra("overlayVisible");//als de string gevuld is en niet null, dan kom je via donatiescherm en dus geen uitleg
         
         uitlegOverlay=(LinearLayout) findViewById(R.id.uitlegoverlay);
	  	 oorkonde = (ImageView) findViewById(R.id.oorkonde); 
		  
	  	 //Log.d("Geography Game",  "viaDonatiescherm= " + viaDonatiescherm);//test 
	  	 
		 //spel uitleg tonen als app wordt geopend, maar alleen als hij standaard wordt geopend, niet als je terugkomt vanuit donatiescherm
		 if(visible==true){ 

		     uitlegOverlay.setVisibility(View.VISIBLE);
		 }
		 
		 
		 
		 /*
		 if(viaDonatiescherm!=null & visible==true){
			 //oorkonde laten zien als via intent binnenkomt dat je van donatiescherm komt, en als donatie bevestigd is
			 oorkonde.setVisibility(View.VISIBLE);
			 
			 	if(amountDonated>=10 && confirmDonation>0){ oorkonde.setImageResource(R.drawable.oorkonde_tweede_officier);}
			 	if(amountDonated>=30 && confirmDonation>0){ oorkonde.setImageResource(R.drawable.oorkonde_eerste_officier);}
			 	if(amountDonated>=50 && confirmDonation>0){ oorkonde.setImageResource(R.drawable.oorkonde_gezagvoerder);}
			 	if(amountDonated>=100 && confirmDonation>0){ oorkonde.setImageResource(R.drawable.oorkonde_admiraal);}
		 }
		*/
		 
		 
		 
		if(visible==false ){ 
			oorkonde.setImageResource(0);//om image van oorkonde leeg te maken, want het spel dat je erna speelt crashed anders door out of memory
			uitlegOverlay.setVisibility(View.GONE); //overlays onzichtbaar
			oorkonde.setVisibility(View.GONE);// overlay oorkonde onzichtbaar maken 
		}
		 
		//als je vanuit donatie scherm komt, en de activity is vernieuwd, dan naar de button zoomen, want daar kwam je oorspronkelijk vandaan, zodat je alsnog kan doneren
		//if(visibleFromDonate!=null){
		//	zoomToButton();
		//}
		
   }
   

   
   
   
   
   
   //method om naar de button weer in beeld te brengen als je terugkomt vanuit donatiescherm. De oude targetlocatie ophalen
   void zoomToButton(){
	   newLocation=false;// je wilt naar de locatie gaan die je net gevonden hebt
	   String[] targetArray=targetLocation();//method uitvoeren van de targetLocation
	   targetLat= Double.parseDouble( targetArray[4]);//target lng uit de array halen en als double in var zetten
	   targetLng= Double.parseDouble( targetArray[5]);
	   marker3 = mMap.addMarker(new MarkerOptions()
       .icon(BitmapDescriptorFactory.fromResource(R.drawable.donate_button_200px))
       .anchor(0.5f, 0.3f)// Anchors 
       .position(new LatLng(targetLat, targetLng)));

	   final LatLng coordinate = new LatLng(targetLat, targetLng); 
   	   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 21.25f)); //naar doel met zoom
	   
   	 //  klikbaar maken
		 mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

		        @Override
		        public boolean onMarkerClick(Marker marker) {
		        Intent intent = new Intent(getApplicationContext(),Donate.class);
		        intent.putExtra("idWebpage", idWebpage);// id van de webpagina van betreffende project meesturen met
	    	    startActivity(intent);	
		        return true;
		        }
		    });
	  newLocation=true;//nu op true want als je op start drukt wil je, na info te hebben gelezen en/of gedoneerd te hebben, naar een nieuwe locatie
   }
   
   
   
   
   
   
   //target laten zien met marker als op knop showtarget wordt gedrukt
   void showTarget(){
	   oorkonde = (ImageView) findViewById(R.id.oorkonde); 
	   oorkonde.setVisibility(View.GONE); //oorkonde onzichtbaar maken, voor geval hij zichtbaar was
	   handler.removeCallbacks(r);//de runnable loop stoppen
	   mMap.clear();//alle markers ed verwijderen
	   final LatLng coordinate = new LatLng(targetLat, targetLng); 
   	   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 5.8f)); //naar doel met zoom, was 5.8f
   	  
   	    Handler handler = new Handler();
 
   	    Runnable runnable = new Runnable() {
   	     @Override
   	    public void run() {
   	      /* do what you need to do */
   	     mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f), 12000, null);// langzaam inzoomen, was 9.5f

   	      //handler.postDelayed(this, 100); alleen voor loopen
   	    }
   	   };
   	  
   	   handler.postDelayed(runnable, 2500);// na 3 sec gaan inzoomen
	
   }
   
   
   
   
   //internet connectie checken
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
   
   
    
    
    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
          
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        if (started){
        	if (mediaPlayer!=null){
	        	 if (mediaPlayer.isPlaying()){
			         mediaPlayer.stop();//na showscore screen, want daar zit volume regelaar, en dan moet mediaplayer nog bestaan
					 mediaPlayer.release();
	        	 	}
        	  	}
        	 }
        started=false;
        handler.removeCallbacks(r);//de runnable stoppen
    
        //recet(); gedaan om memory leak tes stoppen, maar geeft crash, want nullpointer exeption 
       // PollFish.init(this, "b8bb97cf-fd89-4293-a4a1-dbe406aeec78", Position.MIDDLE_RIGHT,0);
        Log.d("geography game","onPause geactiveerd");//test
    }//End of onPause
    
 
    
    @Override
    protected void onStop() {
       super.onStop();
       
       if (started){
    	   if (mediaPlayer!=null){
	    		 if (mediaPlayer.isPlaying()){
			         mediaPlayer.stop();//na showscore screen, want daar zit volume regelaar, en dan moet mediaplayer nog bestaan
					 mediaPlayer.release();
	    		 } 
    	   }
       }
       
   	   Log.d("Game","onStop geactiveerd");//test
       started=false;
       handler.removeCallbacks(r);//de runnable stoppen	
       //recet(); idem, crash als spel stopt zonder opgestart te zijn, als er geen wifi is
       
    }//End of onStop
 
 
 
    
} 
    
    
   


