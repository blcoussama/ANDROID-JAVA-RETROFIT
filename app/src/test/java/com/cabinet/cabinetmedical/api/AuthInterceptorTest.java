package com.cabinet.cabinetmedical.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.cabinet.cabinetmedical.session.TokenStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class AuthInterceptorTest {

    private static final class FakeTokenStore implements TokenStore {
        String token = "jeton-valide";
        int clears;

        @Override
        public String getToken() {
            return token;
        }

        @Override
        public void clear() {
            token = null;
            clears++;
        }
    }

    private MockWebServer server;
    private FakeTokenStore store;
    private AtomicInteger expiredCalls;
    private OkHttpClient client;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        store = new FakeTokenStore();
        expiredCalls = new AtomicInteger();
        client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(store, expiredCalls::incrementAndGet))
                .build();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    private RecordedRequest call(String path, int status, String authorization) throws Exception {
        server.enqueue(new MockResponse().setResponseCode(status));
        Request.Builder request = new Request.Builder().url(server.url(path));
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        try (Response ignored = client.newCall(request.build()).execute()) {
            return server.takeRequest();
        }
    }

    @Test
    public void addsBearerTokenToApiRequests() throws Exception {
        RecordedRequest request = call("/api/appointments/me", 200, null);
        assertEquals("Bearer jeton-valide", request.getHeader("Authorization"));
        assertEquals(0, store.clears);
    }

    @Test
    public void neverSendsTokenToLoginOrRegister() throws Exception {
        assertNull(call("/api/auth/login", 200, null).getHeader("Authorization"));
        assertNull(call("/api/auth/register", 201, null).getHeader("Authorization"));
    }

    @Test
    public void unauthorizedResponseClearsSessionAndNotifies() throws Exception {
        call("/api/appointments/me", 401, null);
        assertEquals(1, store.clears);
        assertEquals(1, expiredCalls.get());
        assertNull(store.getToken());
    }

    @Test
    public void forbiddenResponseKeepsSession() throws Exception {
        call("/api/users", 403, null);
        assertEquals(0, store.clears);
        assertEquals(0, expiredCalls.get());
    }

    @Test
    public void wrongPasswordOnLoginDoesNotTriggerExpiry() throws Exception {
        call("/api/auth/login", 401, null);
        assertEquals(0, store.clears);
        assertEquals(0, expiredCalls.get());
    }

    @Test
    public void explicitAuthorizationHeaderIsLeftUntouched() throws Exception {
        RecordedRequest request = call("/api/auth/logout", 401, "Bearer ancien-jeton");
        assertEquals("Bearer ancien-jeton", request.getHeader("Authorization"));
        assertEquals(0, store.clears);
    }

    @Test
    public void noTokenMeansNoHeader() throws Exception {
        store.token = null;
        assertNull(call("/api/doctors", 200, null).getHeader("Authorization"));
    }
}
