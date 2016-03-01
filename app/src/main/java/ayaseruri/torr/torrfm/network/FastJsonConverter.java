package ayaseruri.torr.torrfm.network;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by wufeiyang on 16/1/11.
 */
public class FastJsonConverter extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestConverter<>();
    }

    private static class FastJsonRequestConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        @Override
        public RequestBody convert(T value) throws IOException {
            String json = JSON.toJSONString(value);
            return RequestBody.create(MEDIA_TYPE, json);
        }
    }

    private static class FastJsonResponseConverter<T> implements Converter<ResponseBody, T> {
        private Type type;

        public FastJsonResponseConverter(Type type) {
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            Reader reader = value.charStream();
            BufferedReader in = null;
            try {
                in = new BufferedReader(reader);
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                String json = buffer.toString();
                return JSON.parseObject(json, type);
            }finally {
                if(null != in){
                    in.close();
                }
            }
        }
    }
}
