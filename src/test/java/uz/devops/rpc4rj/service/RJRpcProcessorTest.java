package uz.devops.rpc4rj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import uz.devops.rpc4rj.IntegrationTest;
import uz.devops.rpc4rj.dummy.JsonRpcDummyApiOne;
import uz.devops.rpc4rj.dummy.JsonRpcDummyApiThree;
import uz.devops.rpc4rj.dummy.JsonRpcDummyApiTwo;
import uz.devops.rpc4rj.dummy.model.*;
import uz.devops.rpc4rj.error.exception.InvalidParamsException;
import uz.devops.rpc4rj.error.exception.JsonRpcVersionValidationException;
import uz.devops.rpc4rj.error.exception.MethodNotFoundException;
import uz.devops.rpc4rj.model.JsonRpcError;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;

import javax.validation.ConstraintViolationException;

@Slf4j
@IntegrationTest
@AutoConfigureWebTestClient(timeout = "600000")
class RJRpcProcessorTest {

    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_STREAM_JSON;
    public static final String JSONRPC = "2.0";
    public static final long MONO_REQUEST_ID = 1L;
    public static final long FLUX_REQUEST_ID = 2L;
    public static final String FLUX_DATA_1 = "FLUX1";
    public static final String FLUX_DATA_2 = "FLUX2";
    public static final String MONO_DATA_1 = "1";
    public static final String MONO_DATA_2 = "2";
    public static final String URI_API_1 = JsonRpcDummyApiOne.URI;
    public static final String URI_API_2 = JsonRpcDummyApiTwo.URI;
    public static final String WRONG_RPC_VERSION = "1";
    public static final String URI_API_3 = "/api/rpc/dummy/three";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsonRpcDummyApiThree apiThree;

