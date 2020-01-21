package org.jboss.ejb.client;

import org.jboss.ejb.client.annotation.ClientInterceptorPriority;

import io.opentracing.Span;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.util.GlobalTracer;

@ClientInterceptorPriority(TracingInterceptor.PRIORITY)
public class TracingInterceptor implements EJBClientInterceptor {

    public static final int PRIORITY = ClientInterceptorPriority.JBOSS_AFTER + 175; 
    
    @Override
    public void handleInvocation(EJBClientInvocationContext context) throws Exception {
        Span span = GlobalTracer.get().activeSpan();
        // EJB client shouldn't be responsible for creating 
        // any spans (if it is not instrumenting the EJB client itself)
        if(span != null) {
            GlobalTracer.get().inject(span.context(), Format.Builtin.TEXT_MAP_INJECT, new TextMapInjectAdapter(context.getContextData()));   
        }
        context.sendRequest();
    }

    @Override
    public Object handleInvocationResult(EJBClientInvocationContext context) throws Exception {
        return context.getResult();
    }
}
