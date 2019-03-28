package com.example.cs16jkd.myapplication.Operations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class GoogleHandler {
    private GoogleSignInOptions gso;
    private GoogleSignInClient SignInClient;
    public static final int REQUEST_GOOGLE_SIGN_IN = 003;

    /**
     * Handle google sign in and management
     */
    public void GoogleHandler(Context ctx) {
        if(isUserSignedIntoGoogle(ctx) == false){
            ConnectToGoogle(ctx);
        }
    }


    /**
     * Checks for the user sign into google
     * @return
     */
    private boolean isUserSignedIntoGoogle(Context ctx) {
        GoogleSignInAccount user_account = GoogleSignIn.getLastSignedInAccount(ctx);
        if(user_account == null){
            return false;
        }else{
            Toast.makeText(ctx, "Welcome "+user_account.getEmail(), Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    /**
     *
     * @param ctx
     */
    private void ConnectToGoogle(Context ctx){
        if(GoogleSignIn.getLastSignedInAccount(ctx) == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            SignInClient = GoogleSignIn.getClient(ctx, gso);

            //Sign in google
            Intent signInGoogleIntent = SignInClient.getSignInIntent();
            ((Activity)ctx).startActivityForResult(signInGoogleIntent, REQUEST_GOOGLE_SIGN_IN);
        }else{
            Toast.makeText(ctx, GoogleSignIn.getLastSignedInAccount(ctx).getEmail(), Toast.LENGTH_SHORT).show();
        }

    }

}
