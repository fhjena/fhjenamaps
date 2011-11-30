package eu.AndroidTraining.Dashboard;

import android.R.bool;
import android.app.Activity;
import android.app.ActivityManager;
import android.widget.Button;
import android.widget.TextView;

public class Verwaltung {
	
		public int Zustand;						//Variable Zustand
		private boolean ButtonShowPosition;
		private boolean ButtonRouting;
		private boolean ButtonCampus=true;
		private boolean ButtonZurueck;
		
		public Verwaltung (int Zustand){ 		//Constructor
			this.Zustand = Zustand;
		}
		
		
		
		public void onClick1(Button sf_ShowPosition){
			ButtonShowPosition=true;
				}			 
		public void onClick2(Button sf_Routing){
			ButtonRouting=true;
				}
		public void onClick3(Button sf_Campus){
			ButtonCampus=true;
		}
    	  	
		
		public void ZustandErmitteln(){
			if (ButtonShowPosition) {
				Zustand = 1;
			
			} else if(ButtonRouting){
				Zustand = 2;
				
			}
			else if(ButtonCampus){
				Zustand = 3;
			
			}
			else{
				Zustand = 9;
			
			}		
			}

		
	}

