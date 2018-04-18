package com.freeme.jsonparse.areas;

import java.io.Serializable;

import com.freeme.jsonparse.pojo.Content;
import com.freeme.jsonparse.pojo.Result;

public class BaseArea implements Serializable {
    private static final long serialVersionUID = 1L;
    private Content content;
    private Result result;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


}
