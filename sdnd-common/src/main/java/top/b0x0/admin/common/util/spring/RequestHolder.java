//package top.b0x0.admin.common.util.spring;
//
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Objects;
//
///**
// * 获取 HttpServletRequest
// * @author TANG
// * @date 2020-11-24
// */
//public class RequestHolder {
//
//    public static HttpServletRequest getHttpServletRequest() {
//        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
//    }
//}
