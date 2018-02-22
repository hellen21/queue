package com.snap.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path( "/v1/queue" )
@Consumes( MediaType.APPLICATION_JSON )
@Produces( MediaType.APPLICATION_JSON )
public class QueueController
{
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private String jsonResponse;

    @GET
    public Response getMessageRabbit() throws IOException
    {
        try
        {
            return Response.status( HttpServletResponse.SC_OK ).entity( jsonResponse ).build();
        }
        catch ( Exception e )
        {

            return Response.status( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ).entity( jsonResponse ).build();
        }
    }
    
    @POST
    @Path("{message}")
    public Response createMessageRabbit(@PathParam("message") String message) throws IOException
    {
        try
        {
            rabbitTemplate.convertAndSend(message);
            return Response.status( HttpServletResponse.SC_OK ).entity( jsonResponse ).build();
        }
        catch ( Exception e )
        {

            return Response.status( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ).entity( jsonResponse ).build();
        }
    }
}
