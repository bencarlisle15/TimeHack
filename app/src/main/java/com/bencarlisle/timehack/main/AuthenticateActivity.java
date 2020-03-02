package com.bencarlisle.timehack.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bencarlisle.timelibrary.main.DataControllable;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

public class AuthenticateActivity extends Activity {
    private final static int RC_AUTH = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(Uri.parse("https://accounts.google.com/o/oauth2/auth"), Uri.parse("https://oauth2.googleapis.com/token"));
            AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(serviceConfig, "1343785243-kmbpa91oug8lc1rrglaibrmedbafca8i.apps.googleusercontent.com", ResponseTypeValues.CODE, Uri.parse("com.bencarlisle.timehack:/oauth2redirect"));
            AuthorizationRequest authRequest = authRequestBuilder.setScope("https://www.googleapis.com/auth/calendar").build();
            AuthorizationService authService = new AuthorizationService(this);
            Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
            authService.dispose();
            startActivityForResult(authIntent, RC_AUTH);
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_AUTH) {
            AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
            AuthorizationException error = AuthorizationException.fromIntent(data);
            if (response != null) {
                AuthState authState = new AuthState(response, error);
                AuthorizationService authService = new AuthorizationService(this);
                authService.performTokenRequest(response.createTokenExchangeRequest(), (resp, err) -> {
                    if (resp != null) {
                        authState.update(resp, err);
                        Log.e("Auth", authState.getAccessToken() + ":");
                        Toast.makeText(this, "Authorization Successful", Toast.LENGTH_SHORT).show();
                        DataControl dataControl = new DataControl(this);
                        dataControl.setAuthState(authState.jsonSerializeString());
                        dataControl.close();
                    } else {
                        Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                authService.dispose();
                finish();
            } else {
                Toast.makeText(this, "Authorization Failed: ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}