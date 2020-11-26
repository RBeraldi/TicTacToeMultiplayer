package com.beraldi.tictactoemultiplayer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

     lateinit var pv : PlayerView
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        pv= PlayerView(context)
        return pv
        //return inflater.inflate(R.layout.fragment_second, container, false)
    }


    override fun onStart() {
        super.onStart()
        Conf.canPoll=true
        pv.poll()
    }

    override fun onPause() {
        super.onPause()
        Conf.canPoll=false
    }



    }