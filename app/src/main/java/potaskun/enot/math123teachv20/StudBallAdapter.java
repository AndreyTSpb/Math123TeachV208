package potaskun.enot.math123teachv20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class StudBallAdapter extends ArrayAdapter<StudBall> {

    private List<StudBall> studBalls;
    private int resource;
    private Context mContext;
    private LayoutInflater lif;
    private int qu;

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
        qu = sb.getQuest()+1;
        viewHolder.quest.setText(""+qu);
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
//        viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId){
//                    case R.id.radioButton1:
//                        ((StudentBallsActivity)mContext).addBall("0");
//                        break;
//                    case R.id.radioButton2:
//                        ((StudentBallsActivity)mContext).addBall("1");
//                        break;
//                    case R.id.radioButton3:
//                        ((StudentBallsActivity)mContext).addBall("2");
//                        break;
//                    case R.id.radioButton4:
//                        ((StudentBallsActivity)mContext).addBall("3");
//                        break;
//                }
//            }
//        });
        RadioButton rb1 = viewHolder.radioButton1;
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qu = (String) viewHolder.quest.getText();
                RadioButton rb = (RadioButton)v;
                switch (rb.getId()) {
                    case R.id.radioButton1:
                        ((StudentBallsActivity)mContext).addBall("0", qu);
                        break;
                    case R.id.radioButton2:
                        ((StudentBallsActivity)mContext).addBall("1", qu);
                        break;
                    case R.id.radioButton3:
                        ((StudentBallsActivity)mContext).addBall("2", qu);
                        break;
                    case R.id.radioButton4:
                        ((StudentBallsActivity)mContext).addBall("3", qu);
                        break;
                }
            }
        });


        return convertView;
    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView q = v.findViewById(R.id.quest);
            String qu = q.getText().toString();
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radioButton1:
                    ((StudentBallsActivity)mContext).addBall("0", qu);
                    break;
                case R.id.radioButton2:
                    ((StudentBallsActivity)mContext).addBall("1", qu);
                    break;
                case R.id.radioButton3:
                    ((StudentBallsActivity)mContext).addBall("2", qu);
                    break;
                case R.id.radioButton4:
                    ((StudentBallsActivity)mContext).addBall("3", qu);
                    break;
            }
        }
    };

    private class ViewHolder{
        final TextView quest;
        final RadioButton radioButton1;
        final RadioButton radioButton2;
        final RadioButton radioButton3;
        final RadioButton radioButton4;
        final RadioGroup radioGroup;

        public ViewHolder(View view){
            quest    = view.findViewById(R.id.quest);
            radioButton1 = view.findViewById(R.id.radioButton1);
            radioButton2 = view.findViewById(R.id.radioButton2);
            radioButton3 = view.findViewById(R.id.radioButton3);
            radioButton4 = view.findViewById(R.id.radioButton4);
            radioGroup   = view.findViewById(R.id.groupBall);
        }
    }
}
