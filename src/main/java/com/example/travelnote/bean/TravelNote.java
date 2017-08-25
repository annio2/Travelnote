package com.example.travelnote.bean;

import java.util.List;

/**
 * Created by wuguanglin on 2017/8/10.
 */

/*服务器返回的json数据将被解析为TravelNote，包含一个结果码和对象数组*/
public class TravelNote {
    private int result;
    private List<Response> response;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }
}
