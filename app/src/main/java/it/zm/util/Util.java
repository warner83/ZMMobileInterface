package it.zm.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;


public class Util {
	public static void handleException(Exception e, Context c){
        // TODO this alert dialog is not shown!
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder( c );
        builder
            .setMessage( "There was an error: " + e.getMessage() )
            .setCancelable( false )
            .setNeutralButton( "Ok.", new DialogInterface.OnClickListener()
            {
                public void onClick ( DialogInterface dialog, int which )
                {
                	Log.d("ERROR","Terminate app");
		        	System.exit(0);
                }
            } );

        AlertDialog error = builder.create();
        error.show();
		
		Log.d("ERROR","Error "+e.getMessage());
	}
}
