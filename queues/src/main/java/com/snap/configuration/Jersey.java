package com.snap.configuration;

import java.io.IOException;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.snap.controller.QueueController;

@Configuration
public class Jersey extends ResourceConfig
{
    public Jersey() throws IOException {
        register( QueueController.class );
    }
}
