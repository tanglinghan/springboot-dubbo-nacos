package top.b0x0.admin.common.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * jackson 工具类
 *
 * @author musui
 */
public class JackJsonUtils {
    /**
     * 定义jackson对象
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     */
    public static String toJsonString(Object data) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(data);

    }

    /**
     * 将json结果集转化为对象
     */
    public static <T> T toBean(String jsonData, Class<T> beanType) throws IOException {
        return OBJECT_MAPPER.readValue(jsonData, beanType);
    }

    /**
     * 将json数据转换成pojo对象list
     */
    public static <T> T toList(String jsonData, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(jsonData, typeReference);
    }
}