package com.soft.reclutamiento.api.config;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PipelinR Configuration for CQRS Pattern
 * <p>
 * This configuration class registers the PipelinR Pipeline bean in Spring context.
 * PipelinR is the Java equivalent of MediatR in .NET.
 * <p>
 * The Pipeline bean is used by Application Services to send Commands and Queries
 * to their respective handlers.
 * <p>
 * .NET Equivalent: No direct equivalent - MediatR is registered via IServiceCollection.AddMediatR()
 */
@Configuration
public class PipelinRConfig {

    /**
     * Creates and configures the PipelinR Pipeline bean
     * <p>
     * This bean is injected into ApplicationBase and used by all Application Services
     * to send Commands and Queries to handlers.
     *
     * @param commandHandlers Spring provides all registered Command.Handler beans
     * @param notificationHandlers Spring provides all registered Notification.Handler beans
     * @return Configured Pipeline instance
     */
    @Bean
    public Pipeline pipeline(
            ObjectProvider<Command.Handler> commandHandlers,
            ObjectProvider<Notification.Handler> notificationHandlers) {
        return new Pipelinr()
                .with(commandHandlers::stream)
                .with(notificationHandlers::stream);
    }
}
