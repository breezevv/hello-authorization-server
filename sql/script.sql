CREATE TABLE client (
                        id varchar(255) NOT NULL,
                        clientId varchar(255) NOT NULL,
                        clientIdIssuedAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        clientSecret varchar(255) DEFAULT NULL,
                        clientSecretExpiresAt timestamp DEFAULT NULL,
                        clientName varchar(255) NOT NULL,
                        clientAuthenticationMethods varchar(1000) NOT NULL,
                        authorizationGrantTypes varchar(1000) NOT NULL,
                        redirectUris varchar(1000) DEFAULT NULL,
                        scopes varchar(1000) NOT NULL,
                        clientSettings varchar(2000) NOT NULL,
                        tokenSettings varchar(2000) NOT NULL,
                        PRIMARY KEY (id)
);


CREATE TABLE authorization (
                               id varchar(255) NOT NULL,
                               registeredClientId varchar(255) NOT NULL,
                               principalName varchar(255) NOT NULL,
                               authorizationGrantType varchar(255) NOT NULL,
                               `attributes` text DEFAULT NULL,
                               state varchar(500) DEFAULT NULL,
                               authorizationCodeValue text DEFAULT NULL,
                               authorizationCodeIssuedAt timestamp DEFAULT NULL,
                               authorizationCodeExpiresAt timestamp DEFAULT NULL,
                               authorizationCodeMetadata text DEFAULT NULL,
                               accessTokenValue text DEFAULT NULL,
                               accessTokenIssuedAt timestamp DEFAULT NULL,
                               accessTokenExpiresAt timestamp DEFAULT NULL,
                               accessTokenMetadata text DEFAULT NULL,
                               accessTokenType varchar(255) DEFAULT NULL,
                               accessTokenScopes text DEFAULT NULL,
                               refreshTokenValue text DEFAULT NULL,
                               refreshTokenIssuedAt timestamp DEFAULT NULL,
                               refreshTokenExpiresAt timestamp DEFAULT NULL,
                               refreshTokenMetadata text DEFAULT NULL,
                               oidcIdTokenValue text DEFAULT NULL,
                               oidcIdTokenIssuedAt timestamp DEFAULT NULL,
                               oidcIdTokenExpiresAt timestamp DEFAULT NULL,
                               oidcIdTokenMetadata text DEFAULT NULL,
                               oidcIdTokenClaims text DEFAULT NULL,
                               PRIMARY KEY (id)
);

CREATE TABLE authorizationConsent (
                                      registeredClientId varchar(255) NOT NULL,
                                      principalName varchar(255) NOT NULL,
                                      authorities text NOT NULL,
                                      PRIMARY KEY (registeredClientId, principalName)
);
