package com.beraldi.tictactoemultiplayer

import android.content.Context
import com.android.volley.toolbox.Volley

class Conf {

    companion object{
        //var url = "http://172.20.10.5:5000/?"
        var url = "http://192.168.1.74:5000/?"
        //var url = "http://robertoberaldi.pythonanywhere.com/?"

        val clientid="241824565470-884uiqtb5glg4ec4d4l9nfg0el6ac18s.apps.googleusercontent.com"

        var myid=-1
        val POLLING = 0
        val NEWGAME = 1 //Not used
        val JOIN = 2 //Not used
        val MOVE= 3
        val WANT_TO_PLAY=4
        val pollingPeriod = 1000L



        //States of the server
        val INIT = 0 //Not used
        val WAITING = 1
        val PLAY = 2 //Not used


        var canPoll=false
     //python3 MyMultiplayerServer.py
    }
}