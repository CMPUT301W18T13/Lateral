package com.lateral.lateral.service.implementation;

/**
 * Created by chant on 2018-03-02.
 */

public abstract class baseTest<I, O> {
    private I input;
    private O output;

    public baseTest(I input, O output){
        this.input = input;
        this.output = output;
    }

    public O getOutput(){
        return output;
    }
}
