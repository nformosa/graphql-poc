package com.nickfish.graphqlpoc;

import graphql.Scalars;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.nickfish.graphqlpoc")
public class GraphqlPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlPocApplication.class, args);
    }


}
