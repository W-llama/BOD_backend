package com.bod.bod.verification.repository;

import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties.AssertingParty.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface verificationRepository extends JpaRepository<Verification,Long> {

}
