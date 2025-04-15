# Microservices Project: Service Discovery, API Gateway, Routing & Load Balancing

This project demonstrates a fundamental microservices architecture using Spring Boot and Spring Cloud. It focuses on implementing:

* *Centralized Access & Routing:* Using *Spring Cloud Gateway* as an API Gateway to provide a single entry point for all client requests and route them to appropriate backend services based on path rules defined in its configuration.
* *Service Discovery:* Using *Spring Cloud Netflix Eureka* as a service registry, allowing services like the API Gateway and Example Service to dynamically find each other via the Eureka server address specified in their configurations.
* *Load Balancing:* Utilizing *Spring Cloud LoadBalancer* integrated with the Gateway and Eureka to distribute incoming requests across multiple running instances of a registered service (like example-service).

## Project Structure

The project (micro_project/service-discovery) contains three core modules:

1.  **eureka-server:** The Service Discovery server (Registry). Runs on port 8761. Configured not to register itself. Includes spring-cloud-starter-netflix-eureka-server dependency and enabled via @EnableEurekaServer.
2.  **api-gateway:** The API Gateway, providing centralized access and routing. Runs on port 8080. Uses spring-cloud-starter-gateway, spring-cloud-starter-netflix-eureka-client, and spring-cloud-starter-loadbalancer. Routes requests for /example/** to load-balanced (lb://) instances of EXAMPLE-SERVICE discovered via Eureka.
3.  **example-service:** A sample backend microservice. Default port is 8081. Includes spring-boot-starter-web and spring-cloud-starter-netflix-eureka-client. Exposes a simple GET /example endpoint. *(Note: Assumes modification to include the server port in the response message for easier load balancing observation)*.

## Prerequisites

* Java Development Kit (JDK) 11 or later
* Apache Maven 3.6 or later
* Git (Optional, for cloning)

## Building the Project

1.  Clone the repository (if applicable) and navigate to the project's root directory:
    bash
    # Example:
    # git clone <your-repository-url>
    cd micro_project/service-discovery
    
2.  Build all modules using Maven. This compiles the code and creates executable JAR files in the target directory of each module:
    bash
    mvn clean package
    

## Running the Applications

Start the services in the following order in *separate terminal windows*. This order is crucial for successful service registration and discovery.

1.  *Start Eureka Server:*
    * Ensures the service registry is available first.
    bash
    cd eureka-server
    java -jar target/eureka-server-*.jar
    # (Check console for startup on port 8761)
    

2.  *Start Example Service Instances (for Load Balancing):*
    * Start at least two instances on different ports. They will register with Eureka.
    * *Instance 1 (Default Port):*
        bash
        cd ../example-service
        java -jar target/example-service-*.jar
        # (Check console for startup on port 8081)
        
    * *Instance 2 (Different Port):*
        (Open a new terminal)
        bash
        cd example-service # Or navigate to the directory if not already there
        java -jar target/example-service-*.jar --server.port=8082
        # (Check console for startup on port 8082)
        
    * You can start more instances on other ports (e.g., 8083) if desired.

3.  *Start API Gateway:*
    * Starts last, discovers registered services from Eureka, and opens the central access point.
    (Open a new terminal)
    bash
    cd ../api-gateway
    java -jar target/api-gateway-*.jar
    # (Check console for startup on port 8080)
    

## Testing the Features

1.  *Verify Service Discovery:*
    * Open your web browser and navigate to the Eureka dashboard: http://localhost:8761.
    * Wait up to 30 seconds. Under "Instances currently registered with Eureka", you should see:
        * API-GATEWAY (1 instance).
        * EXAMPLE-SERVICE (2 or more instances, depending on how many you started, listed with their respective ports like 8081, 8082).

2.  *Test Centralized Access & Routing:*
    * Send a request to the example-service through the API Gateway's address (localhost:8080) and the configured route path (/example).
    * Use curl or your browser:
        bash
        curl http://localhost:8080/example
        
    * You should receive a response originating from one of the example-service instances (e.g., "Hello from Example Service on port 8081!"). This confirms the gateway is routing correctly.

3.  *Test Load Balancing:*
    * Make multiple consecutive requests to the same gateway endpoint:
        bash
        curl http://localhost:8080/example
        curl http://localhost:8080/example
        curl http://localhost:8080/example
        curl http://localhost:8080/example
        
    * Observe the responses. Since you've modified the service to include the port, you should see the port number changing in the output (e.g., alternating between 8081 and 8082), demonstrating that the API Gateway is distributing the requests across the available instances using round-robin load balancing by default.
    * Example expected output pattern:
        
        Hello from Example Service on port 8081!
        Hello from Example Service on port 8082!
        Hello from Example Service on port 8081!
        Hello from Example Service on port 8082!
        ...
        

## Stopping the Applications

Stop each application by pressing Ctrl + C in its respective terminal window. It's good practice to stop them in the reverse order they were started (Gateway -> Example Services -> Eureka Server).