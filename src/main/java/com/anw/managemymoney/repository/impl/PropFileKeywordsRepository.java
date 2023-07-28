package com.anw.managemymoney.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Repository;
import com.anw.managemymoney.repository.KeywordsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class PropFileKeywordsRepository implements KeywordsRepository {

	private final Environment environment;
	
	private Map<String, String> keywordsMap;

    @Autowired
    public PropFileKeywordsRepository(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void loadKeywordsMap() {
        keywordsMap = new HashMap<>();
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        PropertySource<?> propertySource = configurableEnvironment.getPropertySources().get("Config resource 'class path resource [application.properties]' via location 'optional:classpath:/'");
        if (propertySource != null) {
            Map<?, ?> source = (Map<?, ?>) propertySource.getSource();
            for (Map.Entry<?, ?> entry : source.entrySet()) {
                String propertyName = entry.getKey().toString();
                if (propertyName.startsWith("keywords.")) {
                    String category = propertyName.substring("keywords.".length());
                    String keywords = entry.getValue().toString();
                    keywordsMap.put(category, keywords);
                }
            }
            log.info("Keywords map loaded - {}", keywordsMap);
        } else {
            log.error("Failed to load keywords from application.properties. Property source not found.");
        }
    }
    
	@Override
	public Map<String, String> getKeywordsMap(String user) {
		return keywordsMap;
	}

}
