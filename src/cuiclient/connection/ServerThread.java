/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dell_user
 */
public class ServerThread extends Thread{

    //  ------------------------------------
    //  デバッグ用フラグ
    //  これをtrueにするとコンソール上にログを吐き出します
    //  ----------------------------------------
    private static final boolean DEBUG = false;
    
    
    
    private Socket connectedSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    
    private PlayReceiver playReceiver;
    private LoginReceiver loginReceiver;
    private String name;
    
    private boolean buildGameBoard; // ゲームボードをビルドするモード MULTILINEでスタート / ENDLINEでストップ
    
    public ServerThread(String name,PlayReceiver p,LoginReceiver l){
        this.playReceiver = p;
        this.loginReceiver = l;
        this.name = name;
    }
    
    /**
     * 接続が切れるか終了メッセージを受信するまで接続を継続する
     */
    @Override
    public void run() {
        try {
            String mes;
            while ((mes = this.reader.readLine()) != null) {
                this.onReceiveMessage(mes);    
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 関数connectServer
     * サーバに接続する
     * @param ip
     * @param port
     * @return true 成功 | false 失敗
     */
    public boolean connectServer(String ip,int port){
        try {
            this.connectedSocket = new Socket(ip, port);
            if (this.connectedSocket.isConnected()) {
                this.reader = new BufferedReader(new InputStreamReader(this.connectedSocket.getInputStream(), "UTF-8"));
                this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.connectedSocket.getOutputStream(), "UTF-8")));                
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    /** 
     * 関数sendMessage
     * サーバにコメントメッセージを送信する
     * @param str 送信する文字列
     */
    public void sendMessage(String str){
        if (this.writer != null) {
            this.writer.println(str);
            this.writer.flush();
        }
    }
    
    /**
     * 関数sendPlayMessage
     * サーバにユニットを動かすためのメッセージを送信します
     * @param unit [0-3] ユニットの色を示す番号
     * @param x [0-9] 動かす先の座標
     * @param y [0-9] 動かす先の座標
     */
    public void sendPlayMessage(int unit,int x,int y){
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("405 PLAY ");
        sbuf.append(unit);
        sbuf.append(" ");
        sbuf.append(x);
        sbuf.append(" ");
        sbuf.append(y);
        this.sendMessage(sbuf.toString());
    }
    
    
    /**
     * 関数onReceiveMessage
     * mainLoop()内から呼ばれます
     * メッセージを受信した際に呼ばれます
     */
    private void onReceiveMessage(String str){
        //  ログに書き出す
        if(DEBUG) System.out.println("ServerThread#onReceiveMessage(), "+str);
    
        //  パーサでパースします
        //  パースした結果によってリシーバーを呼び出します
        parseMessage(str);
    }
    
    
    /**
     * メッセージをパースする
     */
    private final Pattern MSGPTN = Pattern.compile("([0-9]+) (.*)");
    private final Pattern TEAMIDMSGPTN = Pattern.compile("102 TEAMID ([0-1])");
    private final Pattern ADVERSARYMSGPTN = Pattern.compile("104 ADVERSARY (.*)");
    private final Pattern GAMEENDMSGPTN = Pattern.compile("502 GAMEEND ([0-1])");
    
    public static Pattern UNITPattern = Pattern.compile("401 UNIT ([0-1]) ([0-4]) ([0-8]) ([0-8])");
    public static Pattern OBSTACLEPattern = Pattern.compile("406 OBSTACLE ([0-5]) ([0-8]) ([0-8])");
    public static Pattern TOWERPattern = Pattern.compile("402 TOWER ([0-5]) ([0-1])");
    public static Pattern SCOREPattern = Pattern.compile("403 SCORE ([0-1]) ([0-9]+)");
    
    private void parseMessage(String message){
        //  終了処理
        if (message.toUpperCase().equals("203 EXIT")) {
            this.sendMessage("200 OK");
            return;
        }
        Matcher mc = MSGPTN.matcher(message);
        if (!mc.matches()) return;
        
        int num = Integer.parseInt(mc.group(1));
        switch(num){
            case 502:                 //  GAMEEND[T]
                Matcher gnd = GAMEENDMSGPTN.matcher(message);
                if( !gnd.matches() ) return;
                int win = Integer.parseInt(gnd.group(1));
                sendMessage("400 GETBOARD");
                return;
            case 100:   //  HELLO
                this.sendMessage("101 NAME "+this.name);
                return;
            case 102:   //  サーバへの接続完了
                Matcher nmc = TEAMIDMSGPTN.matcher(message);
                if (!nmc.matches()) return;
                int pid = Integer.parseInt(nmc.group(1));
                this.loginReceiver.onReceiveTeamId(pid);
                return;
            case 104:       //  相手ユーザの接続完了
                Matcher nmc2 = ADVERSARYMSGPTN.matcher(message);
                if (!nmc2.matches()) return;
                String name = nmc2.group(1);
                this.loginReceiver.onReceiveAdversary(name);
                return;
            case 404:       //  DOPLAY受信
                this.sendMessage("400 GETBOARD");
                return;
            case 201:       //  MULTILINE受信
                buildGameBoard = true;
                this.playReceiver.onReceiveMultiLine();
                return;
            case 401:       // UNIT受信
                Matcher umc = UNITPattern.matcher(message);
                if(!umc.matches()) return;
                this.playReceiver.onReceiveUnit(Integer.parseInt(umc.group(1)), 
                        Integer.parseInt(umc.group(2)), Integer.parseInt(umc.group(3)), Integer.parseInt(umc.group(4)));
                return;
            case 402:       // TOWER受信
                Matcher tmc = TOWERPattern.matcher(message);
                if(!tmc.matches()) return;
                this.playReceiver.onReceiveTower(Integer.parseInt(tmc.group(1)), Integer.parseInt(tmc.group(2)));
                return;
            case 403:       // SCORE受信
                Matcher smc = SCOREPattern.matcher(message);
                if (!smc.matches()) return;
                this.playReceiver.onReceiveScore(Integer.parseInt(smc.group(1)), Integer.parseInt(smc.group(2)));
                return;
            case 202:       //  LINEEND受信
                //  ゲームボード作成
                buildGameBoard = false;
                this.playReceiver.onReceiveLineEnd();
                return;
            case 500:   //  PLAYED受信
                this.playReceiver.onReceivePlayed();
                return;
            case 303: // COUND NOT MOVE
                this.playReceiver.onReceiveCoundNotMove();
                return;
            case 200:   //  OK
                this.playReceiver.onReceiveOK();
                return;
        }
        
        
        
        
    }
    
    
    
    
}
