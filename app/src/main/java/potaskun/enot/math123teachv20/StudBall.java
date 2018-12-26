package potaskun.enot.math123teachv20;

import org.json.JSONObject;

public class StudBall {
    private int quest;
    private String ball;

    public StudBall(int quest, String ball){
        this.quest = quest;
        this.ball  = ball;
    }

    public String getBall() {
        return ball;
    }

    public void setBall(String ball) {
        this.ball = ball;
    }

    public int getQuest() {
        return quest;
    }

    public void setQuest(int quest) {
        this.quest = quest;
    }
}
