package com.example.authorization.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "`authorizationConsent`")
@IdClass(AuthorizationConsent.AuthorizationConsentId.class)
public class AuthorizationConsent {
    @Id
    private String registeredClientId;
    @Id
    private String principalName;
    @Column(length = 1000)
    private String authorities;

    public static class AuthorizationConsentId implements Serializable {
        private String registeredClientId;
        private String principalName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthorizationConsentId that = (AuthorizationConsentId) o;
            return registeredClientId.equals(that.registeredClientId) && principalName.equals(that.principalName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(registeredClientId, principalName);
        }
    }
}
