package com.beraldi.tictactoemultiplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PlayerView: View, View.OnTouchListener {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    val POLLING = Conf.POLLING
    val MOVE= Conf.MOVE
    val who = Conf.myid

    val pollingperiod = Conf.pollingPeriod

    val queue = Volley.newRequestQueue(context)

    val url = Conf.url
    var reply : JSONObject? = null


    private var winner = -1
    private var chess = IntArray(9) { -1 }


    private val linepaint = Paint().apply {
        color= Color.WHITE
        strokeWidth=10f
        style= Paint.Style.STROKE
    }


    private var dx=0f
    private var dy=0f


    init {
        setOnTouchListener(this)
        poll()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dx=width/3f
        dy=height/3f

        checkwinner()

        canvas?.drawRGB(255, 4, 244)

        //Player X: corresponds to player who=1 and it marks a move as 1 in the chessboard
        //Player O: corresponds to player who=0 and marks a move as 0 in the chessboard

        if (winner==who) canvas?.drawRGB(0, 255, 0)
        if ((winner!=-1) and (winner!=who))canvas?.drawRGB(255, 0, 0)

        canvas?.drawLine(dx, 0f, dx, height.toFloat(), linepaint)
        canvas?.drawLine(2 * dx, 0f, 2 * dx, height.toFloat(), linepaint)

        canvas?.drawLine(0f, dy, width.toFloat(), dy, linepaint)
        canvas?.drawLine(0f, 2 * dy, width.toFloat(), 2 * dy, linepaint)

        for (k in 0..8) {
            if (chess[k]==0) //draw a Circle
            {
                val i = k%3
                val j = k/3
                // Toast.makeText(context, ""+i+" "+j, Toast.LENGTH_SHORT).show()
                canvas?.drawCircle(dx * i.toFloat() + dx / 2, dy * j.toFloat() + dy / 2, dx / 2f, linepaint)
            }

            if (chess[k]==1){ //draw a cross
                val i = k%3
                val j = k/3

                canvas?.drawLine(i * dx, j * dy, (i + 1) * dx, (j + 1) * dy, linepaint)
                canvas?.drawLine((i + 1) * dx, j * dy, i * dx, (j + 1) * dy, linepaint)
                //Toast.makeText(context, ""+i+" "+j, Toast.LENGTH_SHORT).show()

            }
        }

    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                //find the center of the area
                if (winner != -1) return reset()
                val j = (event?.x / dx).toInt()
                val i = (event?.y / dy).toInt()
                val k = 3 * i + j
                if (who==1) chess[k] = 1
                if (who==0) chess[k] = 0
                makeMove(k)
                invalidate()
                // Toast.makeText(context, ""+i+" "+j, Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }



    fun checkwinner(){
        var a=0
        var b=0
        //check horizontal lines
        for (k in 0..8) {
            if (chess[k]==0) a+=1
            if (chess[k]==1) b+=1
            if ((k==2) or (k==5) or (k==8)) {
                if (a==3){winner=0;return}
                if (b==3){winner=1;return}
                a=0;b=0
            }
        }
        //check vertical lines
        for (offset in 0..2)
            for (k in listOf(0, 3, 6)) {
                if (chess[k + offset]==0) a+=1
                if (chess[k + offset]==1) b+=1
                if (a==3){winner=0;return}
                if (b==3){winner=1;return}
                if (k==6) {a=0;b=0}
            }
        //check diagonals
        for (k in listOf(0, 4, 8)) {
            if (chess[k]==0) a+=1
            if (chess[k]==1) b+=1
            if (a==3){winner=0;return}
            if (b==3){winner=1;return}
        }
        a=0;b=0
        for (k in listOf(2, 4, 6)) {
            if (chess[k]==0) a+=1
            if (chess[k]==1) b+=1
            if (a==3){winner=0;return}
            if (b==3){winner=1;return}
        }
    }


    fun reset(): Boolean {
        return true
        //TO DO: Add the logic to start a new game
        for (i in 0..8) chess[i]=-1
        winner=-1
        makeMove(-1)
        //poll()
        invalidate()
        return true
    }
    fun makeMove(k:Int) {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.POST, url + "req="+MOVE+"&who="+who+"&move="+k.toString(),
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                Log.i("info", "response: " + reply?.toString(2))
            },
            { error: VolleyError? ->
                // Log.i("info", "makeMove: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    fun checkMove() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url + "req="+POLLING+"&who="+who,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                val k = reply!!.getInt("move")
                val who = reply!!.getInt("who")
                if ((k!! >= 0) and (k <= 9) and (who == 0)) chess[k!!] = 0
                if ((k!! >= 0) and (k <= 9) and (who == 1)) chess[k!!] = 1
                if ((who != 0) and (who != 1)) Log.i("info", "checkMove: who out of range")

                Log.i("info", "response: " + reply?.toString(2))

                invalidate()

            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun poll (){
        if (!Conf.canPoll) return
        if (winner!=-1) return
        Handler(Looper.getMainLooper()).postDelayed({
            checkMove()
            poll()
        },pollingperiod)

    }


}
