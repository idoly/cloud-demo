package xyz.idoly.demo.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import io.micrometer.common.annotation.NoOpValueResolver;
import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.DefaultNewSpanParser;
import io.micrometer.tracing.annotation.ImperativeMethodInvocationProcessor;
import io.micrometer.tracing.annotation.MethodInvocationProcessor;
import io.micrometer.tracing.annotation.NewSpanParser;
import io.micrometer.tracing.annotation.SpanAspect;

// https://docs.micrometer.io/tracing/reference/api.html#_aspect_oriented_programming
@Configuration
public class SpanAspectConfig {

    @Bean
    NewSpanParser newSpanParser() {
        return new DefaultNewSpanParser();
    }

    @Bean
    ValueResolver valueResolver() {
        return new NoOpValueResolver();
    }

    @Bean
    ValueExpressionResolver valueExpressionResolver() {
        return new SpelTagValueExpressionResolver();
    }

    @Bean
    MethodInvocationProcessor methodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer,
            BeanFactory beanFactory) {
        return new ImperativeMethodInvocationProcessor(newSpanParser, tracer, beanFactory::getBean,
                beanFactory::getBean);
    }

    @Bean
    SpanAspect spanAspect(MethodInvocationProcessor methodInvocationProcessor) {
        return new SpanAspect(methodInvocationProcessor);
    }

}

class SpelTagValueExpressionResolver implements ValueExpressionResolver {

    private static final Log log = LogFactory.getLog(SpelTagValueExpressionResolver.class);

    @Override
    public String resolve(String expression, Object parameter) {
        try {
            SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
            ExpressionParser expressionParser = new SpelExpressionParser();
            Expression expressionToEvaluate = expressionParser.parseExpression(expression);
            return expressionToEvaluate.getValue(context, parameter, String.class);
        }
        catch (Exception ex) {
            log.error("Exception occurred while tying to evaluate the SpEL expression [" + expression + "]", ex);
        }
        return parameter.toString();
    }

}