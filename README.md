# Reactive JSON RPC 2.0 plugin for the [Spring Boot](https://spring.io/projects/spring-boot).

[This](https://github.com/Israiloff/rpc4rj) project aims to simplify web exchange via 
[JSON RPC 2.0](https://www.jsonrpc.org/specification) protocol in reactive manner. Names of the components and their 
usage are very similar to [JSON-RPC for Java (jsonrpc4j)](https://github.com/briandilley/jsonrpc4j) project 
(thanks a lot to your work folks, your project is awesome!). The main difference between 
the projects is that [this project](https://github.com/Israiloff/rpc4rj) is completely reactive, i.e. built on 
[project reactor](https://projectreactor.io/) aka [webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html) 
for the [Spring Boot](https://spring.io/projects/spring-boot).

## Usage

1. Add the following dependency to your [pom.xml](https://maven.apache.org/pom.html)

```xml
<dependency>
    <groupId>io.github.israiloff</groupId>
    <artifactId>rpc4rj</artifactId>
    <version>VERSION</version>
</dependency>
```

> **VERSION** is the latest version of the [***RPC4RJ***](https://github.com/Israiloff/broker) plugin.

2. Mark the target service with **@RJRpcService** annotation.

```java
@RJRpcService(PATH_TO_API)
interface FooService{
    
}
```

> **PATH_TO_API** is your web path to API (e.g. if the value of the path is ***"/path/to/api"*** then the target url will be 
***http://YOUR_HOST_ADDRESS/path/to/api***).

3. Mark the target methods with @RJRpcMethod and method's arguments with @RJRpcParam annotations.

```java
@RJRpcService(PATH_TO_API)
interface FooService{
    @RJRpcMethod(METHOD_NAME)
    Mono<FooResult> singleResultMethod(@RJRpcParam(ARG_NAME) FooRequest request);
}
```

> **METHOD_NAME** is the name of the target method (e.g. ***"dummyMethod"***).
<!-- -->
> **ARG_NAME** is the name of the method's argument (e.g. ***"dummyArg"***).
<!-- -->
> Note that return type can be wrapped with ***Flux<>*** type. If so, you will get results one by one wrapped into json RPC result wrapper.\

4. Declare errors.

```java
@RJRpcService(PATH_TO_API)
interface FooService {

    @RJRpcMethod(METHOD_NAME)
    @RJRpcErrors({
            @RJRpcError(exception = FooException.class, code = EX_CODE, message = MSG, data = DATA)
    })
    Mono<FooResult> singleResultMethod(@RJRpcParam(ARG_NAME) FooRequest request);
}
```

> **FooException.class** - target exception class, **EX_CODE** - error code to return, **MSG** - error message to return, 
> **DATA** - data to return.
