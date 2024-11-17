package com.sprk.commons.repository.primary;

import com.sprk.commons.dto.UserAuthenticatorR;
import com.sprk.commons.entity.primary.user.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface UserAuthenticatorRepository extends JpaRepository<UserModel, Long> {
	@Query("SELECT NEW com.sprk.commons.dto.UserAuthenticatorR(" +
			"user.userPid, " +
			"user.userUid, " +
			"user.tokenAt, " +
			"user.enabled, " +
			"user.organization IS NOT NULL " +
			") FROM UserModel user " +
			"WHERE user.userUid = :userUid")
	UserAuthenticatorR findByUserUid(@Param("userUid") String userUid);
}