    @Test
    void monoMethodSuccessApiOneTest() {
        log.debug("monoMethodTest started");

        var param = new ApiOneRequest(new DummyRequestOne(MONO_DATA_1), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var resultParam = objectMapper.convertValue(result.getResult(), DummyResponse.class);
        Assertions.assertEquals(param.getRequestOne().getData() + param.getRequestTwo().getData(), resultParam.getRequestData());
        Assertions.assertEquals("SUCCESS", resultParam.getResult());
    }

    @Test
    void fluxMethodSuccessApiOneTest() {
        log.debug("fluxMethodTest started");

        var param = new ApiOneRequest(new DummyRequestOne(FLUX_DATA_1), new DummyRequestTwo(FLUX_DATA_2));
        var request = new JsonRpcRequest(FLUX_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_FLUX, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody();

        StepVerifier
                .create(result)
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("0");
                        }
                )
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("1");
                        }
                )
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("2");
                        }
                )
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("3");
                        }
                )
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("4");
                        }
                )
                .verifyComplete();
    }

    @Test
    void methodNotFoundErrorApiOneTest() {
        log.debug("methodNotFoundErrorTest started");

        var param = new ApiOneRequest(new DummyRequestOne(MONO_DATA_1), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, "NONE_METHOD", param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var error = objectMapper.convertValue(result.getError(), JsonRpcError.class);
        Assertions.assertEquals(MethodNotFoundException.class.getSimpleName(), error.getData());
        Assertions.assertNotNull(error.getMessage());
        Assertions.assertEquals(-32601, error.getCode());
    }

    @Test
    void invalidParamsErrorApiOneTest() {
        log.debug("invalidParamsErrorTest started");

        var param = new DummyRequestOne(MONO_DATA_1);
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var error = objectMapper.convertValue(result.getError(), JsonRpcError.class);
        Assertions.assertEquals(InvalidParamsException.class.getSimpleName(), error.getData());
        Assertions.assertNotNull(error.getMessage());
        Assertions.assertEquals(-32602, error.getCode());
    }

    @Test
    void argsValidationErrorApiOneTest() {
        log.debug("argsValidationErrorTest started");

        var param = new ApiOneRequest(new DummyRequestOne(null), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var error = objectMapper.convertValue(result.getError(), JsonRpcError.class);
        Assertions.assertEquals(ConstraintViolationException.class.getSimpleName(), error.getData());
        Assertions.assertNotNull(error.getMessage());
        Assertions.assertEquals(-32600, error.getCode());
    }

    @Test
    void userDefinedErrorApiOneTest() {
        log.debug("userDefinedErrorApiOneTest started");

        var param = new ApiOneRequest(new DummyRequestOne(MONO_DATA_1), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_MONO_CUSTOM_ERROR, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var error = objectMapper.convertValue(result.getError(), JsonRpcError.class);
        Assertions.assertEquals(JsonRpcDummyApiOne.DUMMY_DATA, error.getData());
        Assertions.assertEquals(JsonRpcDummyApiOne.DUMMY_MESSAGE, error.getMessage());
        Assertions.assertEquals(JsonRpcDummyApiOne.DUMMY_ERROR_CODE, error.getCode());
    }

    @Test
    void versionValidationErrorApiOneTest() {
        log.debug("versionValidationErrorApiOneTest started");

        var param = new ApiOneRequest(new DummyRequestOne(null), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, WRONG_RPC_VERSION, JsonRpcDummyApiOne.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_1)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(MONO_REQUEST_ID, result.getId());

        var error = objectMapper.convertValue(result.getError(), JsonRpcError.class);
        Assertions.assertEquals(JsonRpcVersionValidationException.class.getSimpleName(), error.getData());
        Assertions.assertNotNull(error.getMessage());
        Assertions.assertEquals(-32600, error.getCode());
    }

    @Test
    void monoMethodSuccessApiTwoTest() {
        log.debug("monoMethodTest started");

        var param = new ApiTwoRequest(new DummyRequestOne(FLUX_DATA_1));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiTwo.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_2)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var resultParam = objectMapper.convertValue(result.getResult(), DummyResponse.class);
        Assertions.assertEquals(param.getRequestOne().getData(), resultParam.getRequestData());
        Assertions.assertEquals("SUCCESS", resultParam.getResult());
    }

    @Test
    void fluxMethodSuccessApiTwoTest() {
        log.debug("fluxMethodTest started");

        var param = new ApiTwoRequest(new DummyRequestOne(FLUX_DATA_1));
        var request = new JsonRpcRequest(FLUX_REQUEST_ID, JSONRPC, JsonRpcDummyApiTwo.METHOD_DUMMY_FLUX, param);

        var result = webTestClient
                .post()
                .uri(URI_API_2)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody();

        StepVerifier
                .create(result)
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("0");
                        }
                )
                .expectNextMatches(
                        jsonRpcResponse -> {
                            assertBaseParams(jsonRpcResponse, FLUX_REQUEST_ID);
                            var response = objectMapper.convertValue(jsonRpcResponse.getResult(), DummyResponse.class);
                            return response.getResult().equals("1");
                        }
                )
                .verifyComplete();
    }

    @Test
    void monoMethodSuccessApiThreeTest() {
        log.debug("monoMethodSuccessApiThreeTest started");

        var param = new ApiOneRequest(new DummyRequestOne(MONO_DATA_1), new DummyRequestTwo(MONO_DATA_2));
        var request = new JsonRpcRequest(MONO_REQUEST_ID, JSONRPC, JsonRpcDummyApiOne.METHOD_DUMMY_MONO, param);

        var result = webTestClient
                .post()
                .uri(URI_API_3)
                .accept(MEDIA_TYPE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MEDIA_TYPE)
                .returnResult(JsonRpcResponse.class)
                .getResponseBody()
                .next()
                .block();

        Assertions.assertNotNull(result);
        assertBaseParams(result, MONO_REQUEST_ID);

        var resultParam = objectMapper.convertValue(result.getResult(), DummyResponse.class);
        Assertions.assertEquals(param.getRequestOne().getData() + param.getRequestTwo().getData(), resultParam.getRequestData());
        Assertions.assertEquals("SUCCESS", resultParam.getResult());
    }

    private void assertBaseParams(JsonRpcResponse response, Long requestId) {
        Assertions.assertEquals(requestId, response.getId());
        Assertions.assertEquals(JSONRPC, response.getJsonrpc());
    }
}
