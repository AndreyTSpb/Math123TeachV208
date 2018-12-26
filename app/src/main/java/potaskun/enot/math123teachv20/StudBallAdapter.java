package potaskun.enot.math123teachv20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class StudBallAdapter extends ArrayAdapter<StudBall> {

    private List<StudBall> studBalls;
    private int resource;
    private Context mContext;
    private LayoutInflater lif;

    public StudBallAdapter(@NonNull Context context, int resource, List<StudBall> studBalls) {
        super(context, resource, studBalls);
        this.studBalls = studBalls;
        this.lif = LayoutInflater.from(context);
        this.resource = resource;
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if (convertView == null) {
            //если вид нет то его создать
            convertView = lif.inflate(this.resource, parent, false);
            //Сохраняем предыдущее значение чтоб поновой не строилось
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final StudBall sb = studBalls.get(position);
        viewHolder.quest.setText("Задача№ "+sb.getQuest() + " "+sb.getBall());
        String ball = sb.getBall();
        switch (ball){
            case "0":
                viewHolder.radioButton1.setChecked(true);
                break;
            case "1":
                viewHolder.radioButton2.setChecked(true);
                break;
            case "2":
                viewHolder.radioButton3.setChecked(true);
                break;
            case "3":
                viewHolder.radioButton4.setChecked(true);
                break;
            default:
                viewHolder.radioButton1.setChecked(true);
        }
        /*Нажатие по названию группы*/

        return convertView;
    }

    private class ViewHolder{
        final TextView quest;
        final RadioButton radioButton1;
        final RadioButton radioButton2;
        final RadioButton radioButton3;
        final RadioButton radioButton4;

        public ViewHolder(View view){
            quest    = view.findViewById(R.id.quest);
            radioButton1 = view.findViewById(R.id.radioButton1);
            radioButton2 = view.findViewById(R.id.radioButton2);
            radioButton3 = view.findViewById(R.id.radioButton3);
            radioButton4 = view.findViewById(R.id.radioButton4);
        }
    }
}
