package com.foodai.orchestration.mapper;

import com.foodai.orchestration.domain.model.Saga;
import com.foodai.orchestration.domain.model.SagaStatus;
import com.foodai.orchestration.domain.model.SagaStep;
import com.foodai.orchestration.dto.request.StartSagaRequest;
import com.foodai.orchestration.dto.response.SagaResponse;
import com.foodai.orchestration.dto.response.SagaStepResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T12:26:45+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SagaMapperImpl implements SagaMapper {

    @Override
    public Saga toEntity(StartSagaRequest request, String correlationId) {
        if ( request == null && correlationId == null ) {
            return null;
        }

        Saga.SagaBuilder saga = Saga.builder();

        if ( request != null ) {
            saga.entityId( request.getEntityId() );
            saga.entityType( request.getEntityType() );
            Map<String, String> map = request.getMetadata();
            if ( map != null ) {
                saga.metadata( new LinkedHashMap<String, String>( map ) );
            }
            Map<String, Object> map1 = request.getPayload();
            if ( map1 != null ) {
                saga.payload( new LinkedHashMap<String, Object>( map1 ) );
            }
            saga.priority( request.getPriority() );
            saga.timeoutMs( request.getTimeoutMs() );
            saga.type( request.getType() );
            saga.userId( request.getUserId() );
        }
        saga.correlationId( correlationId );
        saga.status( SagaStatus.PENDING );
        saga.currentStepIndex( 0 );
        saga.totalSteps( 0 );
        saga.completedSteps( 0 );
        saga.retryCount( 0 );
        saga.maxRetries( 3 );

        return saga.build();
    }

    @Override
    public SagaResponse toResponse(Saga saga) {
        if ( saga == null ) {
            return null;
        }

        SagaResponse.SagaResponseBuilder sagaResponse = SagaResponse.builder();

        sagaResponse.completedAt( saga.getCompletedAt() );
        sagaResponse.completedSteps( saga.getCompletedSteps() );
        Map<String, Object> map = saga.getContext();
        if ( map != null ) {
            sagaResponse.context( new LinkedHashMap<String, Object>( map ) );
        }
        sagaResponse.correlationId( saga.getCorrelationId() );
        sagaResponse.createdAt( saga.getCreatedAt() );
        sagaResponse.currentStepIndex( saga.getCurrentStepIndex() );
        sagaResponse.entityId( saga.getEntityId() );
        sagaResponse.entityType( saga.getEntityType() );
        sagaResponse.errorCode( saga.getErrorCode() );
        sagaResponse.errorMessage( saga.getErrorMessage() );
        sagaResponse.id( saga.getId() );
        sagaResponse.startedAt( saga.getStartedAt() );
        sagaResponse.status( saga.getStatus() );
        sagaResponse.steps( toStepResponses( saga.getSteps() ) );
        sagaResponse.totalDurationMs( saga.getTotalDurationMs() );
        sagaResponse.totalSteps( saga.getTotalSteps() );
        sagaResponse.type( saga.getType() );
        sagaResponse.updatedAt( saga.getUpdatedAt() );
        sagaResponse.userId( saga.getUserId() );

        sagaResponse.progressPercent( saga.getProgressPercent() );

        return sagaResponse.build();
    }

    @Override
    public SagaStepResponse toStepResponse(SagaStep step) {
        if ( step == null ) {
            return null;
        }

        SagaStepResponse.SagaStepResponseBuilder sagaStepResponse = SagaStepResponse.builder();

        sagaStepResponse.action( step.getAction() );
        sagaStepResponse.compensationExecuted( step.isCompensationExecuted() );
        sagaStepResponse.completedAt( step.getCompletedAt() );
        sagaStepResponse.durationMs( step.getDurationMs() );
        sagaStepResponse.errorCode( step.getErrorCode() );
        sagaStepResponse.errorMessage( step.getErrorMessage() );
        Map<String, Object> map = step.getOutputData();
        if ( map != null ) {
            sagaStepResponse.outputData( new LinkedHashMap<String, Object>( map ) );
        }
        sagaStepResponse.retryCount( step.getRetryCount() );
        sagaStepResponse.startedAt( step.getStartedAt() );
        sagaStepResponse.status( step.getStatus() );
        sagaStepResponse.stepId( step.getStepId() );
        sagaStepResponse.stepName( step.getStepName() );
        sagaStepResponse.stepOrder( step.getStepOrder() );
        sagaStepResponse.targetService( step.getTargetService() );

        return sagaStepResponse.build();
    }

    @Override
    public List<SagaStepResponse> toStepResponses(List<SagaStep> steps) {
        if ( steps == null ) {
            return null;
        }

        List<SagaStepResponse> list = new ArrayList<SagaStepResponse>( steps.size() );
        for ( SagaStep sagaStep : steps ) {
            list.add( toStepResponse( sagaStep ) );
        }

        return list;
    }

    @Override
    public List<SagaResponse> toResponses(List<Saga> sagas) {
        if ( sagas == null ) {
            return null;
        }

        List<SagaResponse> list = new ArrayList<SagaResponse>( sagas.size() );
        for ( Saga saga : sagas ) {
            list.add( toResponse( saga ) );
        }

        return list;
    }
}
