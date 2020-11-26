package com.beraldi.tictactoemultiplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beraldi.tictactoemultiplayer.Conf.Companion.NEWGAME
import com.beraldi.tictactoemultiplayer.Conf.Companion.POLLING
import com.beraldi.tictactoemultiplayer.Conf.Companion.WANT_TO_PLAY
import com.beraldi.tictactoemultiplayer.Conf.Companion.url
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [Waiting.newInstance] factory method to
 * create an instance of this fragment.
 */
class Waiting : Fragment() {

    var queue : RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        want_to_play()
      //  Handler(Looper.getMainLooper()).postDelayed({check()},Conf.pollingPeriod)
    }


    fun want_to_play() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
                Request.Method.GET, url + "req=" + WANT_TO_PLAY + "&who=" + Conf.myid,
                { response ->
                    // Display the first 500 characters of the response string.
                    val reply = JSONObject(response.toString())
                    val error = reply!!.getBoolean("error")
                    if (error) {
                        Toast.makeText(context, "error:"+reply.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val who = reply!!.getInt("who")
                        Log.i("info", "response: " + reply?.toString(2))
                        if (who == 0) {
                            Conf.myid = 0
                            //Wait until the othe player comes in
                            Handler(Looper.getMainLooper()).postDelayed({ check() }, Conf.pollingPeriod)
                        }

                        if (who == 1) {
                            Conf.myid = 1
                            findNavController().navigate(R.id.action_waiting_to_SecondFragment)
                        }
                    }
                },
                { error: VolleyError? ->
                    Log.i("info", "Polling: " + error.toString())
                })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

    fun check() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url + "req="+POLLING+"&who="+Conf.myid,
            { response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                val state = reply!!.getInt("state")
                Log.i("info", "response: " + reply?.toString(2))

                //No players yet, keep polling...
                if (state==Conf.WAITING){
                    Handler(Looper.getMainLooper()).postDelayed({check()},Conf.pollingPeriod)
                } else
                 {
                    findNavController().navigate(R.id.action_waiting_to_SecondFragment)
                }
            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

}