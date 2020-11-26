package com.beraldi.tictactoemultiplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        val clientid=Conf.clientid
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(clientid)
            .build()


        val intent = Identity.getSignInClient(this).getSignInIntent(request)
        intent.addOnSuccessListener { request ->
            startIntentSenderForResult(request.intentSender,
                1,
                null,
                0,
                0,
                0,
                null)
            Log.i("info", "onCreate: ")
        }
        intent.addOnFailureListener { e-> Log.i("info", "onCreate: ")}
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Log.i("info2", "onOptionsItemSelected: ")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}