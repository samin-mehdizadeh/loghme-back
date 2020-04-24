package ie.domain;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FoodPartyCounter extends TimerTask {
        private static FoodPartyCounter instance;
        private Timer timer;
        private int foodPartyTime;
        private int remainingTime;
        private boolean stop;


        public static FoodPartyCounter getInstance(){
                if(instance == null){
                        instance = new FoodPartyCounter();
                }
                return instance;
        }

        public int getRemainingTime() {
                return this.remainingTime;
        }
        public void setTimer(int foodPartyTime){
                this.foodPartyTime = foodPartyTime / 1000;
                this.stop = true;
                timer =  new Timer();
                timer.schedule(this,0,1000);
        }

        public void start() {
                /*System.out.println("x");
                System.out.println(foodPartyTime);
                this.foodPartyTime = foodPartyTime / 1000;
                this.remainingTime = foodPartyTime / 1000;
                System.out.println(this.foodPartyTime);
                System.out.println("y");
                timer =  new Timer();*/
                this.stop = false;
                this.remainingTime = this.foodPartyTime;
        }
        public void stop() {
                /*if(timer != null){
                        System.out.println("khkhkh");
                        timer.cancel();
                }
                else{

                        System.out.println("haaaaaaaaaaaaa");
                }*/
                this.stop =true;
        }
        public void run() {
                if(this.stop == false){
                        if(this.remainingTime >= 2){
                                System.out.println("correct");
                                System.out.println(this.remainingTime);
                                this.remainingTime = this.remainingTime - 1;
                        }
                        else{
                                this.remainingTime = this.foodPartyTime;
                        }
                }
                else{
                        this.remainingTime = this.foodPartyTime;
                }
        }
}