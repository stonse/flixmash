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

package com.flixmash.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.netflix.config.DynamicStringListProperty;
import com.netflix.karyon.spi.Component;

/**
 * @author Sudhir Tonse (stonse@netflix.com)
 */
@Component
public class FlixMoviesComponent {

	   
    //Configuration via Archaius
    private static final DynamicStringListProperty popularMovieList = new DynamicStringListProperty("flixmash.popularMovieList", (List<String>) null);
 
    
    private static final Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

    @PostConstruct
    public void initialize() {
        // TODO: Initialization logic, eg: connection to DB etc.
    }
    


	public List<String> getPopularMovies() {
		return popularMovieList.get();
	}
    

	public List<String> getUserMovieList(String userid){
		if (map.containsKey(userid)){
			return map.get(userid);
		}else{
			return new ArrayList<String>();
		}
	}

	public void addMovieToList(String userid, String movie){
		if (map.containsKey(userid)){
			map.get(userid).add(movie);
		}else{
			ArrayList<String> list = new ArrayList<String>();
			list.add(movie);
			map.put(userid, list);
		}
	}
	
	public Map<String, ArrayList<String>> getAllMovieLists(){
		return map;
	}

}
