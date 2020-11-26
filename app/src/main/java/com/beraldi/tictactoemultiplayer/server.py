from flask import Flask,jsonify
from flask_restful import Resource, Api, reqparse
import random

app = Flask(__name__)
api = Api(app)

parserget = reqparse.RequestParser()
parserget.add_argument('req',type=int,required=True)
parserget.add_argument('who',type=int,required=True)

parserpost = reqparse.RequestParser()
parserpost.add_argument('req',type=int,required=True)
parserpost.add_argument('who',type=int,required=True)
parserpost.add_argument('move',type=int,required=True)


WANT_TO_PLAY = 4
POLLING = 0
NEWGAME = 1
JOIN = 2
MOVE= 3

MessageAllowed = {'POLLING':POLLING,'NEWGAME':NEWGAME,'JOIN':JOIN,'MOVE':MOVE}


STATE_INIT=0
STATE_WAITING=1
STATE_PLAY=2

STATE = STATE_INIT

LastMove0 = -1
LastMove1 = -1

class TicTacToe(Resource):

    def get(self):
        global LastMove0,LastMove1,STATE,STATE_INIT,STATE_PLAY,STATE_WAITING
        args = parserget.parse_args()
        req = args['req']
        who = args['who']

        print(args)

        reply_msg = {'error':True,'current state':STATE}

        if req == NEWGAME:
            STATE = STATE_INIT
            LastMove0=-1
            LastMove1=-1
            reply_msg['who']=0
            reply_msg['error']=False
            reply_msg['msg']='NEW GAME CREATED'
            reply_msg['state']=STATE

            return jsonify(reply_msg)


        if STATE==STATE_INIT and req==WANT_TO_PLAY:
            STATE=STATE_WAITING
            reply_msg['who']=0
            reply_msg['error']=False
            reply_msg['msg']='SERVER MOVING FROM STATE INIT TO STATE WAIT'
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        if STATE==STATE_WAITING and req==WANT_TO_PLAY:
            STATE=STATE_PLAY
            reply_msg['who']=1
            reply_msg['move']=LastMove0
            reply_msg['error']=False
            reply_msg['msg']='SERVER MOVED FROM STATE WAIT TO PLAY'
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        if STATE==STATE_WAITING and req==POLLING:
            reply_msg['move']=LastMove1
            reply_msg['who']=0
            reply_msg['error']=False
            reply_msg['msg']='SERVER IS WAITING FOR THE OTHER PLAYER'
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        if STATE==STATE_PLAY and req==POLLING and who==0:
            reply_msg['move']=LastMove1
            reply_msg['who']=1
            reply_msg['error']=False
            reply_msg['msg']='SERVER RELAYED THE LAST MOVE FROM 1'
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        if STATE==STATE_PLAY and req==POLLING and who==1:
            reply_msg['move']=LastMove0
            reply_msg['who']=0
            reply_msg['error']=False
            reply_msg['msg']='SERVER RELAYED THE LAST MOVE FROM 0'
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        return jsonify(reply_msg)


    def post(self):
        global LastMove0,LastMove1
        args = parserpost.parse_args()

        reply_msg = {'msg':"error",'error':True,'state':STATE}

        req = args['req']
        who = args['who']
        move = args['move']

        if req == MOVE:
            if who==0:
                LastMove0 = move
            if who==1:
                LastMove1=move
            reply_msg['msg']='NEW MOVE'
            reply_msg['error']=False
            reply_msg['state']=STATE
            return jsonify(reply_msg)

        return jsonify(reply_msg)

api.add_resource(TicTacToe,'/')

if __name__ == '__main__':
    print('starting...api')
    app.run(host='0.0.0.0')
