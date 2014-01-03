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


import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flixmash.core.FlixMoviesComponent;
import com.google.inject.Inject;
import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.StatsTimer;
import com.netflix.servo.monitor.Stopwatch;
import com.netflix.servo.stats.StatsConfig;

/**
 * A JSR-311 Resource that handles REST calls
 * @author Sudhir Tonse (stonse@netflix.com)
 *
 *
 */
@Path("/movies")
public class FlixmashResource {

    private static final Logger logger = LoggerFactory.getLogger(FlixmashResource.class);
    
    // Metrics via Servo
    // JMX:  com.netflix.servo.COUNTER.FlixMash.getMovies
    private static final Counter getMoviesCounter = new BasicCounter(MonitorConfig.builder("FlixMash.getMoviesCounter").build());
    private static final Counter getMoviesErrorCounter = new BasicCounter(MonitorConfig.builder("FlixMash.getMoviesErrorCounter").build());
    private static final Counter getUserMovieListCounter = new BasicCounter(MonitorConfig.builder("FlixMash.getUserMovieListCounter").build());
    private static final Counter getUserMovieListErrorCounter = new BasicCounter(MonitorConfig.builder("FlixMash.getUserMovieListErrorCounter").build());
    private static final Counter addMovieCounter = new BasicCounter(MonitorConfig.builder("FlixMash.addMovieCounter").build());
    private static final Counter addMovieErrorCounter = new BasicCounter(MonitorConfig.builder("FlixMash.addMovieErrorCounter").build());
    private static final Counter userMovieListsCounter = new BasicCounter(MonitorConfig.builder("FlixMash.userMovieListsCounter").build());
    private static final Counter userMovieListsErrorCounter = new BasicCounter(MonitorConfig.builder("FlixMash.userMovieListsCounter").build());
       

    // JMX:  com.netflix.servo.FlixMash.getMovies_* (95th and 99th percentile)
    private static final StatsTimer getMoviesTimer = new StatsTimer(MonitorConfig.builder("FlixMash.getMoviesTimer").build(), 
    		new StatsConfig.Builder().build());
 
    //Register our Servo Metrics
    static {
        DefaultMonitorRegistry.getInstance().register(getMoviesCounter);
        DefaultMonitorRegistry.getInstance().register(getMoviesErrorCounter);
        DefaultMonitorRegistry.getInstance().register(getUserMovieListCounter);
        DefaultMonitorRegistry.getInstance().register(getUserMovieListErrorCounter);
        DefaultMonitorRegistry.getInstance().register(addMovieCounter);
        DefaultMonitorRegistry.getInstance().register(addMovieErrorCounter);
        DefaultMonitorRegistry.getInstance().register(userMovieListsCounter);
        DefaultMonitorRegistry.getInstance().register(userMovieListsErrorCounter);
        DefaultMonitorRegistry.getInstance().register(getMoviesTimer);
    }
    
    // Component that deals with Movies; will be injected by the container
    private final FlixMoviesComponent flixMovies; 

    @Inject
    public FlixmashResource(FlixMoviesComponent component) {
        this.flixMovies = component; // object creation via Injection
    }
    
    //Random object to sleep
    Random rand = new Random();
  
    @GET
    @Path("popularMovies")
    @Produces({MediaType.APPLICATION_JSON})
    public Response movies() {
    	// Start timer
        Stopwatch stopwatch = getMoviesTimer.start();

        JSONObject response = new JSONObject();
        try {
        	getMoviesCounter.increment();
        	Thread.sleep(rand.nextInt(100));
            response.put("Movies", flixMovies.getPopularMovies()); // obtain movies from our flixMovies component
            return Response.ok(response.toString()).build();
        } catch (JSONException e) {
        	getMoviesErrorCounter.increment();
            logger.error("Error creating json response.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
        	logger.error("Error while sleeping", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
        	stopwatch.stop();
        }
    }

 
    @GET
    @Path("userMovieList/{userid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response userMovieList(final @PathParam("userid") String userid) {
        JSONObject response = new JSONObject();
        try {
        	getUserMovieListCounter.increment();
            response.put("userMovieList", flixMovies.getUserMovieList(userid)); // obtain movies from our flixMovies component
            return Response.ok(response.toString()).build();
        } catch (JSONException e) {
        	getUserMovieListErrorCounter.increment();
            logger.error("Error creating json response.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @POST
    @Path("userMovieList/{userid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response addMovie (
            final @PathParam("userid") String userid,
            final @QueryParam("movie") String movie) {
        try {
        	addMovieCounter.increment();
        	flixMovies.addMovieToList(userid, movie);
            return Response.ok().build();
        } catch (Exception e) {
        	addMovieErrorCounter.increment();
            logger.error("Error creating response.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GET
    @Path("userMovieLists")
    @Produces({MediaType.APPLICATION_JSON})
    public Response userMovieLists() {
        JSONObject response = new JSONObject();
        try {
        	userMovieListsCounter.increment();
            response.put("userMovieLists", flixMovies.getAllMovieLists()); // obtain movies from our flixMovies component
            return Response.ok(response.toString()).build();
        } catch (JSONException e) {
        	userMovieListsErrorCounter.increment();
            logger.error("Error creating json response.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}