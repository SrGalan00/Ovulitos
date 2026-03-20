package com.example.ovulitos;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApiService {
    @POST("api/chat")
    Call<AiResponse> askAssistant(@Body AiRequest request);
}
