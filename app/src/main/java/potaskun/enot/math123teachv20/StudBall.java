package potaskun.enot.math123teachv20;

public class StudBall {
    private int quest;
    private int ball;

    public StudBall( int quest, int ball){
        this.quest = quest;
        this.ball  = ball;
    }

    public int getBall() {
        return ball;
    }

    public void setBall(int ball) {
        this.ball = ball;
    }

    public int getQuest() {
        return quest;
    }

    public void setQuest(int quest) {
        this.quest = quest;
    }
}
