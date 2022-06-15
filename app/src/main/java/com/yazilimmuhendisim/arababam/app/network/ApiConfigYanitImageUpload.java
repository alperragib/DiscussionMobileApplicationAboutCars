package com.yazilimmuhendisim.arababam.app.network;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;


public interface ApiConfigYanitImageUpload {

    @Multipart
    @POST("api/database_arababam/yanit_image_upload.php")
    Call<ServerResponse> upload(
            @Header("yanit_id") String authorization,
            @PartMap Map<String, RequestBody> map
    );
}