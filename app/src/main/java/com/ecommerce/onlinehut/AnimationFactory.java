package com.ecommerce.onlinehut;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class AnimationFactory {
    public static AnimationFactory instance=new AnimationFactory();
    private AnimationFactory(){

    }
    public static AnimationFactory getInstance(){
        if(instance==null){
            instance=new AnimationFactory();
        }
        return instance;
    }
    public Animation left_to_right_scale_anim(){
        ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        return anim;
    }
    public Animation right_to_left_scale_anim(){
                ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(500);
                return anim;
    }
}
