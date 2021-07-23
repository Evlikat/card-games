package io.evlikat.games.cardgames.ginrummy.server

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@Configuration
class JacksonConfig {
    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder? {
        return Jackson2ObjectMapperBuilder()
            .modules(KotlinModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
    }
}