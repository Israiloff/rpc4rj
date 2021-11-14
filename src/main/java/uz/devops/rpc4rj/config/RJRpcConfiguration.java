package uz.devops.rpc4rj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RJRpcConfiguration {

    public static final String OBJECT_MAPPER_BEAN_NAME = "objectMapperBeanName";

    @Bean(OBJECT_MAPPER_BEAN_NAME)
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        return objectMapper;
    }
}
