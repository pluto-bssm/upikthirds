package pluto.upik.shared.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import pluto.upik.shared.exception.BadWordException;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.InvalidParameterException;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.exception.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphQL 전용 예외 핸들러
 * GraphQL 요청에서 발생하는 예외를 클라이언트에게 적절한 형태로 전달합니다.
 */
@Slf4j
@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        log.error("GraphQL 예외 발생: path={}, message={}",
                env.getExecutionStepInfo().getPath(), ex.getMessage());

        // BadWordException 처리
        if (ex instanceof BadWordException) {
            BadWordException badWordEx = (BadWordException) ex;
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", badWordEx.getErrorCode());
            extensions.put("fieldName", badWordEx.getFieldName());

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(badWordEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // ResourceNotFoundException 처리
        if (ex instanceof ResourceNotFoundException) {
            ResourceNotFoundException notFoundEx = (ResourceNotFoundException) ex;
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", "RESOURCE_NOT_FOUND");

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(notFoundEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // UnauthorizedException 처리
        if (ex instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedEx = (UnauthorizedException) ex;
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", "UNAUTHORIZED");
            extensions.put("resource", unauthorizedEx.getResource());

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.UNAUTHORIZED)
                    .message(unauthorizedEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // InvalidParameterException 처리
        if (ex instanceof InvalidParameterException) {
            InvalidParameterException invalidParamEx = (InvalidParameterException) ex;
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", invalidParamEx.getErrorCode());
            extensions.put("parameterName", invalidParamEx.getParameterName());
            extensions.put("parameterValue", invalidParamEx.getParameterValue());

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(invalidParamEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // BusinessException 처리
        if (ex instanceof BusinessException) {
            BusinessException businessEx = (BusinessException) ex;
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", businessEx.getErrorCode());

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(businessEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // IllegalArgumentException 처리
        if (ex instanceof IllegalArgumentException) {
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", "ILLEGAL_ARGUMENT");

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // IllegalStateException 처리
        if (ex instanceof IllegalStateException) {
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", "ILLEGAL_STATE");

            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(extensions)
                    .build();
        }

        // 그 외의 예외는 기본 처리
        return null;
    }
}
