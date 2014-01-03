/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.flixmash.server;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;

/**
 * JSR-311 Resource class to set dynamic properties (Runtime)
 * @author Sudhir Tonse (stonse@netflix.com)
 */
@Path("/property")
public class PropertyResource {

    private static final Logger logger = LoggerFactory.getLogger(PropertyResource.class);
    
  
    
    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response setProperty(final @QueryParam("id") String id, final @QueryParam("value") String value) {
        try {
        	((ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance()).setOverrideProperty(id, value);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error("Error while setting property", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}