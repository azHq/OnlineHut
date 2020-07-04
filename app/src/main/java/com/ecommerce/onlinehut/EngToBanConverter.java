package com.ecommerce.onlinehut;

public class EngToBanConverter {

    char[] en ={'0','1','2','3','4','5','6','7','8','9'};
    char[] bn ={'০','১','২','৩','৪','৫','৬','৭','৮','৯'};

    private static  EngToBanConverter engToBanConverter=new EngToBanConverter();
    private EngToBanConverter(){

    }
    public static EngToBanConverter getInstance(){

        if(engToBanConverter==null){
            engToBanConverter=new EngToBanConverter();
        }

        return  engToBanConverter;
    }

    public String convert(String eng_str){


        for(int i=0;i<bn.length;i++){
            eng_str=eng_str.replaceAll(en[i]+"",bn[i]+"");
        }

        return  eng_str;

    }

}
