package net.uprin.mayiuseit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by uPrin on 2017. 12. 9..
 */

public class Comment {
    @SerializedName("comment_srl")
    private int comment_srl;
    @SerializedName("member_srl")
    private int member_srl;
    @SerializedName("document_srl")
    private int document_srl;
    @SerializedName("content")
    private String content;
    @SerializedName("rgsde")
    private String rgsde;

    public int getComment_srl() {
        return comment_srl;
    }

    public void setComment_srl(int comment_srl) {
        this.comment_srl = comment_srl;
    }

    public int getMember_srl() {
        return member_srl;
    }

    public void setMember_srl(int member_srl) {
        this.member_srl = member_srl;
    }

    public int getDocument_srl() {
        return document_srl;
    }

    public void setDocument_srl(int document_srl) {
        this.document_srl = document_srl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRgsde() {
        return rgsde;
    }

    public void setRgsde(String rgsde) {
        this.rgsde = rgsde;
    }
}
