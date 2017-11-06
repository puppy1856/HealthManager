package com.example.cadtc.androidwithmysqlphpsqlite;

import android.icu.text.DecimalFormat;

/**
 * Created by Jason on 2017/10/9.
 */

public class Food_ProjectSetting {

    int _sex;
    int _age;

    double _height;
    double _weight;
    String _activity;
    double _targetWeight;
    double _targetDay;

    Food_ProjectSetting(int sex,int age,double height,double weight,String activity,double targetWeight,double targetDay){
        _sex = sex;
        _age = age;
        _height = height;
        _weight = weight;
        _activity = activity;
        _targetWeight = targetWeight;
        _targetDay = targetDay;
    }

    public void SetProjectSetting(int sex,int age,double height,double weight,String activity,double targetWeight,double targetDay){
        _sex = sex;
        _age = age;
        _height = height;
        _weight = weight;
        _activity = activity;
        _targetWeight = targetWeight;
        _targetDay = targetDay;
    }

    public void Init(){
        SetProjectSetting(0,23,100.0,30.0,"輕度活動",30.0,1.0);
    }

    public String GetStringBMI(){
        Double BMI = _weight / Math.pow(_height/100,2);
        String s = DoubleDotOne(BMI);
        return s;
    }

    public String GetStringDayNeedCal() {
        Double BEE = 0.0,temp = 0.0;
        if (_sex == 0){
            BEE = 66 + 13.7*_weight + 5*_height - 6.8*_age;
        }
        else if (_sex == 1){
            BEE = 655 + 9.6*_weight + 1.8*_height - 4.7*_age;
        }

        if (_activity.equals("輕度活動")){
            temp = BEE * 1.3;
        }
        else if (_activity.equals("中度活動")){
            temp = BEE * 1.4;
        }
        else if (_activity.equals("重度活動")){
            temp = BEE * 1.5;
        }
        return DoubleDotOne(temp);
    }

    public String GetStringTargetDayNeedCal() {
        Double BEE = 0.0,temp = 0.0;
        if (_sex == 0){
            BEE = 66 + 13.7*_targetWeight + 5*_height - 6.8*_age;
        }
        else if (_sex == 1){
            BEE = 655 + 9.6*_targetWeight + 1.8*_height - 4.7*_age;
        }

        if (_activity.equals("輕度活動")){
            temp = BEE * 1.3;
        }
        else if (_activity.equals("中度活動")){
            temp = BEE * 1.4;
        }
        else if (_activity.equals("重度活動")){
            temp = BEE * 1.5;
        }
        return  DoubleDotOne(temp);
    }

    public String GetStringTotalDayNeedCal(){
        Double temp = Math.abs(_weight -_targetWeight)*7700 /  _targetDay;
        return  DoubleDotOne(temp);
    }

    public String GetDayNeedCalSuggest(){
        Double totalDayNeedCal = Math.abs(_weight -_targetWeight)*7700 /  _targetDay;
        Double dayNeedCal = Double.parseDouble(GetStringDayNeedCal());
        if (totalDayNeedCal < 1000) {
            if (_weight - _targetWeight >= 0) {
                return DoubleDotOne(dayNeedCal - totalDayNeedCal);
            }
            else {
                return DoubleDotOne(dayNeedCal + totalDayNeedCal);
            }
        }
        else {
            return "有害健康請降低目標或增加天數";
        }
    }

    public String DoubleDotOne(double num){

        num *= 10;
        int temp = (int) num;
        temp /= 10;
        num = (double) temp;

        return num + "";
    }
}
