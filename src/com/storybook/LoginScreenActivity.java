package com.storybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


public class LoginScreenActivity extends FragmentActivity {
	
	private LoginFragment loginFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();

            getSupportFragmentManager().beginTransaction()
                            .add(android.R.id.content, loginFragment).commit();
    } else {
            // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager()
                            .findFragmentById(android.R.id.content);
    }
	}
}
