package com.sangqi.VideoCatalogService.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

/**
 * Configure this web application to use OAuth 2.0.
 * <p>
 * The resource server is located at "/video", and can be accessed only by retrieving a token from "/oauth/token"
 * using the Password Grant Flow as specified by OAuth 2.0.
 * <p>
 * Most of this code can be reused in other applications. The key methods that would definitely need to
 * be changed are:
 * <p>
 * ResourceServer.configure(...) - update this method to apply the appropriate
 * set of scope requirements on client requests
 * <p>
 * OAuth2Config constructor - update this constructor to create a "real" (not hard-coded) UserDetailsService
 * and ClientDetailsService for authentication. The current implementation should never be used in any
 * type of production environment as these hard-coded credentials are highly insecure.
 * <p>
 * OAuth2SecurityConfiguration.containerCustomizer(...) - update this method to use a real keystore
 * and certificate signed by a CA. This current version is highly insecure.
 */
@Configuration
public class OAuth2SecurityConfiguration {

    // This first section of the configuration just makes sure that Spring Security picks
    // up the UserDetailsService that we create below.
    @Configuration
    @EnableWebSecurity
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    /**
     * This method is used to configure who is allowed to access which parts of our
     * resource server (i.e. the "/video" endpoint)
     */
    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        private static final String VIDEO_ID = "video";

        // This method configures the OAuth scopes required by clients to access
        // all of the paths in the video service.
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            http
                .authorizeRequests()
                .antMatchers("/oauth/token").anonymous();

            // Require all GET requests to have client "read" scope
            http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**")
                .access("#oauth2.hasScope('read')");

            // Require all other requests to have "write" scope
            http
                .authorizeRequests()
                .antMatchers("/**")
                .access("#oauth2.hasScope('write')");
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(VIDEO_ID);
        }
    }

    /**
     * This class is used to configure how our authorization server (the "/oauth/token" endpoint)
     * validates client credentials.
     */
    @Configuration
    @EnableAuthorizationServer
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class OAuth2Config extends
        AuthorizationServerConfigurerAdapter {

        // A data structure used to store both a ClientDetailsService and a UserDetailsService
        private final ClientAndUserDetailsService combinedService;
        // Delegate the processing of Authentication requests to the framework
        @Autowired
        private AuthenticationManager authenticationManager;

        public OAuth2Config() throws Exception {
            // Create a service that has the credentials for all our clients
            ClientDetailsService csvc = new InMemoryClientDetailsServiceBuilder()
                // Create a client that has "read" and "write" access to the
                // video service
                .withClient("mobile")
                .authorizedGrantTypes("password")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes("read", "write")
                .resourceIds("video")
                .and()
                // Create a second client that only has "read" access to the
                // video service
                .withClient("mobileReader")
                .authorizedGrantTypes("password")
                .authorities("ROLE_CLIENT")
                .scopes("read")
                .resourceIds("video")
                .accessTokenValiditySeconds(3600)
                .and()
                .build();

            // Create a series of hard-coded users.
            UserDetailsService svc = new InMemoryUserDetailsManager(
                Arrays.asList(
                    User.create("admin", "pass", "ADMIN", "USER"),
                    User.create("user0", "pass", "USER"),
                    User.create("user1", "pass", "USER"),
                    User.create("user2", "pass", "USER"),
                    User.create("user3", "pass", "USER"),
                    User.create("user4", "pass", "USER"),
                    User.create("user5", "pass", "USER")));

            // Since clients have to use BASIC authentication with the client's id/secret,
            // when sending a request for a password grant, we make each client a user
            // as well. When the BASIC authentication information is pulled from the
            // request, this combined UserDetailsService will authenticate that the
            // client is a valid "user".
            combinedService = new ClientAndUserDetailsService(csvc, svc);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }

        /**
         * Return the list of trusted client information to anyone who asks for it.
         */
        @Bean
        public ClientDetailsService clientDetailsService() throws Exception {
            return combinedService;
        }

        /**
         * Return all of our user information to anyone in the framework who requests it.
         */
        @Bean
        public UserDetailsService userDetailsService() {
            return combinedService;
        }

        /**
         * This method tells our AuthorizationServerConfigurerAdapter to use the delegated AuthenticationManager
         * to process authentication requests.
         */
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        /**
         * This method tells the AuthorizationServerConfigurerAdapter to use our self-defined client details service to
         * authenticate clients with.
         */
        @Override
        public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
            clients.withClientDetails(clientDetailsService());
        }
    }
}
