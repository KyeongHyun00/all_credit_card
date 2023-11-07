package com.project.allcreditcard.utility

import retrofit2.Call
import retrofit2.http.*

interface APIService {

    //서버 접속 확인
    @FormUrlEncoded
    @POST("/app/contest")
    fun requestConnect(
        @Field("connectTest") conText: String
    ): Call<String>

    //로그인
    @FormUrlEncoded
    @POST("/app/login")
    fun requestLogin(
        @Field("userID") userID: String,
        @Field("userPW") userPW: String
    ): Call<String>

    //회원가입
    @FormUrlEncoded
    @POST("/app/addUser")
    fun requestRegister(
        @Field("joinName") joinName: String,
        @Field("joinId") joinId: String,
        @Field("joinRegistNum") joinRegistNum: String,
        @Field("joinEmail") joinEmail: String,
        @Field("joinPhoneNum") joinPhoneNum: String,
        @Field("joinPw") joinPw: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/changePw")
    fun changePw(
        @Field("userId") userId: String,
        @Field("changePw") changePw: String
    ): Call<String>
}