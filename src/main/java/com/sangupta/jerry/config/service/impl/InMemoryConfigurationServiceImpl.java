/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012-2014, Sandeep Gupta
 * 
 * http://www.sangupta/projects/jerry
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.jerry.config.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sangupta.jerry.config.domain.Configuration;
import com.sangupta.jerry.config.service.ConfigurationService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * A {@link ConfigurationService} implementation that keeps all data in-memory.
 * The configuration is lost when the JVM is shutdown.
 * 
 * @author sangupta
 *
 */
public class InMemoryConfigurationServiceImpl extends BaseConfigurationServiceImpl {
	
	private static final ConcurrentMap<String, Configuration> CONFIG_MAP = new ConcurrentHashMap<String, Configuration>();

	@Override
	public boolean create(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		if(AssertUtils.isEmpty(configuration.getConfigKey())) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration older = CONFIG_MAP.putIfAbsent(configuration.getConfigKey(), configuration);
		if(older == null) {
			return true;
		}
		
		return false;
	}

	@Override
	public Configuration get(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		return CONFIG_MAP.get(key);
	}

	@Override
	public boolean update(String key, String value, boolean readOnly) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration current = get(key);
		if(current == null) {
			return false;
		}
		
		if(current.isReadOnly()) {
			return false;
		}
		
		current.setValue(value);
		current.setReadOnly(readOnly);
		return true;
	}

	@Override
	public boolean delete(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration config = CONFIG_MAP.remove(key);
		if(config != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<Configuration> getAllConfigurations() {
		return new ArrayList<Configuration>(CONFIG_MAP.values());
	}

}
