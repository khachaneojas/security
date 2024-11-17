package com.sprk.commons.repository.mongo;

import com.sprk.commons.document.EntitlementModel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



public interface EntitlementAuthenticatorRepository extends MongoRepository<EntitlementModel, String> {
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    EntitlementModel findByUserUid(String userUid);
}